package com.asid.groupmateai.core.services;

import com.asid.groupmateai.storage.entities.GroupEntity;

import java.io.IOException;

public interface GroupService {

    GroupEntity addGroup(String name) throws IOException;

    GroupEntity getGroup(Long groupId);

    boolean groupExists(Long groupId);

    void updateGroup(GroupEntity groupEntity);

    void removeGroup(Long groupId);
}
