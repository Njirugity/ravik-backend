package net.ravik_cms.ravik_backend.wages;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wages")
public class WagesController {
    private final WagesService wagesService;

    @PostMapping("/{project_id}")
    public WageResultDto bulkWages(@PathVariable UUID project_id,
                                   @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return wagesService.bulkWages(project_id, startDate, endDate);
    }
    @GetMapping("/{project_id}")
    public List<WageInfoDto> displayWages(@PathVariable UUID project_id,
                                          @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return wagesService.displayWages(project_id, startDate, endDate);
    }
}
