package io.github.md5sha256.pls;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.md5sha256.pls.function.FunctionParameter;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeFactory;
import io.leangen.geantyref.TypeToken;
import net.minecraft.commands.arguments.HeightmapTypeArgument;
import net.minecraft.commands.arguments.SignedArgument;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.A;

import javax.annotation.Nullable;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public final class BasicArgumentTypeAdapter<T extends ArgumentType<V>, V> implements ArgumentTypeAdapter<T, V> {

    @Override
    public FunctionParameter adaptArgumentType(T argumentType,
                                               String argDesc,
                                               boolean required) {
        Class<?> valueType = getValueType(argumentType.getClass());
        if (valueType == null) {
            throw new IllegalStateException("Failed to parse type argument!");
        }
        return new FunctionParameter(
                guessType(valueType),
                argDesc,
                getEnumConstants(valueType)
        );
    }

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

    private String[] getEnumConstants(Class<?> valueType) {
        Object[] rawEnumConstants =
                GenericTypeReflector.erase(valueType).getEnumConstants();
        if (rawEnumConstants == null) {
            // If enum constants are null, the class doesn't extent enum
            return null;
        }
        String[] enumConstants = new String[rawEnumConstants.length];
        for (int i = 0; i < rawEnumConstants.length; i++) {
            Enum<?> constant = (Enum<?>) rawEnumConstants[i];
            enumConstants[i] = constant.name();
        }
        return enumConstants;
    }

    private FunctionParameter.Type guessType(Class<?> valueType) {
        // Check if its a boxed type or if its a boolean
        // If its not a boxed type we assume the input is a string
        if (!GenericTypeReflector.isBoxType(valueType) || Boolean.class.equals(valueType)) {
            return FunctionParameter.Type.STRING;
        }
        // Check integer types first
        if (Integer.class.equals(valueType)
                || Long.class.equals(valueType)
                || Byte.class.equals(valueType)
        ) {
            return FunctionParameter.Type.INTEGER;
        }
        // If its not a string or an integer it's a number
        return FunctionParameter.Type.NUMBER;
    }
}
