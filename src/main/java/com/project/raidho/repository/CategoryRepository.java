package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.ThemeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<ThemeCategory, Long> {
}
