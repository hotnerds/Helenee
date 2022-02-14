package com.hotnerds.user.domain.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserDto {
    private String username;
    private String email;
}
