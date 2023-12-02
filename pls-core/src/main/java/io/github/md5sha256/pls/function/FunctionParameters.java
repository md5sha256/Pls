package io.github.md5sha256.pls.function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Map;

@ConfigSerializable
public class FunctionParameters {

    @Setting
    @Required
    private String type = "object";

    @Setting("properties")
    @Required
    private Map<String, FunctionParameter> properties;

    /**
     * Default constructor for Configurate
     */
    FunctionParameters() {

    }

    public FunctionParameters(@NonNull Map<String, FunctionParameter> properties) {
        this.properties = properties;
    }

    public @NonNull String type() {
        return type;
    }

    public @NonNull Map<String, FunctionParameter> properties() {
        return properties;
    }
}
