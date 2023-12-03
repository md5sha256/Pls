package io.github.md5sha256.pls;

import com.mojang.brigadier.arguments.ArgumentType;
import io.github.md5sha256.pls.function.FunctionParameter;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;

import java.lang.reflect.Type;

public class BasicArgumentTypeAdapter<T extends ArgumentType<V>, V> implements ArgumentTypeAdapter<T, V> {

    private final TypeToken<V> valueTypeToken = new TypeToken<>() {
    };

    @Override
    public FunctionParameter adaptArgumentType(T argumentType,
                                               String argDesc,
                                               boolean required) {
        return new FunctionParameter(
                guessType(),
                argDesc,
                getEnumConstants()
        );
    }

    private String[] getEnumConstants() {
        Object[] rawEnumConstants =
                GenericTypeReflector.erase(valueTypeToken.getType()).getEnumConstants();
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

    private FunctionParameter.Type guessType() {
        Type valueType = valueTypeToken.getType();
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
