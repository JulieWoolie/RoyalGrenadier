package net.forthecrown.royalgrenadier.types.args;

import com.google.common.collect.ImmutableMap;
import net.forthecrown.grenadier.types.args.Argument;
import net.forthecrown.grenadier.types.args.ParsedArgs;

import java.util.HashMap;
import java.util.Map;

public class ParsedArgsImpl implements ParsedArgs {
    private final Map<Argument, Object> values;

    public ParsedArgsImpl(Map<Argument, Object> values) {
        this.values = ImmutableMap.copyOf(values);
    }

    @Override
    public <T> T getOrDefault(Argument<T> argument, T def) {
        return (T) values.getOrDefault(argument, def);
    }

    @Override
    public int size() {
        return values.size();
    }

    static class Builder {
        private final Map<Argument, Object> values = new HashMap<>();

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