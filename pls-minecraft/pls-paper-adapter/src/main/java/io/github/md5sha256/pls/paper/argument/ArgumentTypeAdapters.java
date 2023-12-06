package io.github.md5sha256.pls.paper.argument;

import com.mojang.brigadier.arguments.ArgumentType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ArgumentTypeAdapters {

    private final Map<Class<?>, ArgumentTypeAdapter<?, ?>> adapters = new HashMap<>();

    public <T extends ArgumentType<V>, V> void withAdapter(Class<T> argumentType, ArgumentTypeAdapter<T, V> adapter) {
        this.adapters.put(argumentType, adapter);
    }

    @SuppressWarnings("unchecked")
    public <T extends ArgumentType<V>, V> Optional<ArgumentTypeAdapter<T, V>> getAdapter(Class<T> argumentType) {
        ArgumentTypeAdapter<?, ?> adapter = this.adapters.get(argumentType);
        if (adapter == null) {
            return Optional.empty();
        }
        return Optional.of((ArgumentTypeAdapter<T, V>) adapter);
    }

    @SuppressWarnings("rawtypes")
    public Optional<ArgumentTypeAdapter<?, ?>> getRawAdapter(Class<? extends ArgumentType> argumentType) {
        ArgumentTypeAdapter<?, ?> adapter = this.adapters.get(argumentType);
        return Optional.ofNullable(adapter);
    }

}
