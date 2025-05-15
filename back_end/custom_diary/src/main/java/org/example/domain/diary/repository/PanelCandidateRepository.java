package org.example.domain.diary.repository;

import org.example.domain.diary.entity.PanelCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PanelCandidateRepository extends JpaRepository<PanelCandidate, Long> {
    List<PanelCandidate> findAllBySessionId(UUID sessionId);
}
