package com.asid.groupmateai.core.services.impl;

import com.asid.groupmateai.core.ai.openai.clients.ThreadOpenAiClient;
import com.asid.groupmateai.core.ai.openai.clients.VectorStoreOpenAiClient;
import com.asid.groupmateai.core.services.GroupService;
import com.asid.groupmateai.core.services.GroupUserService;
import com.asid.groupmateai.core.services.UserService;
import com.asid.groupmateai.storage.entities.GroupEntity;
import com.asid.groupmateai.storage.entities.GroupUserEntity;
import com.asid.groupmateai.storage.entities.UserEntity;
import com.asid.groupmateai.storage.entities.UserRole;
import com.asid.groupmateai.storage.repositories.GroupUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional
public class GroupUserServiceImpl implements GroupUserService {

    private final GroupUserRepository groupUserRepository;
    private final GroupService groupService;
    private final UserService userService;
    private final ThreadOpenAiClient threadClient;
    private final VectorStoreOpenAiClient vectorStoreClient;

    @Autowired
    public GroupUserServiceImpl(final GroupUserRepository groupUserRepository,
                                final GroupService groupService,
                                final UserService userService,
                                final ThreadOpenAiClient threadClient,
                                final VectorStoreOpenAiClient vectorStoreClient) {
        this.groupUserRepository = groupUserRepository;
        this.groupService = groupService;
        this.userService = userService;
        this.threadClient = threadClient;
        this.vectorStoreClient = vectorStoreClient;
    }

    @Override
    public GroupUserEntity addGroupWithUser(final String groupName, final Long userChatId) {
        String threadId = null;
        String vectorStoreId = null;
        try {
            final UserEntity user = userService.getUser(userChatId);
            final GroupEntity group = groupService.addGroup(groupName);

            vectorStoreId = group.getVectorStoreId();
            threadId = threadClient.createThreadWithVectorStore(vectorStoreId)
                .join()
                .getId();

            final GroupUserEntity groupUserEntity = GroupUserEntity.builder()
                .user(user)
                .group(group)
                .threadId(threadId)
                .userRole(UserRole.ADMIN)
                .metadata(Collections.singletonMap("useQueryKeyboard", String.valueOf(false)))
                .build();

            return groupUserRepository.save(groupUserEntity);
        } catch (final Exception e) {
            if (threadId != null) {
                threadClient.deleteThread(threadId);
            }
            if (vectorStoreId != null) {
                vectorStoreClient.deleteVectorStore(vectorStoreId);
            }

            throw e;
        }
    }

    @Override
    public GroupUserEntity addUserToGroup(final Long groupId, final Long userChatId) {
        String threadId = null;
        try {
            final UserEntity user = userService.getUser(userChatId);
            final GroupEntity group = groupService.getGroup(groupId);

            threadId = threadClient.createThreadWithVectorStore(group.getVectorStoreId())
                .join()
                .getId();

            final GroupUserEntity groupUserEntity = GroupUserEntity.builder()
                .user(user)
                .group(group)
                .threadId(threadId)
                .userRole(UserRole.USER)
                .metadata(Collections.singletonMap("useQueryKeyboard", String.valueOf(false)))
                .build();

            return groupUserRepository.save(groupUserEntity);
        } catch (final Exception e) {
            if (threadId != null) {
                threadClient.deleteThread(threadId);
            }

            throw e;
        }
    }

    @Override
    public GroupUserEntity getGroupUserByChatId(final Long userChatId) {
        return groupUserRepository.findByUserChatId(userChatId)
            .orElse(null);
    }

    @Override
    public boolean groupUserExistsByChatId(final Long userChatId) {
        return groupUserRepository.existsById(userChatId);
    }

    @Override
    public void updateGroupUser(final GroupUserEntity groupUserEntity) {
        groupUserRepository.save(groupUserEntity);
    }

    @Override
    public boolean removeUserFromGroup(final Long userChatId, final boolean deleteGroup) {
        final GroupUserEntity groupUser = getGroupUserByChatId(userChatId);

        if (groupUser != null) {
            if (deleteGroup) {
                final Long groupId = groupUser.getGroup().getId();

                groupUserRepository.findByGroupId(groupId).stream()
                    .map(GroupUserEntity::getThreadId)
                    .forEach(threadClient::deleteThread);
                groupService.removeGroup(groupId);
            } else {
                threadClient.deleteThread(groupUser.getThreadId());
                groupUserRepository.deleteById(userChatId);
            }

            return true;
        }

        return false;
    }
}
