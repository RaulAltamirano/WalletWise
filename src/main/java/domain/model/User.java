package domain.model;

import java.util.Set;

public class User {
    private final String id;
    private final String username;
    private final String passwordHash;
    private final Set<String> roles;

    public User(String id, String username, String passwordHash, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = roles;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Set<String> getRoles() { return roles; }

}
