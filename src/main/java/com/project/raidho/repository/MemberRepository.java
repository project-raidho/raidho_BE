package com.project.raidho.repository;

import com.project.raidho.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByProviderId(String providerId);
    Optional<Member> findById(Long id);

}
