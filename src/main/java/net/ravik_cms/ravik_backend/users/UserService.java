package net.ravik_cms.ravik_backend.users;

import net.ravik_cms.ravik_backend.common.exception.UserAlreadyExistsException;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public SupervisorDto addClient(CreateClientDto client){
        Users newClient = userMapper.fromCreateClient(client);
        Users existingUser = userRepository.findByEmail(newClient.getEmail());
        if(existingUser != null){
            throw new UserAlreadyExistsException("User already exists");
        }
        Users savedUser = userRepository.save(newClient);
        return userMapper.toSupervisor(savedUser);
    }
    public SupervisorDto addSupervisor(CreateSupervisorsDto supervisor){
        Users newSupervisor = userMapper.fromCreateSupervisor(supervisor);
        Users existingUser = userRepository.findByEmail(newSupervisor.getEmail());
        if(existingUser != null){
            throw new UserAlreadyExistsException("User already exists");
        }
        Users savedUser = userRepository.save(newSupervisor);
        return userMapper.toSupervisor(savedUser);
    }
    public LabourerDto addLabourer(CreateLabourerDto labourer){
        Users newLabourer = userMapper.fromCreateLabourer(labourer);
        Users existingUser = userRepository.findByEmail(newLabourer.getEmail());
        if(existingUser != null){
            throw new UserAlreadyExistsException("User already exists");
        }
        Users savedUser = userRepository.save(newLabourer);
        return userMapper.toLabourer(savedUser);
    }
    public SupervisorDto getClient(String name){
        Users user = userRepository.findByUserName(name).
                orElseThrow(()->new UserAlreadyExistsException("User not found"));
        return userMapper.toSupervisor(user);
    }
    public SupervisorDto getSupervisor(String name){
        Users user = userRepository.findByUserName(name).
                orElseThrow(()->new UserAlreadyExistsException("User not found"));
        return userMapper.toSupervisor(user);
    }
    public LabourerDto getLabourer(String name){
        Users user = userRepository.findByUserName(name).
                orElseThrow(()->new UserAlreadyExistsException("User not found"));
        return userMapper.toLabourer(user);
    }
}
