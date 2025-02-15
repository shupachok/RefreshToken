package com.sp.refreshtoken.security.service;

import com.sp.refreshtoken.entity.app.Authority;
import com.sp.refreshtoken.entity.app.Role;
import com.sp.refreshtoken.entity.app.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

	private String id;

    private String username;

    private String password;

    public Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(
			String id,
            String username,
            String password,
            List<SimpleGrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
		Set<Role> roles = user.getRoles();

		List<SimpleGrantedAuthority> authorities = new ArrayList<>();

		for(Role role:roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName().name()));

			Set<Authority> authoritiesEntities = role.getAuthorities();

			for(Authority authorityEntity: authoritiesEntities) {
				authorities.add(new SimpleGrantedAuthority(authorityEntity.getName().name()));
			}
		}

        return new UserDetailsImpl(
				user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}