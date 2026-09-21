package com.lectoria_api.users.infrastructure.input.rest.dto.in;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class UpdateUserProfileDTO {

    @Pattern(
            regexp = "^[A-ZÑ][A-Za-zÀ-ÿ]+(\\s[A-ZÑ][A-Za-zÀ-ÿ]+){0,4}$",
            message = "Username and it parts must start with an Uppercase letter"
    )
    private String newUsername;

}
