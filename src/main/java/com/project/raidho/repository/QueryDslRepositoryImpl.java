package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.MeetingPost;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static com.project.raidho.domain.meetingPost.QMeetingPost.meetingPost;

@RequiredArgsConstructor
public class QueryDslRepositoryImpl implements QueryDslRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MeetingPost> findGetOpenMeetingRoom(String date, Pageable pageable) {
        QueryResults<MeetingPost> results = queryFactory
                .selectFrom(meetingPost)
                .where(meetingPost.roomCloseDate.goe(date))
                .orderBy(meetingPost.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(results.getResults(),pageable, results.getTotal());
    }
}
