package com.project.raidho.repository;

import com.project.raidho.domain.Tags;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tags, Long> {
}
