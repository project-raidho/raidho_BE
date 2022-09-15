package com.project.raidho.controller;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.service.TagSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TagSearchController {

    private final TagSearchService tagSearchService;

    @GetMapping("/api/search")
    public ResponseDto<?> TagSearch(@RequestParam(value = "tag") String tag,
                                    @RequestParam (value = "page",defaultValue = "0")int page,
                                    @RequestParam (value = "size",defaultValue = "5")int size,
                                    @AuthenticationPrincipal UserDetails userDetails) {

        return tagSearchService.searchTag(page, size, tag, userDetails);
    }
}
