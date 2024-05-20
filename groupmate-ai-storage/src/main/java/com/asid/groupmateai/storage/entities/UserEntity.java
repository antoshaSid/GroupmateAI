package com.asid.groupmateai.storage.entities;

import com.asid.groupmateai.storage.utils.MapToJsonConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

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

    @Convert(converter = MapToJsonConverter.class)
    private Map<String, String> metadata;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private GroupUserEntity groupUserEntity;

    public UserEntity(final Long chatId, final UserState userState) {
        this.chatId = chatId;
        this.userState = userState;
    }
}
