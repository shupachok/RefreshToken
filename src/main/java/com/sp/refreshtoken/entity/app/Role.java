package com.sp.refreshtoken.entity.app;

import com.sp.refreshtoken.entity.base.BaseEntity;
import com.sp.refreshtoken.entity.enums.ERole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ERole name;

	@ManyToMany(
			fetch = FetchType.EAGER)
	@JoinTable(name="roles_authorities",joinColumns = @JoinColumn(name="roles_id"),
			inverseJoinColumns = @JoinColumn(name="authorities_id"))
	private Set<Authority> authorities = new HashSet<>();
    
}