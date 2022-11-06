package net.forthecrown.royalgrenadier.types.args;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.types.args.ArgsArgument;
import net.forthecrown.grenadier.types.args.Argument;
import net.forthecrown.grenadier.types.args.ParsedArgs;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ArgsArgumentImpl implements ArgsArgument, VanillaMappedArgument {
    public static final SimpleCommandExceptionType
            EMPTY_LABEL         = new SimpleCommandExceptionType(() -> "Empty label!"),
            INPUT_REQUIRED      = new SimpleCommandExceptionType(() -> "Argument requires input"),
            EXPECTED_SEPARATOR  = new SimpleCommandExceptionType(() -> "Expected separator ':' or '='"),
            EXPECTED_BORDER     = new SimpleCommandExceptionType(() -> "Expected bracket '[' or '{'");

    public static final DynamicCommandExceptionType
            UNKNOWN_ARG         = new DynamicCommandExceptionType(o -> () -> "Unknown arg: '" + o + "'"),
            MISSING_ARG         = new DynamicCommandExceptionType(o -> () -> "Missing arg: '" + o + "'"),
            DUPLICATE_ARG       = new DynamicCommandExceptionType(o -> () -> "Duplicate arg: '" + o + "'");

    @Getter
    final ImmutableMap<String, ArgEntry> args;
    final ImmutableSet<ArgEntry> entries;
    final TriState brackets;

    public ArgsArgumentImpl(BuilderImpl builder) {
        this.brackets = builder.brackets;
        this.args = builder.args.build();
        this.entries = builder.entries.build();
    }

    @Override
    public ParsedArgs parse(StringReader reader) throws CommandSyntaxException {
        var parser = new ArgumentsParser(reader, this);
        parser.parse();

        var parsedArgs = parser.builder.build();

        for (var e: args.entrySet()) {
            if (e.getValue().required && !parsedArgs.has(e.getValue().argument)) {
                throw MISSING_ARG.create(e.getKey());
            }
        }

        return parsedArgs;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (!(context.getSource() instanceof CommandSource)) {
            return Suggestions.empty();
        }

        var reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());

        ArgumentsParser parser = new ArgumentsParser(reader, this);

        try {
            parser.parse();
        } catch (CommandSyntaxException ignored) {}

        return parser.getSuggestions((CommandContext<CommandSource>) context, builder);
    }

    @Nonnull
    @Override
    public TriState bracketsForced() {
        return brackets;
    }

    @Override
    public Argument getArg(String name) {
        var argEntry = args.get(name.toLowerCase());
        return argEntry == null ? null : argEntry.argument;
    }

    @Override
    public Set<String> getKeys() {
        return args.keySet();
    }

    @Override
    public ArgumentType<?> getVanillaArgumentType() {
        return StringArgumentType.greedyString();
    }

    public static class BuilderImpl implements Builder {
        private final ImmutableSet.Builder<ArgEntry> entries = ImmutableSet.builder();
        private final ImmutableMap.Builder<String, ArgEntry> args = ImmutableMap.builder();
        private TriState brackets = TriState.FALSE;

        @Override
        public Builder bracketsForced(@Nullable TriState state) {
            brackets = state;
            return this;
        }

        @Override
        public <T> Builder add(Argument<T> argument, boolean required) {
            var entry = new ArgEntry(argument, required);
            entries.add(entry);

            args.put(argument.getName().toLowerCase(), entry);

            for (var s: argument.getAliases()) {
                args.put(s.toLowerCase(), entry);
            }

            return this;
        }

        @Override
        public ArgsArgument build() {
            return new ArgsArgumentImpl(this);
        }
    }

    @RequiredArgsConstructor
    public static class ArgEntry {
        final Argument argument;
        final boolean required;
    }

}