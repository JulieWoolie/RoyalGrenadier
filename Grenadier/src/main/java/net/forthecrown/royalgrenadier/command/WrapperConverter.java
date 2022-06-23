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
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import org.apache.commons.lang3.Validate;

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
        return lis -> node.getRequirement().test(GrenadierUtils.wrap(lis, abstractCommand));
    }

    void convertNodes(Collection<CommandNode<CommandSource>> nodes, ArgumentBuilder<CommandSourceStack, ?> to){
        for (CommandNode<CommandSource> n: nodes){

            if(n instanceof LiteralCommandNode literal){
                to.then(convertNode(literal));
            } else if(n instanceof ArgumentCommandNode arg) {
                to.then(convertNode(arg));
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
        ArgumentType<?> type = convertType(node.getType());
        boolean vanillaSuggests = useVanillaSuggestions(node.getType());

        RequiredArgumentBuilder<CommandSourceStack, ?> result = required(node.getName(), type);

        if(node.getCommand() != null) result.executes(wrapper);
        if(node.getRequirement() != null) result.requires(convertTest(node));

        //Here's that hacky af usage of that getSuggestions method in CommandWrapper
        result.suggests((c, b) -> wrapper.getSuggestions(c, b, node));
        if(node.getCustomSuggestions() == null && vanillaSuggests) {
            result.suggests(null);
        }

        if(node.getChildren() != null && node.getChildren().size() > 0){
            convertNodes(node.getChildren(), result);
        }

        return result;
    }

    boolean useVanillaSuggestions(ArgumentType type) {
        if (type instanceof VanillaMappedArgument vType) {
            return vType.useVanillaSuggestions();
        }

        return false;
    }

    ArgumentType convertType(ArgumentType type) {
        if (ArgumentTypeInfos.isClassRecognized(type.getClass())) {
            return type;
        }

        if (type instanceof VanillaMappedArgument vType) {
            ArgumentType vanilla = vType.getVanillaArgumentType();

            Validate.isTrue(ArgumentTypeInfos.isClassRecognized(vanilla.getClass()), "Type returned by "
                    + VanillaMappedArgument.class.getSimpleName()
                    + " is not registered in vanilla registry"
            );

            return vanilla;
        }

        // This seems to be a works-for-all kind of thing for the most part
        // It's good enough, but still limiting
        return ScoreHolderArgument.scoreHolder();
    }
}