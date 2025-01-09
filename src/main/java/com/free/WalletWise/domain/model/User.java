package com.free.WalletWise.domain.model;

import java.util.HashSet;
import java.util.Set;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String passwordHash;

    @Builder.Default
    private Set<String> roles = new HashSet<>();

    // Constructor protegido para el builder
//    @Builder
//    protected User(Long id, String username, String passwordHash, Set<String> roles) {
//        this.id = id;
//        this.username = username;
//        this.passwordHash = passwordHash;
//        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
//    }

    public static User createNew(String username, String password) {
        User user = new User();
        user.username = username;
        user.passwordHash = password;
        user.roles = new HashSet<>(Set.of("ROLE_USER"));
        return user;
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public Set<String> getRoles() {
        return Set.copyOf(roles);
    }

    public String getPasswordHash() {
    return  passwordHash;
    }
}