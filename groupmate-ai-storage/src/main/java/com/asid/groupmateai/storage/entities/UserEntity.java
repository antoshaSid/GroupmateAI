package com.asid.groupmateai.storage.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USERS")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserEntity {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "user_state")
    @Enumerated(EnumType.STRING)
    private UserState userState;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private GroupUserEntity groupUserEntity;
}
