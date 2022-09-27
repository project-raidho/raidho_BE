package com.project.raidho.service;


import com.project.raidho.domain.ResponseDto;
import com.project.raidho.domain.comment.Comment;
import com.project.raidho.domain.comment.dto.CommentRequestDto;
import com.project.raidho.domain.comment.dto.CommentResponseDto;
import com.project.raidho.domain.comment.dto.UserDetailsCommentDto;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.post.Post;
import com.project.raidho.exception.ErrorCode;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.repository.CommentRepository;
import com.project.raidho.repository.PostRepository;
import com.project.raidho.security.PrincipalDetails;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Builder
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public ResponseDto<?> createComment(Long postId, UserDetails userDetails, CommentRequestDto commentRequestDto) throws RaidhoException {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_POST));
        Member member = new Member();
        Boolean isMine = false;
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        if (member.getProviderId() != null) {
            if (member.getProviderId().equals(post.getMember().getProviderId())) {
                isMine = true;
            }
        }
        Comment comment = Comment.builder()
                .member(member)
                .content(commentRequestDto.getContent())
                .post(post)
                .build();
        commentRepository.save(comment);

        return ResponseDto.success(
                CommentResponseDto.builder()
                        .id(comment.getId())
                        .memberImage(comment.getMember().getMemberImage())
                        .memberName(comment.getMember().getMemberName())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt().toLocalDate())
                        .modifiedAt(comment.getModifiedAt().toLocalDate())
                        .isMine(isMine)
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> getAllCommentsByPost(Long postId, UserDetails userDetails, int page, int size) throws RaidhoException {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseDto.fail(404,"해단 게시글을 찾을 수 없습니다.");
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Comment> commentsList = commentRepository.findAllByOrderByCreatedAtDesc(pageRequest);
        Page<CommentResponseDto> commentResponseDtos = convertToCommentResponseDto(commentsList, userDetails);
        return ResponseDto.success(commentResponseDtos);
    }

    @Transactional
    public  ResponseDto<?> deleteComment (Long commentId, UserDetails userDetails) throws RaidhoException {
        Comment comment = isPresentComment(commentId);
        Member member = new Member();
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        if (null == comment) {
            return ResponseDto.fail(404,"존재하지 않는 댓글 입니다");
        }
        if (!member.getProviderId().equals(comment.getMember().getProviderId())) {
            log.error(ErrorCode.UNAUTHORIZATION_MEMBER.getErrorMessage());
            throw new RaidhoException(ErrorCode.INVALID_AUTH_MEMBER_DELETE);
        } else {
            commentRepository.delete(comment);
            log.info("{} 님의 게시글이 정상적으로 삭제되었습니다.", member.getMemberName());
            return ResponseDto.success("게시글이 정상적으로 댓글이 작제되었습니다.");
        }
    }

    @Transactional(readOnly = true)
    public Comment isPresentComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }


    @Transactional
    public ResponseEntity<?> updateComment (Long commentId, UserDetails userDetails, UserDetailsCommentDto userDetailsCommentDto) {
        Member member = new Member();
        Comment comment = isPresentComment(commentId);
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        if (null == comment) {
            return ResponseEntity.badRequest().body("존재하지 않는 댓글입니다.");
        }
        if (member.getProviderId() !=null) {
            if (member.getProviderId().equals(comment.getMember().getProviderId())) {
                comment.updateComment(userDetailsCommentDto);
                return ResponseEntity.ok().body(
                        CommentResponseDto.builder()
                                .id(comment.getId())
                                .memberImage(comment.getMember().getMemberImage())
                                .memberName(comment.getMember().getMemberName())
                                .content(comment.getContent())
                                .createdAt(comment.getCreatedAt().toLocalDate())
                                .modifiedAt(comment.getModifiedAt().toLocalDate())
                                .build()
                );
            }

        } else {
            return ResponseEntity.badRequest().body("작성자 본인만 수정할수 있습니다.");
        }
                return ResponseEntity.ok().body("댓글이 수정되었습니다");

    }



    private Page<CommentResponseDto> convertToCommentResponseDto(Page<Comment> commentList, UserDetails userDetails) {
        Boolean isMine = false;
        Member member = new Member();
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        List<CommentResponseDto> comments = new ArrayList<>();
        for (Comment comment : commentList) {
            if (member.getProviderId() != null) {
                if (member.getProviderId().equals(comment.getMember().getProviderId())) {
                    isMine = true;
                }
            }
            comments.add(
                    CommentResponseDto.builder()
                            .id(comment.getId())
                            .memberImage(comment.getMember().getMemberImage())
                            .memberName(comment.getMember().getMemberName())
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt().toLocalDate())
                            .modifiedAt(comment.getModifiedAt().toLocalDate())
                            .isMine(isMine)
                            .build()
            );
            isMine = false;
        }
        return new PageImpl<>(comments, commentList.getPageable(), commentList.getTotalElements());
    }
}