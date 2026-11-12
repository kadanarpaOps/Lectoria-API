package com.lectoria_api.users.infrastructure.input.rest.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PublicUserDirectoryEntryDTO {
    private String userId;
    private String username;
    private String profileImageUrl;
}
