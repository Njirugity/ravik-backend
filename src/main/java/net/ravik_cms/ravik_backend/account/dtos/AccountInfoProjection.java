package net.ravik_cms.ravik_backend.account.dtos;

import java.util.UUID;

public record AccountInfoProjection(
        UUID id,
        String name,
        String type,
        Double openingBalance) {
}
