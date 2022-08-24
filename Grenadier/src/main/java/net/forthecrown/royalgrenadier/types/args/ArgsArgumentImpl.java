package net.forthecrown.royalgrenadier.types.args;

import com.google.common.collect.ImmutableMap;
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
import net.forthecrown.grenadier.CompletionProvider;
import net.forthecrown.grenadier.types.args.Argument;
import net.forthecrown.grenadier.types.args.ArgsArgument;
import net.forthecrown.grenadier.types.args.ParsedArgs;
import net.forthecrown.royalgrenadier.GrenadierUtils;
import net.forthecrown.royalgrenadier.VanillaMappedArgument;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ArgsArgumentImpl implements ArgsArgument, VanillaMappedArgument {
    public static final SimpleCommandExceptionType
            EMPTY_LABEL = new SimpleCommandExceptionType(() -> "Empty label!"),
            INPUT_REQUIRED = new SimpleCommandExceptionType(() -> "Argument requires input");

    public static final DynamicCommandExceptionType
            UNKNOWN_ARG = new DynamicCommandExceptionType(o -> () -> "Unknown arg: '" + o + "'"),
            MISSING_ARG = new DynamicCommandExceptionType(o -> () -> "Missing arg: '" + o + "'"),
            ALREADY_USED = new DynamicCommandExceptionType(o -> () -> "Arg already set: '" + o + "'");

    @Getter
    private final Map<String, ArgEntry> args;

    @Getter
    private final char separator;

    @Override
    public ParsedArgs parse(StringReader reader) throws CommandSyntaxException {
        var builder = new ParsedArgsImpl.Builder();

        while (reader.canRead()) {
            reader.skipWhitespace();

            int start = reader.getCursor();
            String label = readLabel(reader);
            reader.skip();

            if (label.isBlank()) {
                throw EMPTY_LABEL.createWithContext(reader);
            }

            Argument argument = getArg(label);

            if (argument == null) {
                throw UNKNOWN_ARG.createWithContext(
                        GrenadierUtils.correctReader(reader, start),
                        label
                );
            }

            if (builder.has(argument)) {
                throw ALREADY_USED.createWithContext(
                        GrenadierUtils.correctReader(reader, start),
                        label
                );
            }

            if (!reader.canRead()) {
                throw INPUT_REQUIRED.create();
            }

            reader.skipWhitespace();

            Object value = argument.getParser().parse(reader);
            builder.add(argument, value);
        }

        for (var e: args.entrySet()) {
            if (e.getValue().required && !builder.has(e.getValue().argument)) {
                throw MISSING_ARG.create(e.getKey());
            }
        }

        return builder.build();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        builder = builder.createOffset(builder.getStart() + builder.getRemainingLowerCase().lastIndexOf(' ') + 1);
        var remaining = builder.getRemainingLowerCase();

        if (remaining.contains(separator + "")) {
            var reader = new StringReader(builder.getInput());
            reader.setCursor(builder.getStart());

            var label = readLabel(reader);
            var arg = args.get(label);

            if (arg != null) {
                if (!reader.canRead()) {
                    builder.suggest(separator + "");
                    return builder.buildFuture();
                }

                reader.skip();
                reader.skipWhitespace();

                builder = builder.createOffset(reader.getCursor());
                return arg.argument.getParser().listSuggestions(context, builder);
            }
        }

        return CompletionProvider.suggestMatching(builder, getKeys());
    }

    private String readLabel(StringReader reader) {
        int start = reader.getCursor();

        while (reader.canRead() && reader.peek() != separator) {
            reader.skip();
        }

        int end = reader.getCursor();

        return reader.getString().substring(start, end).trim();
    }

    @Override
    public Argument getArg(String name) {
        var argEntry = args.get(name);
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
        private final Map<String, ArgEntry> entries = new HashMap<>();

        @Getter
        private char separator = COLON_SEPARATOR;

        @Override
        public <T> Builder add(Argument<T> argument, boolean required) {
            entries.put(argument.getName(), new ArgEntry(argument, required));
            return this;
        }

        @Override
        public Builder setSeparator(char separator) {
            this.separator = separator;
            return this;
        }

        @Override
        public ArgsArgument build() {
            return new ArgsArgumentImpl(
                    ImmutableMap.copyOf(entries),
                    separator
            );
        }
    }

    @RequiredArgsConstructor
    public static class ArgEntry {
        private final Argument argument;
        private final boolean required;
    }
}