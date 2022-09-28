package com.project.raidho.domain.post;

import com.project.raidho.domain.comment.Comment;
import com.project.raidho.domain.s3.MultipartFiles;
import com.project.raidho.domain.postHeart.PostHeart;
import com.project.raidho.domain.Timestamped;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.post.dto.UpdatePostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    private List<MultipartFiles> multipartFiles;

    @Transient
    private List<String> tags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId",nullable = false)
    private Member member;

    @Column
    private int heartCount;

    @Column
    private int commentCount;

    public void update(List<PostHeart> postHearts){
        this.heartCount = postHearts.size();
    }
    public void updatePost(UpdatePostRequestDto updatePostRequestDto){
        this.content= updatePostRequestDto.getContent();
    }

    public void commentCount(List<Comment> comments){
        this.commentCount = comments.size();
    }

}
