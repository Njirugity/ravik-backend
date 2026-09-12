package net.ravik_cms.ravik_backend.account.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountDto {
    private String name;
    private String type;
    private Double openingBalance;
}
