package net.ravik_cms.ravik_backend.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.roles.RoleInfoDto;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientDto {
    @NotBlank(message = "User name required")
    private String userName;
    @Email(message = "Email is not valid")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min=8, message = "Password must be at least 8 characters long")
    private String password;
    @NotBlank(message = "Phone number required")
    private String phoneNumber;
    @NotBlank(message = "Identification required")
    private String idNumber;
    private Set<RoleInfoDto> roles;
}
