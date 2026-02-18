package net.ravik_cms.ravik_backend.wages;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.attendance.Attendance;
import net.ravik_cms.ravik_backend.attendance.AttendanceRepository;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WagesService {
    private final AttendanceRepository attendanceRepository;
    private final WagesRepository wagesRepository;
    private final ProjectMembershipRepository membershipRepository;

    @Transactional
    public WageResultDto bulkWages(UUID projectId, LocalDate startDate, LocalDate endDate) {
        List<ProjectMembership> memberships = membershipRepository.findAllByProjectId(projectId);

        int generated = 0;
        double totalWages =0;
        for(ProjectMembership membership : memberships){
            List<Attendance> records = attendanceRepository.findByMembershipAndPresentTrueAndLockedFalseAndDateBetween(membership, startDate, endDate);

            if(records.isEmpty()){
                continue;
            }
            int daysWorked = records.size();
            double totalAmount = membership.getBaseDailyWage()*daysWorked;

            records.forEach(record -> record.setLocked(true));
            Wages newWage = new Wages(totalAmount,startDate,endDate,daysWorked,membership);
            wagesRepository.save(newWage);
            generated ++;
            totalWages += totalAmount;
        }
        return new WageResultDto(generated, totalWages);
    }

    public List<WageInfoDto> displayWages(UUID projectId, LocalDate startDate, LocalDate endDate) {
        List<ProjectMembership> memberships = membershipRepository.findAllByProjectId(projectId);
        List<Wages> wages = wagesRepository.findAllByMembershipInAndStartDateAndEndDate(memberships, startDate, endDate);
        Map<ProjectMembership, List<Wages>> grouped = wages.stream()
                .collect(Collectors.groupingBy(Wages::getMembership));
        List<WageInfoDto> wagesRows = new ArrayList<>();
        for (Wages wage : wages) {
            WageInfoDto record = getWageInfoDto(wage);
            wagesRows.add(record);
        }
        return wagesRows;
    }

    private static @NonNull WageInfoDto getWageInfoDto(Wages wage) {
        WageInfoDto record = new WageInfoDto();
        record.setUserName(wage.getMembership().getUser().getUserName());
        record.setStartDate(wage.getStartDate());
        record.setEndDate(wage.getEndDate());
        record.setBaseWage(wage.getMembership().getBaseDailyWage());
        record.setWorkingDays(wage.getNumberOfDays());
        record.setTotalAmount(wage.getAmount());
        record.setMembershipId(wage.getMembership().getId());
        record.setRole(wage.getMembership().getRole().getName());
        return record;
    }
}
