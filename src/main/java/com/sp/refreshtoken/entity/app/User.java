package com.sp.refreshtoken.entity.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sp.refreshtoken.entity.base.BaseEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(	name = "users")
@Data
@NoArgsConstructor
public class User extends BaseEntity {

    @Nonnull
    @Column(unique = true)
    private String username;

    @Nonnull
    @JsonIgnore
    private String password;

    @Nonnull
    @Column(unique = true,length = 30)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

}
