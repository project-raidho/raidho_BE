package com.project.raidho.domain.meetingPost;

import com.project.raidho.domain.Timestamped;
import com.project.raidho.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class MeetingPost extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "THEME_CATEGORY_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ThemeCategory themeCategory;

    @Transient
    private List<String> meetingTags = new ArrayList<>();

    @Column
    private String title;

    @Column(name = "EXDESC")
    private String desc;

    @Column
    private String startDate;

    @Column
    private String endDate;

    @Column
    private int people;

    @Column
    private String roomCloseDate;

    @Column
    private String departLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_Id",nullable = false)
    private Member member;
}
