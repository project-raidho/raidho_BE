package com.project.raidho.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class MultipartFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String multipartFiles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    public MultipartFiles(String multipartFiles, Post post) {
        this.multipartFiles = multipartFiles;
        this.post = post;
    }
}
