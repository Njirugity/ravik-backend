package net.ravik_cms.ravik_backend.approvals;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApprovalMapper {
    Approval toEntity(CreateApprovalDto dto);

    @Mapping(source = "project.id", target = "projectId")
    ApprovalInfoDto toInfoDto(Approval approval);
    List<ApprovalInfoDto> toInfoDtoList(List<Approval> approvals);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateApprovalFromDto(UpdateApprovalDto dto, @MappingTarget Approval approval);
}
