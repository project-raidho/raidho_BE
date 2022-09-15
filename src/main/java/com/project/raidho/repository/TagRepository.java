package com.project.raidho.repository;

import com.project.raidho.domain.tags.Tags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tags, Long> {
    List<Tags> findAllByPost_Id(Long id);

    void deleteAllByPost_Id(Long id);
}
