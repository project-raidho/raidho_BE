package com.project.raidho.service;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.domain.meetingPost.ThemeCategory;
import com.project.raidho.domain.meetingPost.dto.ThemeCategoryRequestDto;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.member.MemberRole;
import com.project.raidho.repository.ThemeCategoryRepository;
import com.project.raidho.security.PrincipalDetails;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Builder
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final ThemeCategoryRepository themeCategoryRepository;

    @Transactional
    public ResponseDto<?> createCategory(ThemeCategoryRequestDto requestDto, UserDetails userDetails) {
        if (userDetails != null) {
            Member member = ((PrincipalDetails) userDetails).getMember();
            if (member.getRole() == MemberRole.ADMIN) {
                ThemeCategory themeCategory = ThemeCategory.builder()
                        .countryName(requestDto.getCountryName())
                        .build();
                themeCategoryRepository.save(themeCategory);
                return ResponseDto.success(requestDto.getCountryName() + " 가 등록되었습니다.");
            }
            else {
                return ResponseDto.fail(403, "관리자만 등록할 수 있습니다.");
            }
        }
        return ResponseDto.fail(403,"관리자만 등록할 수 있습니다.");
    }

    @Transactional
    public ResponseDto<?> updateCategory(Long id, ThemeCategoryRequestDto requestDto , UserDetails userDetails) {
        if (userDetails != null) {
            Member member = ((PrincipalDetails) userDetails).getMember();
            if (member.getRole() == MemberRole.ADMIN) {
                ThemeCategory themeCategory = isPresentTheme(id);
                themeCategory.updateCategory(requestDto);
                ThemeCategory updateCategory = isPresentTheme(id);
                return ResponseDto.success(updateCategory.getCountryName());
            } else {
                return ResponseDto.fail(403, "관리자만 변경할 수 있습니다.");
            }
        }
        return ResponseDto.fail(403,"관리자만 변경할 수 있습니다.");
    }

    @Transactional
    public ResponseDto<?> deleteCategory(Long id, UserDetails userDetails){
        if (userDetails != null) {
            Member member = ((PrincipalDetails) userDetails).getMember();
            if (member.getRole() == MemberRole.ADMIN) {
                ThemeCategory themeCategory = isPresentTheme(id);
                themeCategoryRepository.deleteById(id);
                return ResponseDto.success("삭제가 완료 되었습니다.");
            }else {
                return ResponseDto.fail(403, "관리자만 변경할 수 있습니다.");
            }
        }
        return ResponseDto.fail(403,"관리자만 변경할 수 있습니다.");
    }

    // Todo :: Test 배포 전 삭제 해야함
    @Transactional
    public ResponseDto<?> createCategoryTest(ThemeCategoryRequestDto requestDto) {

        ThemeCategory themeCategory = ThemeCategory.builder()
                .countryName(requestDto.getCountryName())
                .build();
        themeCategoryRepository.save(themeCategory);
        return ResponseDto.success(requestDto.getCountryName() + " 가 등록되었습니다.");
    }

    // Todo :: Test 배포 전 삭제 해야함
    @Transactional
    public ResponseDto<?> updateCategoryTest(Long id, ThemeCategoryRequestDto requestDto) {
                ThemeCategory themeCategory = isPresentTheme(id);
                themeCategory.updateCategory(requestDto);
                ThemeCategory updateCategory = isPresentTheme(id);
                return ResponseDto.success(updateCategory.getCountryName());
    }

    // Todo :: Test 배포 전 삭제 해야함
    public ResponseDto<?> deleteCategoryTest(Long id) {
        ThemeCategory themeCategory = isPresentTheme(id);
        themeCategoryRepository.deleteById(id);
        return ResponseDto.success("삭제가 완료 되었습니다.");
    }

    @Transactional(readOnly = true)
    public ThemeCategory isPresentTheme(Long id) {
        Optional<ThemeCategory> themeCategory = themeCategoryRepository.findById(id);
        return themeCategory.orElseThrow(() -> new IllegalArgumentException("등록되지 않은 지역입니다."));
    }
}
