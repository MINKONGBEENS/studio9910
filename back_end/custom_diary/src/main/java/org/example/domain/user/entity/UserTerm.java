package org.example.domain.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_terms")
@Getter
@Setter
@Data
@NoArgsConstructor
public class UserTerm {
    @EmbeddedId
    private UserTermKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("termId")
    @JoinColumn(name = "term_id")
    private Term term;

    @Column(nullable = false)
    private boolean agreed;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
