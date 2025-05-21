package org.example.domain.gallery.repository;

import org.example.domain.gallery.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByUserFirebaseUid(String uid);
}