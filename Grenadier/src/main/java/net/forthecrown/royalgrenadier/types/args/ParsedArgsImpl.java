package net.forthecrown.royalgrenadier.types.args;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.Pair;
import net.forthecrown.grenadier.CommandSource;
import net.forthecrown.grenadier.types.args.Argument;
import net.forthecrown.grenadier.types.args.ParsedArgs;

import java.util.HashMap;
import java.util.Map;

public class ParsedArgsImpl implements ParsedArgs {
    private final Map<Argument, Object> values;
    private final Map<String, Pair<Argument, Object>> namedValues;

    public ParsedArgsImpl(Map<Argument, Object> values) {
        this.values = ImmutableMap.copyOf(values);

        Map<String, Pair<Argument, Object>> map = new HashMap<>();

        for (var e: values.entrySet()) {
            var arg = e.getKey();
            var pair = Pair.of(arg, e.getValue());

            map.put(arg.getName(), pair);

            for (var s: arg.getAliases()) {
                map.put(s, pair);
            }
        }

        namedValues = ImmutableMap.copyOf(map);
    }

    @Override
    public <T> T getOrDefault(Argument<T> argument, T def) {
        return (T) values.getOrDefault(argument, def);
    }

    @Override
    public <T> T getOrDefault(String name, Class<T> type, T def) {
        var pair = namedValues.get(name);

        if (!type.isInstance(pair.value())) {
            return def;
        }

        return (T) pair.value();
    }

    @Override
    public <T> T getOrDefault(String name, Class<T> type, T def, CommandSource source) throws CommandSyntaxException {
        var pair = namedValues.get(name);

        if (pair == null) {
            return def;
        }

        if (!type.isInstance(pair.value())) {
            return def;
        }

        testArgument(pair.key(), source);
        return (T) pair.value();
    }

    @Override
    public int size() {
        return values.size();
    }

    static class Builder {
        final Map<Argument, Object> values = new HashMap<>();

        public <T> void add(Argument<T> argument, T obj) {
            values.put(argument, obj);
        }

        public boolean has(Argument argument) {
            return values.containsKey(argument);
        }

        public ParsedArgsImpl build() {
            return new ParsedArgsImpl(values);
        }
    }
}