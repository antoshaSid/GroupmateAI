package com.asid.groupmateai.core.services.impl;

import com.asid.groupmateai.core.ai.openai.clients.VectorStoreOpenAiClient;
import com.asid.groupmateai.core.services.GroupService;
import com.asid.groupmateai.storage.entities.GroupEntity;
import com.asid.groupmateai.storage.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final VectorStoreOpenAiClient vectorStoreClient;

    @Autowired
    public GroupServiceImpl(final GroupRepository groupRepository, final VectorStoreOpenAiClient vectorStoreClient) {
        this.groupRepository = groupRepository;
        this.vectorStoreClient = vectorStoreClient;
    }

    @Override
    public GroupEntity addGroup(final String name) {
        String vectorStoreId = null;
        try {
            vectorStoreId = vectorStoreClient.createVectorStore()
                .join()
                .getId();
            final GroupEntity groupEntity = GroupEntity.builder()
                .name(name)
                .vectorStoreId(vectorStoreId)
                .build();

            return groupRepository.save(groupEntity);
        } catch (final Exception e) {
            if (vectorStoreId != null) {
                vectorStoreClient.deleteVectorStore(vectorStoreId);
            }

            throw e;
        }
    }

    @Override
    public GroupEntity getGroup(final Long groupId) {
        return groupRepository.findById(groupId)
            .orElse(null);
    }

    @Override
    public boolean groupExists(final Long groupId) {
        return groupRepository.existsById(groupId);
    }

    @Override
    public void updateGroup(final GroupEntity groupEntity) {
        groupRepository.save(groupEntity);
    }

    @Override
    public void removeGroup(final Long groupId) {
        final GroupEntity groupEntity = getGroup(groupId);

        if (groupEntity != null) {
            vectorStoreClient.deleteVectorStore(groupEntity.getVectorStoreId());
            groupRepository.deleteById(groupId);
        }
    }
}
