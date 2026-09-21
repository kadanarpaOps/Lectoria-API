package com.lectoria_api.users.infrastructure.output.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "T_ROLES")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @Builder
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ROLE_ID")
    private UUID roleId;

    @Column(name = "ROLE_NAME")
    private String roleName;

}
