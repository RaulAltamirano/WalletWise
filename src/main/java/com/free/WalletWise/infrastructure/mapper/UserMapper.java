package com.free.WalletWise.infrastructure.mapper;

import com.free.WalletWise.domain.model.User;
import com.free.WalletWise.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                new HashSet<>(entity.getRoles())
        );
    }

    public UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity(domain.getUsername(), domain.getPasswordHash());
        entity.setRoles(new HashSet<>(domain.getRoles()));
        return entity;
    }
}