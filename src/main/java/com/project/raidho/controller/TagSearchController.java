package com.project.raidho.controller;

import com.project.raidho.service.TagSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search")
public class TagSearchController {

    private final TagSearchService tagSearchService;
    // 자랑글 태그 검색
    @GetMapping("/{tag}")
    public ResponseEntity<?> TagSearch(@PathVariable(value = "tag") String tag,
                                       @RequestParam (value = "page",defaultValue = "0")int page,
                                       @RequestParam (value = "size",defaultValue = "10000")int size,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(tagSearchService.searchTag(page, size, tag, userDetails));
    }

    // 관련 여행 후기 태그 검색용
    @GetMapping("/review/{tag}/{id}")
    public ResponseEntity<?> distinctMyPostSearchTag(@PathVariable(value = "tag") String tag,
                                                     @PathVariable(value = "id") Long id,
                                                     @RequestParam (value = "page",defaultValue = "0")int page,
                                                     @RequestParam (value = "size",defaultValue = "10000")int size,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(tagSearchService.distinctMyPostSearchTag(page, size, tag, id, userDetails));
    }

    // 구인글 태그 검색
    @GetMapping("/meeting/{meetingTag}")
    public ResponseEntity<?> MeetingTagSearch(@PathVariable(value = "meetingTag") String meetingTag,
                                              @RequestParam (value = "page",defaultValue = "0")int page,
                                              @RequestParam (value = "size",defaultValue = "10000")int size,
                                              @AuthenticationPrincipal UserDetails userDetails) throws ParseException {
        return ResponseEntity.ok().body(tagSearchService.meetingTagSearch(page, size, meetingTag, userDetails));
    }

}
