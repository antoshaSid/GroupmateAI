package com.asid.groupmateai.core.services;

import com.asid.groupmateai.storage.entities.GroupUserEntity;

import java.io.IOException;

public interface GroupUserService {

    GroupUserEntity addGroupWithUser(String groupName, Long userChatId) throws IOException;

    GroupUserEntity addUserToGroup(Long groupId, Long userChatId);

    GroupUserEntity getGroupUserByChatId(Long userChatId);

    boolean groupUserExistsByChatId(Long userChatId);

    void updateGroupUser(GroupUserEntity groupUserEntity);

    int countGroupUsersByGroupId(Long groupId);

    boolean removeUserFromGroup(Long userChatId, boolean deleteGroup);
}
