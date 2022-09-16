package com.project.raidho.controller;

import com.amazonaws.Response;
import com.project.raidho.domain.ResponseDto;
import com.project.raidho.domain.meetingPost.dto.ThemeCategoryRequestDto;
import com.project.raidho.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    // Todo :: postman 카테고리 등록 test 용
    @PostMapping("/api/category/test")
    public ResponseDto<?> createCategory(@RequestBody ThemeCategoryRequestDto requestDto) {
        if (requestDto.getCountryName() == null) {
            return ResponseDto.fail(404, "카테고리 정보를 입력해주세요.");
        }
        categoryService.createCategoryTest(requestDto);
        return ResponseDto.success(requestDto.getCountryName());
    }

    // Todo :: postman 카테고리 수정 test 용
    @PutMapping("/api/category/{id}")
    public ResponseDto<?> updateCategory(@PathVariable Long id,
                                         @RequestBody ThemeCategoryRequestDto requestDto) {
        if (requestDto.getCountryName() == null) {
            return ResponseDto.fail(404, "카테고리 정보를 입력해주세요.");
        }
        return categoryService.updateCategoryTest(id, requestDto);
    }

    @PostMapping("/api/category")
    public ResponseDto<?> createCategory(@RequestBody ThemeCategoryRequestDto requestDto,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        if (requestDto.getCountryName() == null) {
            return ResponseDto.fail(404, "카테고리 정보를 입력해주세요.");
        }
        return categoryService.createCategory(requestDto,userDetails);
    }

    @PutMapping("/api/category/{id}")
    public ResponseDto<?> updateCategory(@PathVariable Long id,
                                         @RequestBody ThemeCategoryRequestDto requestDto,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        if (requestDto.getCountryName() == null) {
            return ResponseDto.fail(404, "카테고리 정보를 입력해주세요.");
        }
        return categoryService.updateCategory(id, requestDto, userDetails);
    }
}
