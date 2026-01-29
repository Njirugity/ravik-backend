package net.ravik_cms.ravik_backend.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    Optional <Users> findByEmail(String email);
    Optional<Users> findByUserName(String userName);
    boolean existsByUserName(String userName);
}
