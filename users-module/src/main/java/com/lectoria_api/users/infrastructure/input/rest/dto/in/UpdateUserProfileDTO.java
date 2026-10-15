package com.lectoria_api.users.infrastructure.input.rest.dto.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class UpdateUserProfileDTO {

    @NotNull @NotEmpty
    @Pattern(
            regexp = "^[a-z0-9][a-z0-9._-]{3,29}$",
            message = "Username must contains just alphanumeric characters, with a length from 3 to 29"
    )
    private String newUsername;

}
