package net.ravik_cms.ravik_backend.users;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StaffDto {
    private UUID id;
    private String userName;
    private String email;
    private String idNumber;
    private String phoneNumber;
}
