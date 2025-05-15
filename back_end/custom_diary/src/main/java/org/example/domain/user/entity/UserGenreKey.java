package org.example.domain.user.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Embeddable
@Data
public class UserGenreKey implements Serializable {
    private String userId;
    private Long genreId;
}
