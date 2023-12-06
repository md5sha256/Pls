package io.github.md5sha256.pls.function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@ConfigSerializable
public class FunctionParameter {
    @Setting("type")
    @Required
    private String type;

    @Setting("description")
    private String description;

    @Setting("enum")
    private String[] enumConstants;

    /**
     * Default constructor for configurate
     */
    @SuppressWarnings("unused")
    FunctionParameter() {

    }

    public String type() {
        return type;
    }

    public String description() {
        return description;
    }

    public String[] enumConstants() {
        return enumConstants;
    }

    public FunctionParameter(@NonNull Type type,
                             @Nullable String description,
                             @Nullable String[] enumConstants
    ) {
        this.type = type.toString();
        this.description = description;
        this.enumConstants = enumConstants;
    }

    public static <E extends Enum<E>> FunctionParameter createForEnum(
            @Nullable String description,
            @NonNull Class<E> enumClass,
            @NonNull Function<E, String> stringMapper
    ) {
        return createForEnum(description, EnumSet.allOf(enumClass), stringMapper);
    }

    public static <E extends Enum<E>> FunctionParameter createForEnum(
            @Nullable String description,
            @NonNull Class<E> enumClass
    ) {
        return createForEnum(description, EnumSet.allOf(enumClass));
    }

    public static <E extends Enum<E>> FunctionParameter createForEnum(
            @Nullable String description,
            @NonNull Set<E> allowedConstants,
            @NonNull Function<E, String> stringMapper
    ) {
        String[] enumConstants = allowedConstants.stream().map(stringMapper).toArray(String[]::new);
        return new FunctionParameter(Type.STRING, description, enumConstants);
    }

    public static <E extends Enum<E>> FunctionParameter createForEnum(
            @Nullable String description,
            @NonNull Set<E> allowedConstants
    ) {
        return createForEnum(description, allowedConstants, Enum::name);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        FunctionParameter parameter = (FunctionParameter) object;
        return Objects.equals(type, parameter.type) && Objects.equals(description,
                parameter.description) && Arrays.equals(enumConstants,
                parameter.enumConstants);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type, description);
        result = 31 * result + Arrays.hashCode(enumConstants);
        return result;
    }

    @Override
    public String toString() {
        return "FunctionParameter{" +
                "type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", enumConstants=" + Arrays.toString(enumConstants) +
                '}';
    }

    /**
     * Json schema type
     */
    public enum Type {
        /**
         * A string
         */
        STRING("string"),
        /**
         * An integer
         */
        INTEGER("integer"),
        /**
         * An integer or a floating point number
         */
        NUMBER("number");

        private final String apiName;

        Type(String apiName) {
            this.apiName = apiName;
        }

        @Override
        public String toString() {
            return this.apiName;
        }
    }

}
