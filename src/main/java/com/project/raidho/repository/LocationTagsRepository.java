package com.project.raidho.repository;

import com.project.raidho.domain.LocationTags;

import com.project.raidho.domain.Tags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationTagsRepository extends JpaRepository<LocationTags, Long> {

//    List<LocationTags> findAllByPost_Id(Long id);
}
