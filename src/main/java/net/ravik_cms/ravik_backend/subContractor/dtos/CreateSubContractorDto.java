package net.ravik_cms.ravik_backend.subContractor.dtos;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubContractorDto {
    private String title;
    private String jobType;
    private String email;
    private String address;
    @Pattern(regexp = "^\\+[1-9][0-9\\s\\-]{7,17}$", message = "Use international format with country code," +
            "e.g. +254712345678")
    private String phoneNumber;
}
