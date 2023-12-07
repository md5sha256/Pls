package io.github.md5sha256.pls.function;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class FunctionWrapperSerializer implements TypeSerializer<FunctionWrapper> {

    @Override
    public @Nullable FunctionWrapper deserialize(Type type,
                                       ConfigurationNode node) throws SerializationException {
        Function function = node.node("function").get(Function.class);
        if (function == null) {
            return null;
        }
        return new FunctionWrapper(function);
    }

    @Override
    public void serialize(Type type,
                          @Nullable FunctionWrapper obj,
                          ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.removeChild("function");
            node.removeChild("type");
            return;
        }
        node.node("function").set(obj.function());

    }
}
