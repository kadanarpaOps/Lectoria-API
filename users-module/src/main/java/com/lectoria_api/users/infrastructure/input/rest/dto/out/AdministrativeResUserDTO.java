package com.lectoria_api.users.infrastructure.input.rest.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class AdministrativeResUserDTO extends ResUserDTO {

    private String userId;
    private Boolean isEnabled;

}
