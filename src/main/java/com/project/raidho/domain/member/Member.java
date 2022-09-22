package com.project.raidho.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String memberName;

    @Column
    private String email;

    private String memberImage;

    private String memberIntro;

    private String provider; //소셜 종류 구분 version1

    private String providerId; //소셜 종류 구분 version2

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberRole role;

    @Builder
    public Member(String memberName, String email, String memberImage, String memberIntro, String provider, String providerId, MemberRole role) {
        this.memberName = memberName;
        this.email = email;
        this.memberImage = memberImage;
        this.memberIntro = memberIntro;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }

    public void update(String memberImage, String memberIntro){
        this.memberImage=memberImage;
        this.memberIntro=memberIntro;
    }
}
