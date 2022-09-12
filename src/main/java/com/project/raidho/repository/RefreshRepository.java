package com.project.raidho.repository;

import com.project.raidho.domain.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {
//    Optional<RefreshToken> findByMember(Member member);
}
