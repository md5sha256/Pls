package io.github.md5sha256.pls.function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        FunctionParameters that = (FunctionParameters) object;
        return Objects.equals(type, that.type) && Objects.equals(properties,
                that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, properties);
    }

    @Override
    public String toString() {
        return "FunctionParameters{" +
                "type='" + type + '\'' +
                ", properties=" + properties +
                '}';
    }
}
