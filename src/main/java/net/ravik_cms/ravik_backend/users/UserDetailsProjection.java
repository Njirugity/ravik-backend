package net.ravik_cms.ravik_backend.users;

import net.ravik_cms.ravik_backend.common.enums.StaffStatus;

import java.util.UUID;

public record UserDetailsProjection(Long memberId, UUID userId, String userName, String phoneNumber,
                                    String role, String jobTitle, StaffStatus status) {
}
