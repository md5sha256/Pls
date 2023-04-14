package io.github.md5sha256.pls;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class ModerationClassificationSerializer implements TypeSerializer<ModerationClassification> {

    //"results": [
    //    {
    //      "categories": {
    //        "hate": false,
    //        "hate/threatening": true,
    //        "self-harm": false,
    //        "sexual": false,
    //        "sexual/minors": false,
    //        "violence": true,
    //        "violence/graphic": false
    //      },
    //      "category_scores": {
    //        "hate": 0.22714105248451233,
    //        "hate/threatening": 0.4132447838783264,
    //        "self-harm": 0.005232391878962517,
    //        "sexual": 0.01407341007143259,
    //        "sexual/minors": 0.0038522258400917053,
    //        "violence": 0.9223177433013916,
    //        "violence/graphic": 0.036865197122097015
    //      },
    //      "flagged": true
    //    }

    private final ModerationCategory[] categories = ModerationCategory.values();

    @Override
    public ModerationClassification deserialize(Type type, ConfigurationNode node) throws SerializationException {
        ConfigurationNode flaggedNode = node.node("flagged");
        ConfigurationNode scoresNode = node.node("category_scores");
        ConfigurationNode categoriesNode = node.node("categories");

        if (flaggedNode.empty()) {
            throw new SerializationException("Missing flagged key");
        }
        if (scoresNode.empty()) {
            throw new SerializationException("Missing category_scores key");
        }
        if (categoriesNode.empty()) {
            throw new SerializationException("Missing categories key");
        }

        final Set<ModerationCategory> flaggedCategories = EnumSet.noneOf(ModerationCategory.class);
        final Map<ModerationCategory, Double> scores = new EnumMap<>(ModerationCategory.class);

        final boolean flagged = flaggedNode.getBoolean();

        for (ModerationCategory category : categories) {
            ConfigurationNode valueNode = categoriesNode.node(category.apiName());
            if (valueNode.empty()) {
                throw new SerializationException("Missing result value for category: " + category.apiName());
            }
            boolean value = valueNode.getBoolean();
            if (value) {
                flaggedCategories.add(category);
            }
            ConfigurationNode scoreNode = scoresNode.node(category.apiName());
            if (scoreNode.empty()) {
                throw new SerializationException("Missing score value for category: " + category.apiName());
            }
            double score = scoreNode.getDouble();
            scores.put(category, score);
        }
        return new ModerationClassification(flaggedCategories, scores, flagged);
    }

    @Override
    public void serialize(Type type, @Nullable ModerationClassification obj, ConfigurationNode node) throws SerializationException {
        Set<ModerationCategory> flagged = obj.flaggedCategories();
        for (ModerationCategory category : categories) {
            node.node(category.apiName()).set(flagged.contains(category));
        }
    }
}
