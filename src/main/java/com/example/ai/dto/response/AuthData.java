package com.example.ai.dto.response;



import com.example.ai.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthData {
    private String jwt;
    private User user;
}
