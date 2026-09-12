package net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos.CreateSubContractorRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos.SubContractorRequiredInfoProjection;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos.UpdateSubContractorRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.service.SubContractorRequiredService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subContractorRequired")
public class SubContractorRequiredController {
    private final SubContractorRequiredService subContractorRequiredService;

    @PostMapping("/{milestone_id}")
    public ResponseEntity<List<SubContractorRequiredInfoProjection>> createSubContractorRequired(
            @PathVariable UUID milestone_id,
            @RequestBody List<CreateSubContractorRequiredDto> request) {
        List<SubContractorRequiredInfoProjection> body = subContractorRequiredService.createSubContractorRequired(milestone_id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{milestone_id}")
    public ResponseEntity<List<SubContractorRequiredInfoProjection>> getSubContractorRequired(@PathVariable UUID milestone_id) {
        return ResponseEntity.ok(subContractorRequiredService.getSubContractorRequired(milestone_id));
    }

    @GetMapping("/project/{project_id}")
    public ResponseEntity<Page<SubContractorRequiredInfoProjection>> getSubContractorRequiredByProject(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String jobType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(subContractorRequiredService.getSubContractorRequiredByProject(project_id, search, jobType, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateSubContractorRequired(@PathVariable Long id, @RequestBody UpdateSubContractorRequiredDto request) {
        subContractorRequiredService.updateSubContractorRequired(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubContractorRequired(@PathVariable Long id) {
        subContractorRequiredService.deleteSubContractorRequired(id);
        return ResponseEntity.noContent().build();
    }
}
