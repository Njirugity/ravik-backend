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
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/approvals")
public class ApprovalController {
    private final ApprovalService approvalService;
    private final AuthorizationService authorizationService;

    @PostMapping("/project/{project_id}")
    public ResponseEntity<ApprovalInfoDto> createApproval(@PathVariable UUID project_id,
                                                            @Valid @RequestBody CreateApprovalDto request){
        authorizationService.authorize("CREATE_APPROVAL");
        ApprovalInfoDto body = approvalService.createApproval(request, project_id);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/project/{project_id}")
    public ResponseEntity<Page<ApprovalInfoDto>> getApprovals(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable){
        authorizationService.authorize("READ_APPROVAL");
        Page<ApprovalInfoDto> body = approvalService.getApprovals(project_id, search, pageable);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApprovalInfoDto> getApproval(@PathVariable Long id){
        authorizationService.authorize("READ_APPROVAL");
        return ResponseEntity.ok(approvalService.getApproval(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApprovalInfoDto> updateApproval(@PathVariable Long id,
                                                           @RequestBody UpdateApprovalDto request){
        authorizationService.authorize("UPDATE_APPROVAL");
        return ResponseEntity.ok(approvalService.updateApproval(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApproval(@PathVariable Long id){
        authorizationService.authorize("DELETE_APPROVAL");
        approvalService.deleteApproval(id);
        return ResponseEntity.noContent().build();
    }
}
