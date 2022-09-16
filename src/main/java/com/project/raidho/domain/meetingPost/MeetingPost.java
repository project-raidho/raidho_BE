package com.project.raidho.domain.meetingPost;

import com.project.raidho.domain.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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


}
