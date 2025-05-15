package org.example.domain.user.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AuthResponseDTO {
    private String firebaseUid;
    private String email;
    private String gender;
    private String password;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
}
