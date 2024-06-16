package com.asid.groupmateai.core.services;

import com.asid.groupmateai.storage.entities.GroupEntity;
import com.asid.groupmateai.storage.entities.GroupUserEntity;
import com.asid.groupmateai.storage.entities.UserRole;

import java.io.IOException;
import java.util.List;

public interface GroupUserService {

    GroupUserEntity addGroupWithUser(String groupName, Long userChatId) throws IOException;

    GroupUserEntity addUserToGroup(Long groupId, Long userChatId);

    GroupUserEntity getGroupUserByChatId(Long userChatId);

    GroupEntity getGroupByChatId(Long userChatId);

    UserRole getUserRoleByChatId(Long userChatId);

    void updateGroupUser(GroupUserEntity groupUserEntity);

    int countGroupUsersByGroupId(Long groupId);

    /**
     * Remove user from group and delete group if needed
     *
     * @param userChatId user chat id to delete
     * @param deleteGroup deletes group if true
     * @return list of users who were in the group
     */
    List<Long> removeUserFromGroup(Long userChatId, boolean deleteGroup) throws IOException;
}
