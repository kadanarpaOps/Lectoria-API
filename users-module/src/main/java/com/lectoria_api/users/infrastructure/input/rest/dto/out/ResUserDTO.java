package com.lectoria_api.users.infrastructure.input.rest.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ResUserDTO {

    private String username;
    private String email;
    private List<String> userRoles;

}
