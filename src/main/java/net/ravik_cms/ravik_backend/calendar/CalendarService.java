package net.ravik_cms.ravik_backend.calendar;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final CalendarMapper calendarMapper;
    private final ProjectsRepository projectsRepository;

    @Transactional
    public CalendarInfoDto createCalender(UUID projectId, CreateCalendarDto request) {
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        Calendar calendar = calendarMapper.dtoToEntity(request);
        calendar.setProject(project);
        return calendarMapper.entityToDto(calendarRepository.save(calendar));
    }

    public Calendar getCalenderEntity(UUID projectId){
        return calendarRepository.findByProjectId(projectId)
                .orElseThrow(()-> new ResourceNotFoundException("Project calender not found"));
    }

    public CalendarInfoDto getCalender(UUID projectId){
        Calendar calendar = getCalenderEntity(projectId);
        return calendarMapper.entityToDto(calendar);
    }
    @Transactional
    public CalendarInfoDto updateCalender(UUID id, CalendarInfoDto request){
        Calendar calendar = calendarRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Project calender not found"));
        calendarMapper.updateCalender(request, calendar);
        return calendarMapper.entityToDto(calendarRepository.save(calendar));
    }
    @Transactional
    public void deleteCalender(UUID id){
        Calendar calendar = calendarRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Project calender not found"));
        calendarRepository.delete(calendar);
    }
    public boolean isWorkingDay(Calendar calender, LocalDate date){
        if (calender == null) return true;

        if(calender.getHolidays().contains(date))return false;

        DayOfWeek day = date.getDayOfWeek();
        return switch (day){
            case MONDAY -> calender.isMonday();
            case TUESDAY -> calender.isTuesday();
            case WEDNESDAY -> calender.isWednesday();
            case THURSDAY -> calender.isThursday();
            case FRIDAY -> calender.isFriday();
            case SATURDAY -> calender.isSaturday();
            case SUNDAY -> calender.isSunday();
        };
    }
    public LocalDate addWorkingDays(LocalDate start, int duration, Calendar calendar){
        LocalDate current = start;

        while(!isWorkingDay(calendar, current)){
            current = current.plusDays(1);
        }

        if(duration <= 1) return start;

        int workDays = 1;
        while(workDays < duration){
            current = current.plusDays(1);
            if(isWorkingDay(calendar, current)){
                workDays++;
            }

        }
        return current;

    }
    public LocalDate subtractWorkingDays(LocalDate end, int duration, Calendar calendar){
        LocalDate current = end;
        while(!isWorkingDay(calendar, current)){
            current = current.minusDays(1);
        }
        if(duration <= 1) return end;

        int workDays = 1;

        while(workDays < duration){
            current = current.minusDays(1);
            if(isWorkingDay(calendar, current)){
                workDays++;
            }
        }
        return current;
    }
    public long daysBetween(LocalDate start, LocalDate end, Calendar calendar){
        if(start.isAfter(end)){
            return 0L;
        }
        long duration = 0L;
        LocalDate current = start;
        while(!current.isAfter(end)){
            if(isWorkingDay(calendar, current)){
                duration++;
            }
            current = current.plusDays(1);
        }
        return duration;
    }
}
