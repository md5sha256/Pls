package io.github.md5sha256.pls.paper.argument.adapter;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import io.github.md5sha256.pls.function.FunctionParameter;
import io.github.md5sha256.pls.paper.argument.ArgumentTypeAdapter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;

public class EnumArgumentAdapter<T extends ArgumentType<V>, V extends Enum<V>> implements ArgumentTypeAdapter<T, V> {

    public static <V extends Enum<V>> String lowerCaseName(V value) {
        return value.name().toLowerCase(Locale.ENGLISH);
    }

    public static <V extends Enum<V>> String name(V value) {
        return value.name();
    }

    private final String description;
    private final String[] enumConstants;

    public EnumArgumentAdapter(@NonNull String description, V[] enumConstants) {
        this(description, enumConstants, Enum::name);
    }

    public EnumArgumentAdapter(@NonNull String description,
                               V[] enumConstants,
                               Function<V, String> toStringProvider) {
        this.description = description;
        this.enumConstants = Arrays.stream(enumConstants)
                .map(toStringProvider)
                .toArray(String[]::new);
    }

    @Override
    public FunctionParameter adaptArgumentType(T argumentType, CommandNode<?> node) {
        return new FunctionParameter(
                FunctionParameter.Type.STRING,
                this.description,
                Arrays.copyOf(this.enumConstants, this.enumConstants.length)
        );
    }
}
