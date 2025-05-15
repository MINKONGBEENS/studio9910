package org.example.domain.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class LoginRequestDTO {
    private String idToken;
}
