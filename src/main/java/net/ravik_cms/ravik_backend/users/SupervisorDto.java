package net.ravik_cms.ravik_backend.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupervisorDto {
    private String id;
    private String userName;
    private String email;
    private String phoneNumber;
    private String idNumber;
}
