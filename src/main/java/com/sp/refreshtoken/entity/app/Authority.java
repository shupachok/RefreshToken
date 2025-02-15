package com.sp.refreshtoken.entity.app;

import com.sp.refreshtoken.entity.base.BaseEntity;
import com.sp.refreshtoken.entity.enums.EAuthority;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "authorities")
@Data
@NoArgsConstructor
public class Authority extends BaseEntity {

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private EAuthority name;
	
	@ManyToMany(mappedBy = "authorities")
	Set<Role> roles;
}