package com.project.raidho.controller;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.domain.meetingPost.dto.ThemeCategoryRequestDto;
import com.project.raidho.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/api/category")
    public ResponseDto<?> createCategory(@RequestBody ThemeCategoryRequestDto requestDto,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        if (requestDto.getCountryName() == null) {
            return ResponseDto.fail(404, "카테고리 정보를 입력해주세요.");
        }
        categoryService.createCategory(requestDto,userDetails);
        return null;
    }

    // Todo :: postman test 용
    @PostMapping("/api/category/test")
    public ResponseDto<?> createCategory(@RequestBody ThemeCategoryRequestDto requestDto) {
        if (requestDto.getCountryName() == null) {
            return ResponseDto.fail(404, "카테고리 정보를 입력해주세요.");
        }
        categoryService.createCategoryTest(requestDto);
        return ResponseDto.success(requestDto.getCountryName());
    }

}
