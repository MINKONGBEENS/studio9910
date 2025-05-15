package org.example.domain.user.dto;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SignupResponseDTO {
    private String firebaseUid;
    private String email;
    private String password;
    private String gender;
    private LocalDate birthDate;
    private List<String> agreedTermCodes;
    private List<Long> genreIds;
    private Long artStyleId;
}
