package org.example.domain.diary.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "session_choice")
@Getter
@Setter
@Data
@NoArgsConstructor
public class SessionChoice {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID sessionId;
    private short selectedSet;
    @Column(nullable = false)
    private LocalDateTime selectedAt;
}
