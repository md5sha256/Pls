package io.github.md5sha256.pls.paper.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import io.github.md5sha256.pls.function.FunctionParameter;

import java.lang.reflect.GenericDeclaration;

public interface ArgumentTypeAdapter<T extends ArgumentType<V>, V> {
    FunctionParameter adaptArgumentType(T argumentType, String argDesc, boolean required);

}
