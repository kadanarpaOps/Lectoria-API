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
public class UpdateUserEmailDTO {

    @NotNull @NotEmpty
    @Pattern(
            regexp = "^[\\w.%+-]+@[A-Za-z\\d.-]{2,}\\.[a-z]{2,6}$",
            message = "Email must be valid"
    )
    private String newEmail;

}
