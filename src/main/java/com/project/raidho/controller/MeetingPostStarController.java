package com.project.raidho.controller;

import com.project.raidho.exception.RaidhoException;
import com.project.raidho.service.MeetingPostStarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/meetingPostStar")
public class MeetingPostStarController {

    private final MeetingPostStarService meetingPostStarService;

    @PostMapping("/{meetingPostId}")
    public ResponseEntity<?> meetingPostStarCreate(@PathVariable("meetingPostId") Long meetingPostId, HttpServletRequest httpServletRequest) throws RaidhoException {
        return meetingPostStarService.createMeetingPostStar(meetingPostId, httpServletRequest);
    }

    @DeleteMapping("/{meetingPostId}")
    public ResponseEntity<?> meetingPostStarDelete(@PathVariable("meetingPostId") Long meetingPostId, HttpServletRequest httpServletRequest) throws RaidhoException {
        return meetingPostStarService.deleteMeetingPostStar(meetingPostId, httpServletRequest);
    }
}