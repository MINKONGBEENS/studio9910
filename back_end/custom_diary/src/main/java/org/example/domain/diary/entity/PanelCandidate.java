package org.example.domain.diary.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "panel_candidates",
        uniqueConstraints = @UniqueConstraint(columnNames = {"session_id","set_id","panel_index"}))
@Getter
@Setter
@Data
@NoArgsConstructor
public class PanelCandidate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private GenerationSession session;

    private short setId;
    private short panelIndex;
    private long seed;
    @Column(columnDefinition = "TEXT")
    private String promptEn;
    @Column(nullable = false)
    private String imagePath;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
