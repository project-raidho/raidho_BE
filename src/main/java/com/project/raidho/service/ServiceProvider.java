package com.project.raidho.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.project.raidho.domain.IsMineDto;
import com.project.raidho.domain.MeetingStatusDto;
import com.project.raidho.domain.chat.RoomDetail;
import com.project.raidho.domain.chat.RoomMaster;
import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.meetingPost.dto.MeetingPostResponseDto;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.post.Post;
import com.project.raidho.domain.post.dto.MainPostResponseDto;
import com.project.raidho.domain.s3.MultipartFiles;
import com.project.raidho.domain.tags.MeetingTags;
import com.project.raidho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceProvider {

    private final RoomDetailRepository roomDetailRepository;
    private final MeetingPostStarRepository meetingPostStarRepository;
    private final RoomMasterRepository roomMasterRepository;
    private final MeetingTagRepository meetingTagRepository;
    private final PostHeartRepository postHeartRepository;
    private final CommentRepository commentRepository;
    private final ImgRepository imgRepository;

    public IsMineDto isMineCheck_Post(Member member, Post post) {
        IsMineDto isMineDto = new IsMineDto();
        if (member.getProviderId() != null) {
            if (member.getProviderId().equals(post.getMember().getProviderId())) {
                isMineDto.setMine(true);
            }
            int isHeartMineCh = postHeartRepository.getCountOfPostAndMemberPostHeart(post, member);
            if (isHeartMineCh >= 1) {
                isMineDto.setHeartMine(true);
            }
        }
        isMineDto.setHeartCount(postHeartRepository.getCountOfPostHeart(post));
        isMineDto.setCommentCount(commentRepository.getCountOfComment(post));
        return isMineDto;
    }

    public IsMineDto isMineCheck_MeetingPost(Member member, RoomMaster roomMaster, MeetingPost meetingPost) {
        IsMineDto isMineDto = new IsMineDto();
        if (member.getProviderId() != null) {
            if (member.getProviderId().equals(meetingPost.getMember().getProviderId())) {
                isMineDto.setMine(true);
            }
            RoomDetail roomDetails = roomDetailRepository.findByRoomMasterAndMember(roomMaster, member);
            if (roomDetails != null) {
                isMineDto.setAlreadyMine(true);
            }
            int isStarMineCh = meetingPostStarRepository.getCountOfMeetingPostAndMemberMeetingPostStar(meetingPost, member);
            if (isStarMineCh >= 1) {
                isMineDto.setStarMine(true);
            }
        }
        return isMineDto;
    }

    public MeetingStatusDto dateCheck(MeetingPost meetingPost, int memberCount) throws ParseException {
        MeetingStatusDto meetingStatusDto = new MeetingStatusDto();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(meetingPost.getRoomCloseDate());
        Date tomorrow = new Date(date.getTime() + (1000 * 60 * 60 * 24));
        if (tomorrow.after(new Date()) && (meetingPost.getPeople() > memberCount)) {
            meetingStatusDto.setMeetingStatus(1);
        } else if (tomorrow.after(new Date()) && memberCount >= meetingPost.getPeople()) {
            meetingStatusDto.setMeetingStatus(2);
        } else if (tomorrow.before(new Date())) {
            meetingStatusDto.setMeetingStatus(3);
        }
        return meetingStatusDto;
    }

    public List<MeetingPostResponseDto> meetingPost(List<MeetingPost> meetingPostList, Member member) throws ParseException {
        List<MeetingPostResponseDto> meetingPostResponseDtos = new ArrayList<>();
        for (MeetingPost meetingPost : meetingPostList) {
            RoomMaster roomMaster = roomMasterRepository.findByRoomId(meetingPost.getId())
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 채팅방입니다."));
            int memberCount = roomDetailRepository.getCountJoinRoomMember(roomMaster);
            IsMineDto isMineDto = isMineCheck_MeetingPost(member, roomMaster, meetingPost);
            MeetingStatusDto meetingStatusDto = dateCheck(meetingPost,memberCount);
            List<MeetingTags> meetingTags = meetingTagRepository.findAllByMeetingPost(meetingPost);
            List<String> stringTagList = new ArrayList<>();
            for (MeetingTags mt : meetingTags) {
                stringTagList.add(mt.getMeetingTag());
            }
            meetingPostResponseDtos.add(
                    MeetingPostResponseDto.builder()
                            .id(meetingPost.getId())
                            .themeCategory(meetingPost.getThemeCategory().getCountryName())
                            .title(meetingPost.getTitle())
                            .desc(meetingPost.getDesc())
                            .departLocation(meetingPost.getDepartLocation())
                            .startDate(meetingPost.getStartDate())
                            .endDate(meetingPost.getEndDate())
                            .people(meetingPost.getPeople())
                            .memberCount(memberCount)
                            .isStarMine(isMineDto.isStarMine())
                            .roomCloseDate(meetingPost.getRoomCloseDate())
                            .isMine(isMineDto.isMine())
                            .isAlreadyJoin(isMineDto.isAlreadyMine())
                            .meetingTags(stringTagList)
                            .meetingStatus(meetingStatusDto.getMeetingStatus())
                            .memberName(meetingPost.getMember().getMemberName())
                            .memberImage(meetingPost.getMember().getMemberImage())
                            .build()
            );
        }
        return meetingPostResponseDtos;
    }

    public List<MeetingPostResponseDto> meetingPostPage(Page<MeetingPost> meetingPostList, Member member) throws ParseException {
        List<MeetingPostResponseDto> meetingPostResponseDtos = new ArrayList<>();
        for (MeetingPost meetingPost : meetingPostList) {
            RoomMaster roomMaster = roomMasterRepository.findByRoomId(meetingPost.getId())
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 채팅방입니다."));
            int memberCount = roomDetailRepository.getCountJoinRoomMember(roomMaster);
            IsMineDto isMineDto = isMineCheck_MeetingPost(member, roomMaster, meetingPost);
            MeetingStatusDto meetingStatusDto = dateCheck(meetingPost,memberCount);
            List<MeetingTags> meetingTags = meetingTagRepository.findAllByMeetingPost(meetingPost);
            List<String> stringTagList = new ArrayList<>();
            for (MeetingTags mt : meetingTags) {
                stringTagList.add(mt.getMeetingTag());
            }
            meetingPostResponseDtos.add(
                    MeetingPostResponseDto.builder()
                            .id(meetingPost.getId())
                            .themeCategory(meetingPost.getThemeCategory().getCountryName())
                            .title(meetingPost.getTitle())
                            .desc(meetingPost.getDesc())
                            .departLocation(meetingPost.getDepartLocation())
                            .startDate(meetingPost.getStartDate())
                            .endDate(meetingPost.getEndDate())
                            .people(meetingPost.getPeople())
                            .memberCount(memberCount)
                            .isStarMine(isMineDto.isStarMine())
                            .roomCloseDate(meetingPost.getRoomCloseDate())
                            .isMine(isMineDto.isMine())
                            .isAlreadyJoin(isMineDto.isAlreadyMine())
                            .meetingTags(stringTagList)
                            .meetingStatus(meetingStatusDto.getMeetingStatus())
                            .memberName(meetingPost.getMember().getMemberName())
                            .memberImage(meetingPost.getMember().getMemberImage())
                            .build()
            );
        }
        return meetingPostResponseDtos;
    }

    public List<MainPostResponseDto> post(List<Post> postList, Member member) {
        List<MainPostResponseDto> posts = new ArrayList<>();
        for (Post post : postList) {
            IsMineDto isMineDto = isMineCheck_Post(member,post);
            List<MultipartFiles> multipartFile = imgRepository.findAllByPost_Id(post.getId());
            if (multipartFile.size() > 1) {
                isMineDto.setImages(true);
            }
            List<String> multipartFiles = new ArrayList<>();
            for (MultipartFiles c : multipartFile) {
                multipartFiles.add(c.getMultipartFiles());
            }
            posts.add(
                    MainPostResponseDto.builder()
                            .id(post.getId())
                            .memberName(post.getMember().getMemberName())
                            .memberImage(post.getMember().getMemberImage())
                            .multipartFiles(Collections.singletonList(multipartFiles.get(0)))
                            .heartCount(isMineDto.getHeartCount())
                            .commentCount(isMineDto.getCommentCount())
                            .isMine(isMineDto.isMine())
                            .isHeartMine(isMineDto.isHeartMine())
                            .isImages(isMineDto.isImages())
                            .createdAt(post.getCreatedAt().toLocalDate())
                            .modifiedAt(post.getModifiedAt().toLocalDate())
                            .build()
            );
        }
        return posts;
    }

    public List<MainPostResponseDto> postPage(Page<Post> postList, Member member) {
        List<MainPostResponseDto> posts = new ArrayList<>();
        for (Post post : postList) {
            IsMineDto isMineDto = isMineCheck_Post(member,post);
            List<MultipartFiles> multipartFile = imgRepository.findAllByPost_Id(post.getId());
            if (multipartFile.size() > 1) {
                isMineDto.setImages(true);
            }
            List<String> multipartFiles = new ArrayList<>();
            for (MultipartFiles c : multipartFile) {
                multipartFiles.add(c.getMultipartFiles());
            }
            posts.add(
                    MainPostResponseDto.builder()
                            .id(post.getId())
                            .memberName(post.getMember().getMemberName())
                            .memberImage(post.getMember().getMemberImage())
                            .multipartFiles(Collections.singletonList(multipartFiles.get(0)))
                            .heartCount(isMineDto.getHeartCount())
                            .commentCount(isMineDto.getCommentCount())
                            .isMine(isMineDto.isMine())
                            .isHeartMine(isMineDto.isHeartMine())
                            .isImages(isMineDto.isImages())
                            .createdAt(post.getCreatedAt().toLocalDate())
                            .modifiedAt(post.getModifiedAt().toLocalDate())
                            .build()
            );
        }
        return posts;
    }
}
