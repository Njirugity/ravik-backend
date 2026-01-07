package net.ravik_cms.ravik_backend.users;

import org.mapstruct.Mapper;

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

}
