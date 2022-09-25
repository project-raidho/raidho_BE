package com.project.raidho.controller;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.domain.meetingPost.dto.MeetingPostRequestDto;
import com.project.raidho.domain.meetingPost.dto.UpdateMeetingPost;
import com.project.raidho.service.MeetingPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/meeting")
public class MeetingPostController {

    private final MeetingPostService meetingPostService;
    @PostMapping
    public ResponseEntity<?> createMeetingPost(@RequestBody MeetingPostRequestDto meetingPostRequestDto, HttpServletRequest request) throws IOException {
        return ResponseEntity.ok().body(meetingPostService.createMeetingPost(meetingPostRequestDto, request));
    }

    @GetMapping
    public ResponseEntity<?> getAllMeetingPost(@RequestParam (value = "page",defaultValue = "0")int page,
                                               @RequestParam (value = "size",defaultValue = "20")int size,
                                               @AuthenticationPrincipal UserDetails userDetails) throws ParseException {

        return ResponseEntity.ok().body(meetingPostService.getAllMeetingPost(page,size,userDetails));
    }

    @GetMapping("/{themeName}")
    public ResponseEntity<?> getCategoryMeetingPost(@RequestParam (value = "page",defaultValue = "0")int page,
                                                    @RequestParam (value = "size",defaultValue = "20")int size,
                                                    @AuthenticationPrincipal UserDetails userDetails,
                                                    @PathVariable String themeName) throws ParseException {


        return ResponseEntity.ok().body(meetingPostService.getAllCategoryMeetingPost(page,size,userDetails,themeName));
    }

    @GetMapping("/myMeetingPost")
    public ResponseEntity<?> getAllMyMeetingPost(@AuthenticationPrincipal UserDetails userDetails) throws ParseException {
        return meetingPostService.getAllMyMeetingPost(userDetails);
    }

    @DeleteMapping("/{meetingId}")
    public ResponseDto<?> deleteMeetingPost(@PathVariable("meetingId") Long meetingId, @AuthenticationPrincipal UserDetails userDetails){
        return meetingPostService.deleteMeetingPost(meetingId, userDetails);
    }
    @PutMapping("/{meetingId}")
    public ResponseEntity<?> updateMeetingPost(@PathVariable("meetingId") Long meetingId,
                                               @AuthenticationPrincipal UserDetails userDetails,
                                               @ModelAttribute UpdateMeetingPost updateMeetingPost) {
        return meetingPostService.updateMeetingPost(meetingId,userDetails,updateMeetingPost);
    }
}
