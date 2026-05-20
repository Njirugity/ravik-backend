package net.ravik_cms.ravik_backend.authentication;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String userName;
    private String password;

}
