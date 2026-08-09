package net.ravik_cms.ravik_backend.jobTitles;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface JobTitleMapper {
    JobTitles toEntity(CreateJobTitleDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateJobTitle(UpdateJobTitleDto dto, @MappingTarget JobTitles entity);
}
