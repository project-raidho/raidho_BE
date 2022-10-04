package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.post.Post;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static com.project.raidho.domain.meetingPost.QMeetingPost.meetingPost;
import static com.project.raidho.domain.post.QPost.post;

@RequiredArgsConstructor
public class QueryDslRepositoryImpl implements QueryDslRepository{

    private final JPAQueryFactory queryFactory;

    // Todo :: Post
    @Override
    public Page<Post> getAllPost(Pageable pageable) {
        QueryResults<Post> results = queryFactory
                .selectFrom(post)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    // Todo :: MeetingPost
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

    @Override
    public Page<MeetingPost> findGetOpenMeetingRoomAndCategory(String date, Long id, Pageable pageable) {
        QueryResults<MeetingPost> results = queryFactory
                .selectFrom(meetingPost)
                .where(meetingPost.roomCloseDate.goe(date),
                        meetingPost.themeCategory.id.eq(id))
                .orderBy(meetingPost.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(results.getResults(),pageable, results.getTotal());
    }

    @Override
    public Page<MeetingPost> findGetOpenMeetingRoomWhereStartDate(String start, String end, String date, Pageable pageable) {
        return null;
    }

    @Override
    public Page<MeetingPost> findGetOpenMeetingRoomWhereStartDateAndCategory(String start, String end, String date, Long id, Pageable pageable) {
        QueryResults<MeetingPost> results = queryFactory
                .selectFrom(meetingPost)
                .where(meetingPost.roomCloseDate.goe(date),
                        meetingPost.startDate.goe(start),
                        meetingPost.endDate.loe(end),
                        meetingPost.themeCategory.id.eq(id))
                .orderBy(meetingPost.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(results.getResults(),pageable, results.getTotal());
    }
}
