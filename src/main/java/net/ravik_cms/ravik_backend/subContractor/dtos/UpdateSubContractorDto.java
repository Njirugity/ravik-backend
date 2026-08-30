package net.ravik_cms.ravik_backend.subContractor.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSubContractorDto {
    private String title;
    private String jobType;
    private String email;
    private String address;
    private String phoneNumber;
}
