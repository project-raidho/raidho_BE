package com.project.raidho.domain.s3;

import com.project.raidho.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
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

}
