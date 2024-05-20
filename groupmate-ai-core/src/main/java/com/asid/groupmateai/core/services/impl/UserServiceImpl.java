package com.asid.groupmateai.core.services.impl;

import com.asid.groupmateai.core.services.UserService;
import com.asid.groupmateai.storage.entities.UserEntity;
import com.asid.groupmateai.storage.entities.UserState;
import com.asid.groupmateai.storage.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity addUser(final Long chatId) {
        return userRepository.save(new UserEntity(chatId, UserState.IDLE));
    }

    @Override
    public UserEntity getUser(final Long chatId) {
        return userRepository.findById(chatId)
            .orElse(null);
    }

    @Override
    public boolean userExists(final Long chatId) {
        return userRepository.existsById(chatId);
    }

    @Override
    public UserState getUserState(Long chatId) {
        if (userExists(chatId)) {
            return getUser(chatId).getUserState();
        }

        return null;
    }

    @Override
    public void updateUserState(final Long chatId, final UserState userState) {
        this.updateUserState(chatId, userState, null);
    }

    @Override
    public void updateUserState(final Long chatId, final UserState userState, final Map<String, String> metadata) {
        final UserEntity user = getUser(chatId);

        if (Objects.nonNull(user)) {
            user.setUserState(userState);
            user.setMetadata(metadata);
            userRepository.save(user);
        }
    }
}
