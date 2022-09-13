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

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MultipartFiles> multipartFiles;

    @Transient
    private List<String> tags = new ArrayList<>();

    @Transient
    @Column(nullable = false)
    private List<String> locationTags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

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
