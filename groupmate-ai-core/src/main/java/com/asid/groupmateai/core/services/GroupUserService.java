package com.asid.groupmateai.core.services;

import com.asid.groupmateai.storage.entities.GroupUserEntity;

public interface GroupUserService {

    GroupUserEntity addGroupWithUser(String groupName, Long userChatId);

    GroupUserEntity addUserToGroup(Long groupId, Long userChatId);

    GroupUserEntity getGroupUserByChatId(Long userChatId);

    boolean groupUserExistsByChatId(Long userChatId);

    GroupUserEntity updateGroupUser(GroupUserEntity groupUserEntity);

    void removeGroupUserByChatId(Long userChatId);
}
