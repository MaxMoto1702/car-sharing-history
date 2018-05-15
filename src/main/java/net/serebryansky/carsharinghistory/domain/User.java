package net.serebryansky.carsharinghistory.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String passwordConfirm;
    private Set<Role> roles;
    private Integer vkUserId;
    private String vkSecret;

    public User() {
    }

    public User(String username, String ignore1, String ignore2) {
        this.username = username;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Transient
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setVkUserId(Integer vkUserId) {
        this.vkUserId = vkUserId;
    }

    public void setVkSecret(String vkSecret) {
        this.vkSecret = vkSecret;
    }

    public Integer getVkUserId() {
        return vkUserId;
    }

    public String getVkSecret() {
        return vkSecret;
    }
}
