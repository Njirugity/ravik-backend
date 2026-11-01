package net.ravik_cms.ravik_backend.jobTitles;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/jobTitles")
public class JobTitleController {
    private final JobTitlesService jobTitlesService;
    private final AuthorizationService authorizationService;

    @PostMapping("/{project_id}")
    public ResponseEntity<?> addJobTitle(@PathVariable UUID project_id,
                                            @RequestBody CreateJobTitleDto request){
        authorizationService.authorize("CREATE_LABOUR");
        jobTitlesService.addJobTitle(request, project_id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{project_id}")
    public ResponseEntity<Page<JobTitlesInfoProjection>> getJobTitles(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable
            ){
        authorizationService.authorize("READ_LABOUR");
        Page<JobTitlesInfoProjection> body = jobTitlesService.getJobTitles(project_id,search,pageable);
        return ResponseEntity.ok(body);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateEntity(@PathVariable Long id, @RequestBody UpdateJobTitleDto request){
        authorizationService.authorize("UPDATE_LABOUR");
        jobTitlesService.updateJobTitle(id, request);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntity(@PathVariable Long id){
        authorizationService.authorize("DELETE_LABOUR");
        jobTitlesService.deleteJobTitle(id);
        return ResponseEntity.noContent().build();
    }
}
