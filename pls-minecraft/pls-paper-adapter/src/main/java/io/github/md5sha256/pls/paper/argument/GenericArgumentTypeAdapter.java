package io.github.md5sha256.pls.paper.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import io.github.md5sha256.pls.function.FunctionParameter;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class GenericArgumentTypeAdapter<T extends ArgumentType<V>, V> implements ArgumentTypeAdapter<T, V> {

    public abstract @NonNull String getDescription(T argumentType);

    @Override
    public FunctionParameter adaptArgumentType(T argumentType, CommandNode<?> node) {
        return new FunctionParameter(
                ArgumentTypeAdapterUtil.guessType(argumentType.getClass()),
                getDescription(argumentType),
                ArgumentTypeAdapterUtil.getEnumConstants(argumentType.getClass())
        );
    }
}
