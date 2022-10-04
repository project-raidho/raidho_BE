package com.project.raidho.domain.chat;

import com.project.raidho.domain.Timestamped;
import com.project.raidho.domain.member.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatMessage extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long roomId;

    @Column
    private String message;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public enum Type {
        ENTER, TALK, QUIT
    }

    @Column
    private Type type;

    @Column
    private String sender;

    @Column
    private String memberImage;

    @Column
    private String messageTime;

    public void MessageMemberUpdate(Member member) {
        this.sender = member.getMemberName();
        this.memberImage = member.getMemberImage();
    }

}
