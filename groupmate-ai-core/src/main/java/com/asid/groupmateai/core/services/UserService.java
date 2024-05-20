package com.asid.groupmateai.core.services;

import com.asid.groupmateai.storage.entities.UserEntity;
import com.asid.groupmateai.storage.entities.UserState;

import java.util.Map;

public interface UserService {

    UserEntity addUser(Long chatId);

    UserEntity getUser(Long chatId);

    boolean userExists(Long chatId);

    UserState getUserState(Long chatId);

    void updateUserState(Long chatId, UserState userState);

    void updateUserState(Long chatId, UserState userState, Map<String, String> metadata);
}
