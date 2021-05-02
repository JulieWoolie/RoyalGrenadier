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
import net.forthecrown.royalgrenadier.RoyalArgumentRegistry;
import net.forthecrown.royalgrenadier.source.CommandSources;
import net.minecraft.server.v1_16_R3.ArgumentRegistry;
import net.minecraft.server.v1_16_R3.CommandListenerWrapper;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Converts commands from the CommandSource dispatcher to NMS commands.
 * <p>Honestly, if there was some other way I could get the chat to display arguments in the correct color in chat, I would use it</p>
 */
public class WrapperConverter {

    private final CommandWrapper wrapper;
    private final BrigadierCommand command;
    private final AbstractCommand abstractCommand;
    private final LiteralArgumentBuilder<CommandListenerWrapper> nms;
    private final LiteralCommandNode<CommandSource> regged;

    public WrapperConverter(CommandWrapper wrapper, AbstractCommand command, LiteralCommandNode<CommandSource> toConvert){
        this.abstractCommand = command;
        this.wrapper = wrapper;
        this.regged = toConvert;
        this.command = command.getRoot();
        nms = literal(command.getName());

        start();
    }

    public LiteralArgumentBuilder<CommandListenerWrapper> finish(){
        return nms;
    }

    void start(){
        nms.requires(wrapper);
        if(regged.getCommand() != null) nms.executes(wrapper);
        if(regged.getChildren() != null && regged.getChildren().size() > 0){
            convertNodes(command.getArguments(), nms);
        }
    }

    Predicate<CommandListenerWrapper> convertTest(CommandNode<CommandSource> node){
        return lis -> node.getRequirement().test(CommandSources.getOrCreate(lis, abstractCommand));
    }

    void convertNodes(Collection<CommandNode<CommandSource>> nodes, ArgumentBuilder<CommandListenerWrapper, ?> to){
        for (CommandNode<CommandSource> n: nodes){

            if(n instanceof LiteralCommandNode){
                to.then(convertNode((LiteralCommandNode<CommandSource>) n));
            } else if(n instanceof ArgumentCommandNode) {
                ArgumentCommandNode<CommandSource, ?> n1 = (ArgumentCommandNode<CommandSource, ?>) n;
                to.then(convertNode(n1));
            } else throw new IllegalStateException("Unknown Argument type: " + n.getClass().getName());
        }
    }

    private LiteralArgumentBuilder<CommandListenerWrapper> literal(String name){
        return LiteralArgumentBuilder.literal(name);
    }

    private RequiredArgumentBuilder<CommandListenerWrapper, ?> required(String name, ArgumentType<?> type){
        return RequiredArgumentBuilder.argument(name, type);
    }

    LiteralArgumentBuilder<CommandListenerWrapper> convertNode(LiteralCommandNode<CommandSource> node){
        LiteralArgumentBuilder<CommandListenerWrapper> result = literal(node.getLiteral());

        if(node.getCommand() != null) result.executes(wrapper);
        if(node.getRequirement() != null) result.requires(convertTest(node));

        if(node.getChildren() != null && node.getChildren().size() > 0){
            convertNodes(node.getChildren(), result);
        }

        return result;
    }

    RequiredArgumentBuilder<CommandListenerWrapper, ?> convertNode(ArgumentCommandNode<CommandSource, ?> node){
        ArgumentType<?> type = node.getType();

        //If it's not a vanilla argument type
        if(!ArgumentRegistry.a(type)) type = convertUnknownType(type);

        RequiredArgumentBuilder<CommandListenerWrapper, ?> result = required(node.getName(), type);
        if(node.getCommand() != null) result.executes(wrapper);

        if(node.getRequirement() != null) result.requires(convertTest(node));

        if(node.getCustomSuggestions() != null) result.suggests((c, b) -> wrapper.getSuggestions(c, b, node));
        else if(!RoyalArgumentRegistry.shouldUseVanillaSuggestions(node.getType())) result.suggests((c, b) -> node.getType().listSuggestions(c, b));

        if(node.getChildren() != null && node.getChildren().size() > 0){
            convertNodes(node.getChildren(), result);
        }

        return result;
    }

    ArgumentType<?> convertUnknownType(ArgumentType<?> type){
        return RoyalArgumentRegistry.getNMS(type);
    }
}
