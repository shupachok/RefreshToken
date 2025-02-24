package com.sp.refreshtoken.config;

import com.sp.refreshtoken.entity.app.Authority;
import com.sp.refreshtoken.entity.app.Role;
import com.sp.refreshtoken.entity.app.User;
import com.sp.refreshtoken.entity.enums.EAuthority;
import com.sp.refreshtoken.entity.enums.ERole;
import com.sp.refreshtoken.repository.AuthorityRepository;
import com.sp.refreshtoken.repository.RoleRepository;
import com.sp.refreshtoken.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;

import java.util.*;

@Log4j2
@Component
public class InitialUsersSetup {

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userDetailsRepository;

    @Autowired
    PasswordEncoder encoder;

    @Transactional
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        Authority readAuthority = createAuthority(EAuthority.ADD);
        Authority editAuthority = createAuthority(EAuthority.EDIT);
        Authority deleteAuthority = createAuthority(EAuthority.DELETE);

        Set<Authority> userAuthority = new HashSet<>();
        userAuthority.add(readAuthority);
        userAuthority.add(editAuthority);

        createRole(ERole.ROLE_USER, userAuthority);

        Set<Authority> adminAuthority = new HashSet<>();
        adminAuthority.add(readAuthority);
        adminAuthority.add(deleteAuthority);
        adminAuthority.add(editAuthority);

        Role roleAdmin = createRole(ERole.ROLE_ADMIN, adminAuthority);

        if (roleAdmin == null) return;

        Optional<User> adminUser = userDetailsRepository.findByUsername("admin");

        if (adminUser.isEmpty()) {
            User user = new User();
            Set<Role> adminRole = new HashSet<>();
            adminRole.add(roleAdmin);

            user.setUsername("admin");
            user.setPassword(encoder.encode("admin1234"));
            user.setEmail("demoadmin@gmail.com");
            user.setRoles(adminRole);

            userDetailsRepository.save(user);
        }


    }

    @Transactional
    protected Authority createAuthority(EAuthority name) {
        Optional<Authority> authority = authorityRepository.findByName(name);

        if (authority.isEmpty()) {
            Authority newAuthority = new Authority();
            newAuthority.setName(name);
            authorityRepository.save(newAuthority);
            return newAuthority;
        }
        return authority.get();
    }

    @Transactional
    protected Role createRole(ERole name, Set<Authority> authorities) {
        Optional<Role> roleEntity = roleRepository.findByName(name);

        if (roleEntity.isEmpty()) {
            Role roleEnt = new Role();
            roleEnt.setName(name);
            roleEnt.setAuthorities(authorities);
            roleRepository.save(roleEnt);
            return roleEnt;
        }

        return roleEntity.get();
    }
}