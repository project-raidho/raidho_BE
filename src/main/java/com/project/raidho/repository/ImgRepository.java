package com.project.raidho.repository;

import com.project.raidho.domain.LocationTags;
import com.project.raidho.domain.MultipartFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImgRepository extends JpaRepository<MultipartFiles, Long> {
    List<MultipartFiles> findAllByPost_Id(Long id);
}
