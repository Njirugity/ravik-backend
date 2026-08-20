package net.ravik_cms.ravik_backend.users;

import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper { CreateClientDto toCreateClientDto(Users client);
    Users fromCreateClient(CreateClientDto client);
    ClientDto toClient(Users user);
    Users fromCreateUsers(CreateUserDto dto);
}
