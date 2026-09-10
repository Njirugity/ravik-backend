package net.ravik_cms.ravik_backend.phase.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhaseOverdueDto {
    private UUID id;
    private String title;
    private String severity;
}
