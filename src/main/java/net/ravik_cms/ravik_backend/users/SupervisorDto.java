package net.ravik_cms.ravik_backend.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.roles.RoleInfoDto;
import net.ravik_cms.ravik_backend.roles.Roles;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupervisorDto {
    private UUID id;
    private String userName;
    private String email;
    private String idNumber;
    private String phoneNumber;
}
