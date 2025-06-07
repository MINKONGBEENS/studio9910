package org.example.domain.diary.repository;

import org.example.domain.diary.entity.SeedPreference;
import org.example.domain.diary.entity.SeedPreferenceKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeedPreferenceRepository extends JpaRepository<SeedPreference, SeedPreferenceKey> {
    List<SeedPreference> findByUserId(String userId);

}
