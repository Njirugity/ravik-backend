package net.ravik_cms.ravik_backend.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupervisorWithRoleDto {
    private UUID id;
    private String userName;
    private String email;
    private String idNumber;
    private String phoneNumber;
    private String role;
}
