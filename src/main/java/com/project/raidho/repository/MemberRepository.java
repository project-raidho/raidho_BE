package com.project.raidho.repository;

import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
//    @Query("SELECT m FROM Member m WHERE m.provider = :provider and m.providerId = :providerId and m.memberName = :memberName")
//    Optional<Member> findMember(@Param(value = "provider") String provider, @Param(value = "providerId") String providerId, @Param(value = "memberName") String memberName);
    Optional<Member> findByProviderId(String providerId);
    Optional<Member> findById(Long id);
}
