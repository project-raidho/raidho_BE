package com.project.raidho.controller;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.post.dto.PostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/meeting")
public class MeetingPostController {
    private final MeetingPost meetingPost;
    @PostMapping
    public ResponseEntity<?> createMeetingPost(@RequestBody PostRequestDto postRequestDto, UserDetails userDetails) throws IOException {
        meetingPost.createMeetingPost(postRequestDto, userDetails);
        return ResponseEntity.ok().body(ResponseDto.success("ok"));
    }
}
