package com.project.raidho.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Images {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    public Images(String imgUrl, Post post) {
        this.imgUrl = imgUrl;
        this.post = post;
    }
}
