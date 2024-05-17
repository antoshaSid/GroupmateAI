package com.asid.groupmateai.storage.repositories;

import com.asid.groupmateai.storage.TestStorageModuleConfiguration;
import com.asid.groupmateai.storage.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = TestStorageModuleConfiguration.class)
class GroupUserRepositoryTest {

    private static final Long USER_CHAT_ID = 1L;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupUserRepository groupUserRepository;

    private UserEntity userEntity;
    private GroupEntity groupEntity;
    private GroupUserEntity groupUserEntity;

    @BeforeEach
    void setUp() {
        this.userEntity = userRepository.saveAndFlush(new UserEntity(USER_CHAT_ID, UserState.IDLE));
        assertNotNull(this.userEntity.getChatId());

        this.groupEntity = groupRepository.saveAndFlush(GroupEntity.builder()
            .name("Test Group")
            .vectorStoreId("test_vector_store_id")
            .build());
        assertNotNull(this.groupEntity.getId());

        this.groupUserEntity = groupUserRepository.saveAndFlush(GroupUserEntity.builder()
            .user(userEntity)
            .group(groupEntity)
            .threadId("test_thread_id")
            .userRole(UserRole.ADMIN)
            .build());
        assertNotNull(this.groupUserEntity.getUserChatId());
    }

    @Test
    void findByUserChatId() {
        final Optional<GroupUserEntity> optionalEntity = groupUserRepository.findByUserChatId(USER_CHAT_ID);
        assertTrue(optionalEntity.isPresent());

        final GroupUserEntity actualEntity = optionalEntity.get();
        assertEquals(USER_CHAT_ID, actualEntity.getUserChatId());
        assertEquals(userEntity.getChatId(), actualEntity.getUser()
            .getChatId());

        assertEquals(groupEntity.getId(), actualEntity.getGroup()
            .getId());
        assertEquals(groupEntity.getName(), actualEntity.getGroup()
            .getName());
        assertEquals(groupEntity.getVectorStoreId(), actualEntity.getGroup()
            .getVectorStoreId());
    }

    @Test
    void findByGroupId() {
        final Collection<GroupUserEntity> groupUsers = groupUserRepository.findByGroupId(groupEntity.getId());

        assertEquals(1, groupUsers.size());
        assertEquals(userEntity.getChatId(), groupUsers.stream()
            .findFirst()
            .get()
            .getUser()
            .getChatId());
    }
}