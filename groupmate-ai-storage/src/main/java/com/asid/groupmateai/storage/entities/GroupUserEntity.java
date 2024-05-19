package com.asid.groupmateai.storage.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GROUP_USERS")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GroupUserEntity {

    @Id
    @Column(name = "user_chat_id")
    private Long userChatId;

    @OneToOne
    @JoinColumn(name = "user_chat_id")
    @MapsId
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @Column(name = "thread_id", unique = true, nullable = false)
    private String threadId;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
}
