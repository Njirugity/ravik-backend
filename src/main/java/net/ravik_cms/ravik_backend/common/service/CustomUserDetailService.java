package net.ravik_cms.ravik_backend.common.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.common.security.UserPrincipal;
import net.ravik_cms.ravik_backend.permission.Permissions;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.users.UserRepository;
import net.ravik_cms.ravik_backend.users.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users loadUser = userRepository.findByUserName(username).
                orElseThrow(()->new UsernameNotFoundException("User not found"));

        return new UserPrincipal(
                loadUser.getId(),
                loadUser.getUserName(),
                loadUser.getPassword()
        );
    }
}
