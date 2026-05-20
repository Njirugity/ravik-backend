package net.ravik_cms.ravik_backend.users;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLabourerDto {
    @NotBlank(message = "User name required")
    private String userName;
    @NotBlank(message = "Phone number required")
    private String phoneNumber;
    @NotBlank(message = "Identification required")
    private String idNumber;
    private String roleKey;
    private Double baseDailyWage;
}
