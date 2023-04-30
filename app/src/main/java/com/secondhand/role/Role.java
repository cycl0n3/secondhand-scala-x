package com.secondhand.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secondhand.user.User;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable=false, updatable=false)
    private long id;

    @Column(name="name", nullable=false, unique=true)
    private String name;

    @ManyToMany(mappedBy = "roles",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        fetch = FetchType.LAZY)
    @JsonIgnore
    List<User> users = new ArrayList<>();

    @Override
    public String toString() {
        return "Role [id=" + id + ", name=" + name + "]";
    }
}
