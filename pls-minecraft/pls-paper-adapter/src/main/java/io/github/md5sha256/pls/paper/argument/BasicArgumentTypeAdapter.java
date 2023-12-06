package io.github.md5sha256.pls.paper.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import io.github.md5sha256.pls.function.FunctionParameter;
import io.leangen.geantyref.GenericTypeReflector;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class BasicArgumentTypeAdapter<T extends ArgumentType<V>, V> implements ArgumentTypeAdapter<T, V> {

    @Override
    public FunctionParameter adaptArgumentType(
            T argumentType,
            CommandNode<?> node
    ) {
        Class<?> valueType = getValueType(argumentType.getClass());
        if (valueType == null) {
            throw new IllegalStateException("Failed to parse type argument!");
        }
        return new FunctionParameter(
                ArgumentTypeAdapterUtil.guessType(valueType),
                node.getUsageText(),
                ArgumentTypeAdapterUtil.getEnumConstants(valueType)
        );
    }

    @SuppressWarnings("rawtypes")
    private @Nullable Class<?> getValueType(@NonNull Class<? extends ArgumentType> clazz) {
        Class<?> target = getValueTypeFromSuperInterfaces(clazz);
        if (target != null) {
            return target;
        }
        return getValueTypeFromSuperClass(clazz);
    }

    private Class<?> getValueTypeFromSuperClass(Class<?> clazz) {
        if (clazz.getSuperclass().equals(Object.class)) {
            return null;
        }
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof Class<?> aClass) {
            if (ArgumentType.class.isAssignableFrom(aClass)) {
                return getValueTypeFromSuperInterfaces(aClass);
            } else {
                return getValueTypeFromSuperClass(aClass);
            }
        } else if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type[] typeArgs = parameterizedType.getActualTypeArguments();
            if (typeArgs.length != 1) {
                throw new IllegalStateException("Found multiple type arguments for type: " + parameterizedType.getRawType());
            }
            Type actualType = typeArgs[0];
            if (actualType instanceof Class<?> actualTypeClass) {
                return actualTypeClass;
            } else if (actualType instanceof ParameterizedType actualTypeParameterized) {
                return (Class<?>) actualTypeParameterized.getRawType();
            } else {
                throw new IllegalStateException("Unknown actual type: " + actualType.getTypeName());
            }
        }
        return null;
    }

    private Class<?> getValueTypeFromSuperInterfaces(@NonNull Class<?> clazz) {
        for (Type type : clazz.getGenericInterfaces()) {
            Class<?> erased = GenericTypeReflector.erase(type);
            if ((erased.equals(ArgumentType.class)
                    || ArgumentType.class.isAssignableFrom(erased))
                    && type instanceof ParameterizedType parameterizedType
            ) {

                Type[] typeArgs = parameterizedType.getActualTypeArguments();
                if (typeArgs.length != 1) {
                    throw new IllegalStateException("Found multiple type arguments for type: " + parameterizedType.getRawType());
                }
                Type actualType = typeArgs[0];
                if (actualType instanceof Class<?> actualTypeClass) {
                    return actualTypeClass;
                } else if (actualType instanceof ParameterizedType actualTypeParameterized) {
                    return (Class<?>) actualTypeParameterized.getRawType();
                } else {
                    throw new IllegalStateException("Unknown actual type: " + actualType.getTypeName());
                }
            }
        }
        return null;
    }
}
