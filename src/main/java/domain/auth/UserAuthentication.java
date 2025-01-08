package domain.auth;

public class UserAuthentication {

    private String username;
    private String token;

    public UserAuthentication(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "UserAuthentication{" +
                "username='" + username + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}

