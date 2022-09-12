package com.project.raidho.repository;

import com.project.raidho.domain.MultipartFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImgRepository extends JpaRepository<MultipartFiles, Long> {

}
