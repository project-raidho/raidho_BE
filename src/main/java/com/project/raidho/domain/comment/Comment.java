package com.project.raidho.domain.comment;

import com.project.raidho.domain.Timestamped;
import com.project.raidho.domain.comment.dto.UserDetailsCommentDto;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment extends Timestamped {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @JoinColumn(name = "member_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "post_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    public void updateComment(UserDetailsCommentDto userDetailsCommentDto){
        this.content= userDetailsCommentDto.getContent();
    }
}

