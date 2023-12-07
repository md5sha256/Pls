package io.github.md5sha256.pls.paper.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import io.leangen.geantyref.GenericTypeReflector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ArgumentTypeAdapters {

    private final Map<Class<?>, ArgumentTypeAdapter<?, ?>> adapters = new HashMap<>();
    private final Map<Class<?>, Supplier<? extends ArgumentTypeAdapter<?, ?>>> adapterFactories = new HashMap<>();

    public ArgumentTypeAdapters() {

    }

    public ArgumentTypeAdapters(ArgumentTypeAdapters other) {
        this.adapters.putAll(other.adapters);
    }

    public <T extends ArgumentType<V>, V> void withAdapter(Class<T> argumentType, ArgumentTypeAdapter<T, V> adapter) {
        this.adapters.put(argumentType, adapter);
    }

    public <T extends ArgumentType<?>> void withGenericFactory(Class<? super T> argumentType, Supplier<ArgumentTypeAdapter<? super T, ?>> adapterFactory) {
        Class<?> erased = GenericTypeReflector.erase(argumentType);
        this.adapterFactories.put(erased, adapterFactory);
    }

    @SuppressWarnings("rawtypes")
    public Optional<ArgumentTypeAdapter<?, ?>> getRawAdapter(Class<? extends ArgumentType> argumentType) {
        ArgumentTypeAdapter<?, ?> adapter = this.adapters.get(argumentType);
        if (adapter != null) {
            return Optional.of(adapter);
        }
        Class<?> erased = GenericTypeReflector.erase(argumentType);
        return Optional.ofNullable(this.adapterFactories.get(erased)).map(Supplier::get);
    }

}
