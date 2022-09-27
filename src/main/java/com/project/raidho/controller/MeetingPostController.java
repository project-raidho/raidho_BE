package com.project.raidho.controller;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.domain.meetingPost.dto.MeetingPostRequestDto;
import com.project.raidho.domain.meetingPost.dto.UpdateMeetingPost;
import com.project.raidho.exception.RaidhoException;
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
                                                    @PathVariable String themeName) throws ParseException, RaidhoException {


        return ResponseEntity.ok().body(meetingPostService.getAllCategoryMeetingPost(page,size,userDetails,themeName));
    }

    @GetMapping("/myMeetingPost")
    public ResponseEntity<?> getAllMyMeetingPost(@AuthenticationPrincipal UserDetails userDetails) throws ParseException {
        return meetingPostService.getAllMyMeetingPost(userDetails);
    }

    @GetMapping("/filter/{num}/{category}")
    public ResponseEntity<?> getOpenMeetingPost(@RequestParam (value = "page",defaultValue = "0")int page,
                                                @RequestParam (value = "size",defaultValue = "20")int size,
                                                @AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable int num,
                                                @PathVariable String category) throws RaidhoException {
        if (num == 1) {
            return ResponseEntity.ok().body(meetingPostService.getOpenMeetingRoomAndCategory(page, size, userDetails, category));
        }
        return ResponseEntity.badRequest().body("Bad Request");
    }

    @GetMapping("/filter/{num}")
    public ResponseEntity<?> getOpenMeetingPost(@RequestParam (value = "page",defaultValue = "0")int page,
                                                @RequestParam (value = "size",defaultValue = "20")int size,
                                                @AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable int num) {
        if (num == 1) {
            return ResponseEntity.ok().body(meetingPostService.getOpenMeetingRoom(page, size, userDetails));
        }
        return ResponseEntity.badRequest().body("Bad Request");
    }


    @DeleteMapping("/{meetingId}")
    public ResponseDto<?> deleteMeetingPost(@PathVariable("meetingId") Long meetingId, @AuthenticationPrincipal UserDetails userDetails) throws RaidhoException {
        return meetingPostService.deleteMeetingPost(meetingId, userDetails);
    }
    @PutMapping("/{meetingId}")
    public ResponseEntity<?> updateMeetingPost(@PathVariable("meetingId") Long meetingId,
                                               @AuthenticationPrincipal UserDetails userDetails,
                                               @RequestBody UpdateMeetingPost updateMeetingPost) throws RaidhoException {
        return meetingPostService.updateMeetingPost(meetingId,userDetails,updateMeetingPost);
    }
}
