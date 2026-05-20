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
public class LabourerDto {
    private UUID id;
    private String userName;
    private String phoneNumber;
    private String idNumber;
}
