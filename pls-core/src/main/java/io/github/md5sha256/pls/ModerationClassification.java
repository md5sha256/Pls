package io.github.md5sha256.pls;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public record ModerationClassification(Set<ModerationCategory> flaggedCategories, Map<ModerationCategory, Double> scores, boolean flagged) {

    public ModerationClassification(Set<ModerationCategory> flaggedCategories, Map<ModerationCategory, Double> scores, boolean flagged) {
        this.flaggedCategories = EnumSet.copyOf(flaggedCategories);
        this.scores = new EnumMap<>(scores);
        this.flagged = flagged;
    }

    @Override
    public Set<ModerationCategory> flaggedCategories() {
        return Collections.unmodifiableSet(this.flaggedCategories);
    }

    @Override
    public Map<ModerationCategory, Double> scores() {
        return Collections.unmodifiableMap(this.scores);
    }

    public double score(ModerationCategory moderationCategory) {
        return this.scores.get(moderationCategory);
    }

    public boolean isFlagged(ModerationCategory moderationCategory) {
        return this.flaggedCategories.contains(moderationCategory);
    }

    public boolean anyFlagged(ModerationCategory... moderationCategories) {
        for (ModerationCategory category : moderationCategories) {
            if (this.flaggedCategories.contains(category)) {
                return true;
            }
        }
        return false;
    }


    public boolean allFlagged(ModerationCategory... moderationCategories) {
        return this.flaggedCategories.containsAll(Arrays.asList(moderationCategories));
    }

    public boolean noneFlagged(ModerationCategory... moderationCategories) {
        for (ModerationCategory category : moderationCategories) {
            if (this.flaggedCategories.contains(category)) {
                return false;
            }
        }
        return true;
    }

}
