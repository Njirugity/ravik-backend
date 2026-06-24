package net.ravik_cms.ravik_backend.calendar;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CalendarMapper {
    Calendar dtoToEntity(CreateCalendarDto dto);
    CalendarInfoDto entityToDto(Calendar calender);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCalender(CalendarInfoDto request, @MappingTarget Calendar calender);
}
