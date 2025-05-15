package org.example.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class TermAgreementDTO {
    @NotBlank
    private String code;
    private boolean agreed;
}
