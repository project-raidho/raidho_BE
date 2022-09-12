//package com.project.raidho.domain;

//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//import java.util.List;
//
//@Entity
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//public class Tags {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    @Column
//    private String tag;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "postId",nullable = false)
//    private Post post;
//
//    public void update (List<Tags> tags) {
//        this.tag = tag;
//    }
//}
