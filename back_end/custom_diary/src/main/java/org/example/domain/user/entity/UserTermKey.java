package org.example.domain.user.entity;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class UserTermKey implements Serializable {
    private String userId;    // firebase_uid
    private Long termId;
}
