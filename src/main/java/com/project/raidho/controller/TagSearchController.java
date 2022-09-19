package com.project.raidho.controller;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.security.PrincipalDetails;
import com.project.raidho.service.TagSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class TagSearchController {

    private final TagSearchService tagSearchService;

    @GetMapping("/api/search/{tag}")
    public ResponseEntity<?> TagSearch(@PathVariable(value = "tag") String tag,
                                       @RequestParam (value = "page",defaultValue = "0")int page,
                                       @RequestParam (value = "size",defaultValue = "5")int size,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(tagSearchService.searchTag(page, size, tag, userDetails));
    }
}
