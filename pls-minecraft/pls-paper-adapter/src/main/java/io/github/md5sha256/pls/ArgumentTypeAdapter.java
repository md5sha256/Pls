package io.github.md5sha256.pls;

import com.mojang.brigadier.arguments.ArgumentType;
import io.github.md5sha256.pls.function.FunctionParameter;

public interface ArgumentTypeAdapter<T extends ArgumentType<V>, V> {
    FunctionParameter adaptArgumentType(T argumentTypeString, String argDesc, boolean required);

}
