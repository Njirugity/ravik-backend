package net.ravik_cms.ravik_backend.users;

import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    CreateClientDto toCreateClientDto(Users client);
    Users fromCreateClient(CreateClientDto client);
    CreateSupervisorsDto toCreateSupervisorsDto(Users supervisor);
    Users fromCreateSupervisor(CreateSupervisorsDto supervisor);
    CreateLabourerDto toCreateLabourersDto(Users labourer);
    Users fromCreateLabourer(CreateLabourerDto labourer);
    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.userName", target = "userName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.idNumber", target = "idNumber")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    @Mapping(source = "role.name", target = "role")
    SupervisorWithRoleDto toSupervisorWithRole(ProjectMembership membership);
    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.userName", target = "userName")
    @Mapping(source = "user.idNumber", target = "idNumber")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    @Mapping(source = "role.name", target = "role")
    LabourerWithRoleDto toLabourerWithRole(ProjectMembership membership);
    SupervisorDto toSupervisor (Users supervisor);
    LabourerDto toLabourer(Users labourer);
    StaffDto toStaff(Users user);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(StaffDto dto, @MappingTarget Users user);
    ClientDto toClient(Users user);
}
