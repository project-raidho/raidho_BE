package com.project.raidho.service;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.domain.meetingPost.ThemeCategory;
import com.project.raidho.domain.meetingPost.dto.ThemeCategoryRequestDto;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.member.MemberRole;
import com.project.raidho.repository.CategoryRepository;
import com.project.raidho.security.PrincipalDetails;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Builder
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public ResponseDto<?> createCategory(ThemeCategoryRequestDto requestDto, UserDetails userDetails) {
        if (userDetails != null) {
            Member member = ((PrincipalDetails) userDetails).getMember();
            if (member.getRole() == MemberRole.ADMIN) {
                ThemeCategory themeCategory = ThemeCategory.builder()
                        .countryName(requestDto.getCountryName())
                        .build();
                categoryRepository.save(themeCategory);
                return ResponseDto.success(requestDto.getCountryName() + " 가 등록되었습니다.");
            }
            else {
                return ResponseDto.fail(403, "관리자만 등록할 수 있습니다.");
            }
        }
        return ResponseDto.fail(403,"관리자만 등록할 수 있습니다.");
    }

    @Transactional
    public ResponseDto<?> createCategoryTest(ThemeCategoryRequestDto requestDto) {

                ThemeCategory themeCategory = ThemeCategory.builder()
                        .countryName(requestDto.getCountryName())
                        .build();
                categoryRepository.save(themeCategory);
                return ResponseDto.success(requestDto.getCountryName() + " 가 등록되었습니다.");
    }
}
