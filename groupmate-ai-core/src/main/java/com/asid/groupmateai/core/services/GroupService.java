package com.asid.groupmateai.core.services;

import com.asid.groupmateai.storage.entities.GroupEntity;

public interface GroupService {

    GroupEntity addGroup(String name);

    GroupEntity getGroup(Long groupId);

    boolean groupExists(Long groupId);

    void updateGroup(GroupEntity groupEntity);

    void removeGroup(Long groupId);
}
