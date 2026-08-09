package net.ravik_cms.ravik_backend.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByUserName(String userName);

    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.users.UserDetailsProjection(
                            m.id, u.id, u.userName, u.phoneNumber, r.name, j.title, m.status)
                FROM ProjectMembership m
                JOIN m.user u
                LEFT JOIN m.role r
                LEFT JOIN m.jobTitle j
                WHERE m.project.id = :projectId
                AND (:role IS NULL OR r.name = :role)
                AND (:jobTitle IS NULL OR j.title = :jobTitle)
                AND (
                       :search IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%'))
                     )
            """,
            countQuery = """
                SELECT COUNT(m)
                FROM ProjectMembership m
                JOIN m.user u
                LEFT JOIN m.role r
                LEFT JOIN m.jobTitle j
                WHERE m.project.id = :projectId
                AND (:role IS NULL OR r.name = :role)
                AND (:jobTitle IS NULL OR j.title = :jobTitle)
                AND (
                       :search IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%'))
                     )
            """
    )
    Page<UserDetailsProjection> findAllUsers(
            @Param("projectId") UUID projectId,
            @Param("role") String role,
            @Param("jobTitle") String jobTitle,
            @Param("search") String search,
            Pageable pageable
            );
}
