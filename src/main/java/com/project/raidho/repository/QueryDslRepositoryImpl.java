package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.MeetingPost;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@RequiredArgsConstructor
public class QueryDslRepositoryImpl implements QueryDslRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MeetingPost> findGetOpenMeetingRoom(String date, PageRequest pageRequest) {
        return null;
    }
}
