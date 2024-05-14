package com.asid.groupmateai.storage.repositories;

import com.asid.groupmateai.storage.entities.GroupUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUserEntity, Long> {

    Optional<GroupUserEntity> findByUserChatId(final Long userChatId);

    Collection<GroupUserEntity> findByGroupId(final Long groupId);
}
