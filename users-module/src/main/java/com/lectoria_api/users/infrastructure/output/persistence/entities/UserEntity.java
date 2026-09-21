package com.lectoria_api.users.infrastructure.output.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "T_USERS")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @Builder
public class UserEntity {

    @Id
    @Column(name = "USER_ID")
    private UUID userId;

    @Column(name = "USER_USERNAME")
    private String username;

    @Column(name = "USER_EMAIL")
    private String userEmail;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "T_USER_ROLES",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private List<RoleEntity> userRoles;

    @Column(name = "USER_ENABLED")
    private boolean userEnabled;

}
