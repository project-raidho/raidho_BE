package com.project.raidho.domain.tags;

import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.post.dto.UpdatePostRequestDto;
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
public class MeetingTags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String meetingTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meetingPostId",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MeetingPost meetingPost;

}
