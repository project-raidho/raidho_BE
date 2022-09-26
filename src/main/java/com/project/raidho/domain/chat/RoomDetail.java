package com.project.raidho.domain.chat;

import com.project.raidho.domain.Timestamped;
import com.project.raidho.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDetail extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_master_id", nullable = false)
    private RoomMaster roomMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column
    private Long chatId;

    public void updateChatId(Long chatid){
        this.chatId = chatid;
    }

    public RoomDetail(RoomMaster roomMaster, Member member) {
        this.roomMaster = roomMaster;
        this.member = member;
    }

}
