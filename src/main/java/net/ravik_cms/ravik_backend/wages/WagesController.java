package net.ravik_cms.ravik_backend.wages;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.PaymentFrequency;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wages")
public class WagesController {
    private final WagesService wagesService;
    private final ProjectMembershipRepository membershipRepository;


    @GetMapping("/daily/{project_id}")
    ResponseEntity<Page<WagePreviewDto>> previewDailyWages(
            @PathVariable UUID project_id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate end,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20)Pageable pageable){
        Page<WagePreviewDto> body = wagesService.previewDailyWages(project_id, start, end, role, search, pageable);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/monthly/{project_id}")
    ResponseEntity<Page<WagePreviewDto>> previewMonthlyWages(
            @PathVariable UUID project_id,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20)Pageable pageable){
        Page<WagePreviewDto> body = wagesService.previewMonthlyWages(project_id,year,month, role, search, pageable);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/{project_id}")
    ResponseEntity<Page<WageHistoryProjection>> getWageHistory(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
    ){
        Page<WageHistoryProjection> body = wagesService.getWageHistory(project_id, role, search, pageable);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/daily")
    ResponseEntity<?> confirmDailyWages(
            @RequestBody List<CreateWageRecordDto> request
    ){
        wagesService.confirmDailyWage(request);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/monthly")
    ResponseEntity<?> confirmMonthlyWages(
            @RequestBody List<CreateWageRecordDto> request
    ){
        wagesService.confirmMonthlyWage(request);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{wage_id}")
    ResponseEntity<?> deleteWageRecord(@PathVariable Long wage_id){
        wagesService.deleteWage(wage_id);
        return ResponseEntity.noContent().build();
    }

}
