package net.forthecrown.royalgrenadier.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.grenadier.command.BrigadierCommand;
import net.forthecrown.royalgrenadier.arguments.RoyalArgumentsImpl;
import net.forthecrown.royalgrenadier.source.CommandSources;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Converts commands from the CommandSource dispatcher to NMS commands.
 * <p>Honestly, if there was some other way I could get the chat to display arguments in the correct color in chat, I would use it</p>
 * <p>I don't wanna resort to using the default command system as a base, as much as that would help the pain I'm in lmao</p>
 */
public class WrapperConverter {

    private final CommandWrapper wrapper;
    private final BrigadierCommand command;
    private final AbstractCommand abstractCommand;
    private final LiteralArgumentBuilder<CommandSourceStack> nms;
    private final LiteralCommandNode<CommandSource> regged;

    public WrapperConverter(CommandWrapper wrapper, AbstractCommand command, LiteralCommandNode<CommandSource> toConvert){
        this.abstractCommand = command;
        this.wrapper = wrapper;
        this.regged = toConvert;
        this.command = command.getCommand();
        nms = literal(command.getName());

        start();
    }

    public LiteralArgumentBuilder<CommandSourceStack> finish(){
        return nms;
    }

    void start(){
        nms.requires(wrapper);
        if(regged.getCommand() != null) nms.executes(wrapper);
        if(regged.getChildren() != null && regged.getChildren().size() > 0){
            convertNodes(command.getArguments(), nms);
        }
    }

    Predicate<CommandSourceStack> convertTest(CommandNode<CommandSource> node){
        return lis -> node.getRequirement().test(CommandSources.getOrCreate(lis, abstractCommand));
    }

    void convertNodes(Collection<CommandNode<CommandSource>> nodes, ArgumentBuilder<CommandSourceStack, ?> to){
        for (CommandNode<CommandSource> n: nodes){

            if(n instanceof LiteralCommandNode){
                to.then(convertNode((LiteralCommandNode<CommandSource>) n));
            } else if(n instanceof ArgumentCommandNode) {
                ArgumentCommandNode<CommandSource, ?> n1 = (ArgumentCommandNode<CommandSource, ?>) n;
                to.then(convertNode(n1));
            } else throw new IllegalStateException("Unknown node: " + n.getClass().getName());
        }
    }

    private LiteralArgumentBuilder<CommandSourceStack> literal(String name){
        return Commands.literal(name);
    }

    private RequiredArgumentBuilder<CommandSourceStack, ?> required(String name, ArgumentType<?> type){
        return Commands.argument(name, type);
    }

    LiteralArgumentBuilder<CommandSourceStack> convertNode(LiteralCommandNode<CommandSource> node){
        LiteralArgumentBuilder<CommandSourceStack> result = literal(node.getLiteral());

        if(node.getCommand() != null) result.executes(wrapper);
        if(node.getRequirement() != null) result.requires(convertTest(node));

        if(node.getChildren() != null && node.getChildren().size() > 0){
            convertNodes(node.getChildren(), result);
        }

        return result;
    }

    RequiredArgumentBuilder<CommandSourceStack, ?> convertNode(ArgumentCommandNode<CommandSource, ?> node){
        ArgumentType<?> type = RoyalArgumentsImpl.getNMS(node.getType());

        RequiredArgumentBuilder<CommandSourceStack, ?> result = required(node.getName(), type);

        if(node.getCommand() != null) result.executes(wrapper);
        if(node.getRequirement() != null) result.requires(convertTest(node));

        //Here's that hacky af usage of that getSuggestions method in CommandWrapper
        result.suggests((c, b) -> wrapper.getSuggestions(c, b, node));
        if(node.getCustomSuggestions() == null && RoyalArgumentsImpl.shouldUseVanillaSuggestions(node.getType())) {
            result.suggests(null);
        }

        if(node.getChildren() != null && node.getChildren().size() > 0){
            convertNodes(node.getChildren(), result);
        }

        return result;
    }
}
