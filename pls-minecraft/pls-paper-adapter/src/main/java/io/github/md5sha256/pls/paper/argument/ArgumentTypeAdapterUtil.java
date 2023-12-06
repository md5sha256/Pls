package io.github.md5sha256.pls.paper.argument;

import io.github.md5sha256.pls.function.FunctionParameter;
import io.leangen.geantyref.GenericTypeReflector;

public class ArgumentTypeAdapterUtil {

    public static String[] getEnumConstants(Class<?> valueType) {
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

    public static FunctionParameter.Type guessType(Class<?> valueType) {
        // Check if it's a boxed type or if it's a boolean
        // If it's not a boxed type we assume the input is a string
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
        // If it's not a string or an integer it's a number
        return FunctionParameter.Type.NUMBER;
    }

}
