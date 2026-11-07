package net.ravik_cms.ravik_backend.approvals;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import net.ravik_cms.ravik_backend.authorization.RequirePermission;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/approvals")
public class ApprovalController {
    private final ApprovalService approvalService;

    @RequirePermission("CREATE_APPROVAL")
    @PostMapping("/project/{project_id}")
    public ResponseEntity<ApprovalInfoDto> createApproval(@PathVariable UUID project_id,
                                                            @Valid @RequestBody CreateApprovalDto request){
        ApprovalInfoDto body = approvalService.createApproval(request, project_id);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @RequirePermission("READ_APPROVAL")
    @GetMapping("/project/{project_id}")
    public ResponseEntity<Page<ApprovalInfoDto>> getApprovals(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable){
        Page<ApprovalInfoDto> body = approvalService.getApprovals(project_id, search, pageable);
        return ResponseEntity.ok(body);
    }

    @RequirePermission("READ_APPROVAL")
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalInfoDto> getApproval(@PathVariable Long id){
        return ResponseEntity.ok(approvalService.getApproval(id));
    }

    @RequirePermission("UPDATE_APPROVAL")
    @PatchMapping("/{id}")
    public ResponseEntity<ApprovalInfoDto> updateApproval(@PathVariable Long id,
                                                           @RequestBody UpdateApprovalDto request){
        return ResponseEntity.ok(approvalService.updateApproval(id, request));
    }

    @RequirePermission("DELETE_APPROVAL")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApproval(@PathVariable Long id){
        approvalService.deleteApproval(id);
        return ResponseEntity.noContent().build();
    }
}
