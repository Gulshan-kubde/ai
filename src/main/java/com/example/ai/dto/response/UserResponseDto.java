package com.example.ai.dto.response;

import com.example.ai.model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private String mobileNo;
    private User.Role role;
    private boolean verified;
    private String token;
}
