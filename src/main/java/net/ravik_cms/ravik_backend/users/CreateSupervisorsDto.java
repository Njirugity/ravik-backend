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
public class CreateSupervisorsDto {
    @NotBlank(message = "User name required")
    private String userName;
    @Email(message = "Email is not valid")
    private String email;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotBlank(message = "Identification required")
    private String idNumber;

    private String roleKey;
    private Double baseDailyWage;
}
