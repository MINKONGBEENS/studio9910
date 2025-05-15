package org.example.domain.diary.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "seed_preferences")
@Getter
@Setter
@Data
@NoArgsConstructor
public class SeedPreference {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Id
    private long seed;

    private int chosenCnt;
    private int skippedCnt;
    private LocalDateTime lastUsed;
}
