package org.example.domain.diary.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeedPreferenceKey implements Serializable {
    private String userId;
    private long seed;
}
