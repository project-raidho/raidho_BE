package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.ThemeCategory;
import com.project.raidho.domain.meetingPost.dto.MeetingPostRequestDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<ThemeCategory, Long> {
    Optional<ThemeCategory> findByCountryName(String countryName);
}
