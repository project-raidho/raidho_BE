//package com.project.raidho.domain;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//public class Chat extends Timestamped{
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    @Column(nullable = false)
//    private String title;
//
//    @Column(nullable = false)
//    private String content;
//
//    @Transient
//    @Column(nullable = false)
//    private List<String> chatTags = new ArrayList<>();
//// 테마 카테고리
//// 여행장소 카테고리
//// 모집인원 카테고리(10명)
//
//}
