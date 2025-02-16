package com.sp.refreshtoken.repository;

import com.sp.refreshtoken.entity.app.Authority;
import com.sp.refreshtoken.entity.app.Role;
import com.sp.refreshtoken.entity.enums.EAuthority;
import com.sp.refreshtoken.entity.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
    Optional<Authority> findByName(EAuthority name);
}
