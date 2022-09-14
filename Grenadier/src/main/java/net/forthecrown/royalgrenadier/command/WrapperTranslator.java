package net.forthecrown.royalgrenadier.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import it.unimi.dsi.fastutil.Pair;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.command.AbstractCommand;
import net.forthecrown.royalgrenadier.WrappedCommandSource;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Converts a given grenadier command node to a vanilla Brigadier node.
 * This isn't API, but I will still document this class to give anyone
 * that wants to create something similar to Grenadier an easier time...
 * or allows them to learn from my mistakes lol
 * <p>
 * This is done, broadly, by walking through all the parts and sub-nodes,
 * of a grenadier argument node and then converting them to vanilla ones
 * laid out in the same structure.
 * <p>
 * What I meant by that poorly explained paragraph is that this is a tree
 * walker class that translates grenadier to vanilla.
 * <p>
 * Please read the comments and documentation below for furher information
 * on how the translation is done.
 */
public class WrapperTranslator {

    private final CommandWrapper wrapper;
    private final AbstractCommand abstractCommand;
    private final LiteralArgumentBuilder<CommandSourceStack> nms;
    private final LiteralCommandNode<CommandSource> regged;

    public WrapperTranslator(CommandWrapper wrapper,
                             AbstractCommand command,
                             LiteralCommandNode<CommandSource> toConvert
    ) {
        this.abstractCommand = command;
        this.wrapper = wrapper;
        this.regged = toConvert;

        this.nms = literal(command.getName());
    }

    /**
     * Translates the grenadier node to vanilla, by calling
     * {@link #translateBase(CommandNode, ArgumentBuilder)} with
     * the {@link #regged} and {@link #nms} fields.
     *
     * @return The vanilla-mapped command node
     */
    public LiteralArgumentBuilder<CommandSourceStack> translate() {
        translateBase(regged, nms);
        return nms;
    }

    /**
     * Converts the given node's predicate to a vanilla predicate by
     * taking a given vanilla source, creating a {@link WrappedCommandSource}
     * and then calling the original, given, node's predicate
     *
     * @param test The node to translate the requirement of
     * @return The translated requirement
     */
    Predicate<CommandSourceStack> translateTest(Predicate<CommandSource> test) {
        return lis -> test.test(WrappedCommandSource.of(lis, abstractCommand, null));
    }

    /**
     * Translates the basic parts of the given source node to
     * the target vanilla node.
     * <p>
     * 'Base parts' here means the {@link CommandNode#getCommand()},
     * {@link CommandNode#getRequirement()} and {@link CommandNode#getChildren()}.
     * <p>
     * Also, apart of the base is the node redirect. Due to how redirects can potentially
     * occur, there's no way to prevent an infinite recursion loop during translation.
     * So I was forced to resort to taking Bukkit's approach to Brigadier, and made
     * this method simply return a greedy string argument, as that'll accept all input
     * given to it and won't stand in the way of any other arguments being parsed.
     * <p>
     * I didn't want to adopt the approach above but I felt like I had no other, choice,
     * as I'd tried to use a {@link Map} to prevent already translated nodes from being
     * translated twice, however it didn't work.
     *
     * @param source The source grenadier node
     * @param target The target vanilla node
     *
     * @return Either the <code>target</code> parameter, or an argument node, depending
     *         on if the source node had a {@link CommandNode#getRedirect()}. If it did,
     *         it returns a greedy string argument type.
     */
    ArgumentBuilder<CommandSourceStack, ?> translateBase(CommandNode<CommandSource> source, ArgumentBuilder<CommandSourceStack, ?> target) {
        if (source.getCommand() != null) {
            // Always call wrapper's execute method
            // that will parse the entire input, run
            // command logic and so forth on its own
            // without interference from vanilla
            target.executes(wrapper);
        }

        // If the requirement isn't the default one, convert it
        if (source.getRequirement() != ArgumentBuilder.<CommandSource>defaultRequirement()) {
            target.requires(translateTest(source.getRequirement()));
        }

        var children = source.getChildren();

        if (source.getRedirect() != null) {
            return required(source.getName(), StringArgumentType.greedyString())
                    .requires(target.getRequirement())
                    .executes(wrapper)
                    .suggests((context, builder) -> {
                        var wrappedSource = WrappedCommandSource.of(context.getSource(), abstractCommand, null);
                        return wrapper.suggest(wrappedSource, context.getInput());
                    });
        } else if (!children.isEmpty()) {
            // Translate children over to vanilla
            for (CommandNode<CommandSource> n: children) {
                target.then(translateNode(n));
            }
        }

        return target;
    }

    /**
     * Translates a single given argument node to vanilla.
     * <p>
     * It does this by checking if the given node <code>n</code>
     * is an instance of a {@link LiteralCommandNode} or a
     * {@link ArgumentCommandNode}, if it's the former, it
     * calls {@link #translateLiteral(LiteralCommandNode)} to
     * translate it, if it's the latter, it calls
     * {@link #translateArgument(ArgumentCommandNode)} to
     * translate it.
     * @param n The node to translate
     * @return The translated node
     * @throws IllegalStateException If the node was not either a literal or argument command node
     */
    ArgumentBuilder<CommandSourceStack, ?> translateNode(CommandNode<CommandSource> n) throws IllegalStateException {
        if (n instanceof LiteralCommandNode literal) {
            return translateLiteral(literal);
        } else if (n instanceof ArgumentCommandNode arg) {
            return translateArgument(arg);
        } else {
            throw new IllegalStateException("Cannot translate unknown node: " + n.getClass().getName());
        }
    }

    private LiteralArgumentBuilder<CommandSourceStack> literal(String name){
        return Commands.literal(name);
    }

    private RequiredArgumentBuilder<CommandSourceStack, ?> required(String name, ArgumentType<?> type){
        return Commands.argument(name, type);
    }

    /**
     * Simply takes the grenadier literal and creates a
     * vanilla literal with the same name and then calls
     * {@link #translateBase(CommandNode, ArgumentBuilder)}
     * before returning the result.
     *
     * @param node The literal to translate
     * @return The Translated result
     */
    ArgumentBuilder<CommandSourceStack, ?>  translateLiteral(LiteralCommandNode<CommandSource> node) {
        LiteralArgumentBuilder<CommandSourceStack> result = literal(node.getLiteral());
        return translateBase(node, result);
    }

    /**
     * Converts a given argument node to vanilla.
     * <p>
     * This is done by creating a vanilla node with the same name
     * as the grenadier one. The type for the node is gotten with
     * {@link #translateType(ArgumentType)}. If the grenadier node
     * has no custom suggestions and the <code>convertType</code>
     * result states to use vanilla suggestions, then the resulting
     * node will use vanilla for suggestions, otherwise, the source
     * node's suggestions are mapped to vanilla using
     * {@link CommandWrapper#getSuggestions(CommandContext, SuggestionsBuilder, ArgumentCommandNode)}
     *
     * @param node The node to translate
     * @return The translated result
     */
    ArgumentBuilder<CommandSourceStack, ?> translateArgument(ArgumentCommandNode<CommandSource, ?> node) {
        var typeResult = translateType(node.getType());

        var vanillaArgument = typeResult.right();
        var useVanillaSuggestions = typeResult.left();

        RequiredArgumentBuilder<CommandSourceStack, ?> result = required(node.getName(), vanillaArgument);

        if (node.getCustomSuggestions() != null || !useVanillaSuggestions) {
            result.suggests((c, b) -> wrapper.getSuggestions(c, b, node));
        } else {
            result.suggests(null);
        }

        return translateBase(node, result);
    }

    /**
     * Converts the given grenadier argument type to a vanilla
     * argument type.
     * <p>
     * If the given node is already registered in the vanilla registry,
     * like in the case of the built in argument types, this will
     * simply return the parameter itself.
     * <p>
     * If the given type is a {@link VanillaMappedArgument} then
     * it will use that argument's {@link VanillaMappedArgument#getVanillaArgumentType()}
     * for the type and {@link VanillaMappedArgument#useVanillaSuggestions()}
     * for the boolean in the returned pair. If the vanilla mapped argument
     * returns a non-vanilla argument, then this will throw an exception.
     * <p>
     * If the given type is neither a {@link VanillaMappedArgument} or
     * a vanilla argument already, then {@link ScoreHolderArgument#scoreHolder()}
     * is returned instead. This is because I believe that argument
     * casts the widest possible net for what it accepts in it's parsing
     * before automatically turning the chat red and showing an
     * error above the text in chat.
     *
     * @param type The type to convert.
     * @return A pair, the boolean states if the vanilla argument should
     *         be used for suggestions or not, and the value is the
     *         vanilla argument type.
     *
     * @throws IllegalArgumentException If the <code>type</code> is a {@link VanillaMappedArgument}
     *                                  but did not return a vanilla argument in its
     *                                  {@link VanillaMappedArgument#getVanillaArgumentType()}
     */
    Pair<Boolean, ArgumentType<?>> translateType(ArgumentType type) throws IllegalArgumentException {
        if (ArgumentTypeInfos.isClassRecognized(type.getClass())) {
            return Pair.of(true, type);
        }

        if (type instanceof VanillaMappedArgument vType) {
            ArgumentType vanilla = vType.getVanillaArgumentType();

            Validate.isTrue(ArgumentTypeInfos.isClassRecognized(vanilla.getClass()),
                    "Type returned by '%s' is not registered in vanilla registry",
                    vType.getClass().getName()
            );

            return Pair.of(vType.useVanillaSuggestions(), vanilla);
        }

        // This seems to be a works-for-all kind of thing for the most part
        // It's good enough, but still limiting
        return Pair.of(false, ScoreHolderArgument.scoreHolder());
    }
}