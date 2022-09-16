package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.ThemeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThemeCategoryRepository extends JpaRepository<ThemeCategory, Long> {
    Optional<ThemeCategory> findByCountryName(String countryName);

    Optional<ThemeCategory> findById(Long id);
}
