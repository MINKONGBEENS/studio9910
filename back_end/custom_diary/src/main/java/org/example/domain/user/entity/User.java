package org.example.domain.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Data
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "firebase_uid")
    private String firebaseUid;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 1)
    private String gender;  // 'M','F','O'

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate birthDate;

    public User(String firebaseUid, String email, String gender, String password, LocalDate birthDate) {
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.gender = gender;
        this.password = password;
        this.birthDate = birthDate;
        this.createdAt = LocalDateTime.now();
    }
}

