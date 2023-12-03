package io.github.md5sha256.pls;

import io.github.md5sha256.pls.function.FunctionParameter;

public record ParameterContext(FunctionParameter parameter, String name, boolean required) {
}
