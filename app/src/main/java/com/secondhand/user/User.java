package com.secondhand.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secondhand.role.Role;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable=false, updatable=false)
    private long id;

    @Column(name="username", nullable=false, unique=true)
    private String username;

    @Column(name="email", nullable=false, unique = true)
    private String email;

    @Column(name="password", nullable=false)
    private String password;

    @Column(name="enabled", nullable=false)
    private boolean enabled;

    @Column(name="picture", nullable=true, columnDefinition="LONGBLOB")
    @JsonIgnore
    private byte[] picture;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
        joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    @JsonIgnore
    List<Role> roles = new ArrayList<>();

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", email=" + email + ", password=" + password + "]";
    }
}
