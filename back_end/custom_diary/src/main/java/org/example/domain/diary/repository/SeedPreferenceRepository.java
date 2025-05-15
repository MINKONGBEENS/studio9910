package org.example.domain.diary.repository;

import org.example.domain.diary.entity.SeedPreference;
import org.example.domain.diary.entity.SeedPreferenceKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeedPreferenceRepository extends JpaRepository<SeedPreference, SeedPreferenceKey> {

}
