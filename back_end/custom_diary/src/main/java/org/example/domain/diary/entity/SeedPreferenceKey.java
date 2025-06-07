package org.example.domain.diary.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class SeedPreferenceKey implements Serializable {
    private String userId;
    private long seed;
    public SeedPreferenceKey() {}

    public SeedPreferenceKey(String userId, long seed) {
        this.userId = userId;
        this.seed = seed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeedPreferenceKey)) return false;
        SeedPreferenceKey that = (SeedPreferenceKey) o;
        return seed == that.seed &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, seed);
    }
}
