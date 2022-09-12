package com.project.raidho.domain;

import com.project.raidho.domain.member.Member;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String content;

    @Transient
    private final List<Images> imgList = new ArrayList<>();

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "memberId",nullable = false)
//    private Member member;

//    public Post(String content, Member member) {
//        this.content = content;
//        this.member = member;
//    }
//    @Column
//    private int heartCount;

//    public void update(List<PostHeart> postHearts){
//        this.heartCount = postHearts.size();
//    }

}
