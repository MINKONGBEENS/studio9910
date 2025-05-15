package org.example.domain.diary.repository;

import org.example.domain.diary.entity.SessionChoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SessionChoiceRepository extends JpaRepository<SessionChoice, UUID> {

}
