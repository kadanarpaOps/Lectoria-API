package com.lectoria_api.users.infrastructure.input.rest.dto.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class RegisterUserDTO {

    @NotNull @NotEmpty
    @Pattern(
            regexp = "^[a-z0-9][a-z0-9._-]{3,29}$",
            message = "Username must contains just alphanumeric characters, with a length from 3 to 29"
    )
    private String username;

    @NotNull @NotEmpty
    @Pattern(
            regexp = "^[\\w.%+-]+@[A-Za-z\\d.-]{2,}\\.[a-z]{2,6}$",
            message = "Email must be valid"
    )
    private String email;

    @NotNull @NotEmpty
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{7,30}$",
            message = "Password must be from 7 to 30 characters, that contains 1 lowercase and 1 uppercase letter, 1 number and 1 special character"
    )
    private String password;

    @NotNull @NotEmpty
    @NotEmpty(message = "Role List must not be empty")
    private List<String> roleNames;

    private Boolean isEnabled;

}
