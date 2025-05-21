package org.example.domain.diary.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "weekly_webtoons",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","year","month","week"}))
@Getter
@Setter
@Data
@NoArgsConstructor
public class WeeklyWebtoon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false, length=128)
    private String userId;

    @Column(name="session_id", columnDefinition="uuid", nullable=false)
    private UUID sessionId;

    @Column(nullable=false)
    private int year;
    @Column(nullable=false)
    private int month;
    @Column(nullable=false)
    private int week;

    @Column(name="merged_image_url", nullable=false)
    private String mergedImageUrl;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;
}
