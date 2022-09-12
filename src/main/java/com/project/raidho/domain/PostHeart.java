//package com.project.raidho.domain;
//
//import com.project.raidho.domain.member.Member;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Builder
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//public class PostHeart {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    @Column
//    private int postHeart;
//
//    @JoinColumn(name = "member_id", nullable = false)
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Member member;
//
//    @JoinColumn(name = "post_id", nullable = false)
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Post post;
//
//}
