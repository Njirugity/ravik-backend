package net.ravik_cms.ravik_backend.subContractor.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.subContractor.service.SubContractorService;
import net.ravik_cms.ravik_backend.subContractor.dtos.CreateSubContractorDto;
import net.ravik_cms.ravik_backend.subContractor.dtos.SubContractorInfoProjection;
import net.ravik_cms.ravik_backend.subContractor.dtos.UpdateSubContractorDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subContractors")
public class SubContractorController {
    private final SubContractorService subContractorService;

    @PostMapping("/{project_id}")
    public ResponseEntity<?> addSubContractor(@PathVariable UUID project_id,
                                               @RequestBody CreateSubContractorDto request) {
        subContractorService.addSubContractor(request, project_id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{project_id}")
    public ResponseEntity<Page<SubContractorInfoProjection>> getSubContractors(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String jobType,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SubContractorInfoProjection> body = subContractorService.getSubContractors(project_id, search, jobType, pageable);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateSubContractor(@PathVariable Long id, @RequestBody UpdateSubContractorDto request) {
        subContractorService.updateSubContractor(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubContractor(@PathVariable Long id) {
        subContractorService.deleteSubContractor(id);
        return ResponseEntity.noContent().build();
    }
}
