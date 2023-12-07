package io.github.md5sha256.pls.paper.argument.adapter;

import com.mojang.brigadier.arguments.ArgumentType;
import io.github.md5sha256.pls.paper.argument.GenericArgumentTypeAdapter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.StringJoiner;

public class ScalarDescriptionAdapter<T extends ArgumentType<V>, V> extends GenericArgumentTypeAdapter<T, V> {

    private final String description;
    private final boolean injectExamples;

    public ScalarDescriptionAdapter(@NonNull String description, boolean injectExamples) {
        this.description = description;
        this.injectExamples = injectExamples;
    }

    public ScalarDescriptionAdapter(@NonNull String description) {
        this(description, true);
    }

    @Override
    public @NonNull String getDescription(T argumentType) {
        if (!this.injectExamples) {
            return this.description;
        }
        StringJoiner joiner = new StringJoiner(", ");
        argumentType.getExamples().forEach(joiner::add);
        return this.description + " Examples: " + joiner;
    }
}
