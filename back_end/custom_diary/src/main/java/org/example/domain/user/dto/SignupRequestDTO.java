package org.example.domain.user.dto;

import com.google.firebase.database.annotations.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class SignupRequestDTO {
    @NotBlank
    private String idToken;

    @NotBlank
    private String password;

    @NotEmpty
    private List<TermAgreementDTO> terms;

    @NotBlank
    @Pattern(regexp = "[MFO]")
    private String gender; // 'M','F','O'

    @NotNull
    private LocalDate birthDate;

    @NotEmpty
    private List<Long> genreIds;

    @NotNull
    private Map<String, String> artStylePrompt;  // JSON 형태의 key/values
}
