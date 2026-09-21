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
public class UpdateUserPasswordDTO {

    @NotNull @NotEmpty
    @Pattern(
            regexp = "^(?=.*\\d+)(?=.*[a-z]+)(?=.*[A-Z]+)(?=.*[^a-zA-Z\\d]+)[\\w\\W]{7,30}$",
            message = "Password must be from 7 to 30 characters, that contains 1 lowercase and 1 uppercase letter, 1 number and 1 special character"
    )
    private String newPassword;

}
