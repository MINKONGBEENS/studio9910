package org.example.domain.diary.repository;

import org.example.domain.diary.entity.GenerationSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GenerationSessionRepository extends JpaRepository<GenerationSession, UUID> {

}
