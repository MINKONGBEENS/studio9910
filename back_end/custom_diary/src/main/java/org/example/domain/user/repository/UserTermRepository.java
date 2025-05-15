package org.example.domain.user.repository;
import org.example.domain.user.entity.UserTerm;
import org.example.domain.user.entity.UserTermKey;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserTermRepository extends JpaRepository<UserTerm, UserTermKey> {
}
