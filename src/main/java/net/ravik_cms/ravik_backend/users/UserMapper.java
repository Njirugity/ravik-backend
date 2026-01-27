package net.ravik_cms.ravik_backend.users;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    CreateClientDto toCreateClientDto(Users client);
    List<CreateClientDto> toCreateCLientList(List<Users> clients);
    Users fromCreateClient(CreateClientDto client);
    CreateSupervisorsDto toCreateSupervisorsDto(Users supervisor);
    List<CreateSupervisorsDto> toCreateSupervisorsList(List<Users> supervisors);
    Users fromCreateSupervisor(CreateSupervisorsDto supervisor);
    CreateLabourerDto toCreateLabourersDto(Users labourer);
    List<CreateLabourerDto> toCreateLabourersDto(List<Users> labourers);
    Users fromCreateLabourer(CreateLabourerDto labourer);
    SupervisorDto toSupervisor (Users supervisor);
    List<SupervisorDto> toSupervisorList (List<Users> supervisors);
    Users fromSupervisor(SupervisorDto supervisors);
    LabourerDto toLabourer(Users labourer);
    List<LabourerDto> toLabourerList(List<Users> labourers);
    Users fromLabourer(LabourerDto labourer);
    StaffDto toStaff(Users user);
    List<StaffDto> toStaffList(List<Users> users);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(StaffDto dto, @MappingTarget Users user);

}
