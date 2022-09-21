package com.project.raidho.domain.chat;

import com.project.raidho.domain.Timestamped;
import com.project.raidho.domain.meetingPost.MeetingPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoomMaster extends Timestamped {

    @Id
    private Long roomId;

    @JoinColumn(name = "meetingPostId")
    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MeetingPost meetingPost;

    @Column
    private String roomName;

    @Column
    private String recentChat;

    @Column
    private String roomPic;

    @OneToMany(mappedBy = "roomMaster",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<RoomDetail> roomDetails = new ArrayList<>();

}
