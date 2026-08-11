package net.ravik_cms.ravik_backend.roles;

import java.util.UUID;

public interface RoleMembershipCount {
    UUID getRoleId();
    Long getMemberCount();
}
