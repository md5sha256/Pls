package io.github.md5sha256.pls.function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Arrays;
import java.util.Objects;


@SuppressWarnings("unused")
@ConfigSerializable
public record Function(
        @Setting("name")
        @Required
        @NonNull String name,
        @Setting("description")
        @Nullable String description,

        @Setting("parameters")
        @Required
        @NonNull FunctionParameters parameters,

        @Setting("required")
        @Nullable String[] requiredParams
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Function function = (Function) o;
        return Objects.equals(name, function.name) && Objects.equals(description,
                function.description) && Objects.equals(parameters,
                function.parameters) && Arrays.equals(requiredParams,
                function.requiredParams);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, description, parameters);
        result = 31 * result + Arrays.hashCode(requiredParams);
        return result;
    }

    @Override
    public String toString() {
        return "Function{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", parameters=" + parameters +
                ", requiredParams=" + Arrays.toString(requiredParams) +
                '}';
    }
}
