package com.project.raidho.controller;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.security.PrincipalDetails;
import com.project.raidho.service.TagSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
                                       @RequestParam (value = "size",defaultValue = "5")int size,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(tagSearchService.searchTag(page, size, tag, userDetails));
    }
    // 구인글 태그 검색
    @GetMapping("/meeting/{meetingtag}")
    public ResponseEntity<?> MeetingTagSeatch(@PathVariable(value = "meetingtag") String meetingTag,
                                              @RequestParam (value = "page",defaultValue = "0")int page,
                                              @RequestParam (value = "size",defaultValue = "5")int size,
                                              @AuthenticationPrincipal UserDetails userDetails) throws ParseException {
        return ResponseEntity.ok().body(tagSearchService.meetingTagSeatch(page, size, meetingTag, userDetails));
    }

}
