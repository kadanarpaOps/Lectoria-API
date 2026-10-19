package com.lectoria_api.users.infrastructure.input.rest.dto.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.lectoria_api.common.domain.constants.Constants.DATE_FORMAT;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ResUserDetailsDTO {

    @JsonFormat(pattern = DATE_FORMAT)
    private LocalDateTime createdAt;
    @JsonFormat(pattern = DATE_FORMAT)
    private LocalDateTime lastUpdatedAt;
    private boolean registeredVerified;
    private String profileImageUrl;

}
