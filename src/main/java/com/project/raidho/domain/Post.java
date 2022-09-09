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

    @Column(nullable = false)
    private String content;

    @Column
    private String tags;

    @Transient
    private final List<Images> imgList = new ArrayList<>();

    @Column
    private String locationTags;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "memberId",nullable = false)
//    private Member member;

//    public Post(String content, Member member) {
//        this.content = content;
//        this.member = member;
//    }

}
