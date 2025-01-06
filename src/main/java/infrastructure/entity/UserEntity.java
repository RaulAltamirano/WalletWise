package infrastructure.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    public UserEntity() {}

    public UserEntity(String username, String password, Set<String> roles) {
        this.username = username;
        this.password = password;
//        this.roles = roles;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    // Métodos requeridos por UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> (GrantedAuthority) () -> role).toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Aquí podrías implementar lógica si soportas expiración de cuentas
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Aquí podrías implementar lógica si soportas bloqueo de cuentas
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Aquí podrías implementar lógica si soportas expiración de credenciales
    }

    @Override
    public boolean isEnabled() {
        return true; // Podrías añadir un campo `enabled` en la entidad para gestionarlo
    }
}

