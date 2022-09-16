package com.project.raidho.domain.meetingPost;

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
public class ThemeCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "COUNTRY_NAME", nullable = false)
    private String countryName;

    @OneToMany(mappedBy = "themeCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<MeetingPost> meetingPostList= new ArrayList<>();
}
