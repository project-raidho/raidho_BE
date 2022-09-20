package com.project.raidho.controller;

import com.amazonaws.Response;
import com.project.raidho.domain.ResponseDto;
import com.project.raidho.domain.meetingPost.dto.ThemeCategoryRequestDto;
import com.project.raidho.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PutMapping("/api/category/test/{id}")
    public ResponseDto<?> updateCategory(@PathVariable Long id,
                                         @RequestBody ThemeCategoryRequestDto requestDto) {
        if (requestDto.getCountryName() == null) {
            return ResponseDto.fail(404, "카테고리 정보를 입력해주세요.");
        }
        return categoryService.updateCategoryTest(id, requestDto);
    }
    // Todo :: postman 카테고리 삭제 test 용
    @DeleteMapping("/api/category/test/{id}")
    public ResponseDto<?> deleteCategory(@PathVariable Long id) {

        return categoryService.deleteCategoryTest(id);
    }

    @PostMapping("/api/category")
    public ResponseEntity<?> createCategory(@RequestBody ThemeCategoryRequestDto requestDto,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        if (requestDto.getCountryName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.fail(404, "카테고리 정보를 입력해주세요."));
        }
        return ResponseEntity.ok().body(categoryService.createCategory(requestDto,userDetails));
    }

    @PutMapping("/api/category/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id,
                                         @RequestBody ThemeCategoryRequestDto requestDto,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        if (requestDto.getCountryName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.fail(404, "카테고리 정보를 입력해주세요."));
        }
        return ResponseEntity.ok().body(categoryService.updateCategory(id, requestDto, userDetails));
    }

    @DeleteMapping("/api/category/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok().body(categoryService.deleteCategory(id, userDetails));
    }
}
