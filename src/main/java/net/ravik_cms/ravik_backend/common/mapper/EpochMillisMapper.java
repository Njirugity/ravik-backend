package net.ravik_cms.ravik_backend.common.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Shared MapStruct helper: converts the {@code LocalDateTime} timestamps stored on
 * {@link net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity} to epoch millis for
 * DTOs, so the frontend gets a plain {@code long} instead of parsing a local-datetime string.
 * Reference via {@code @Mapper(uses = EpochMillisMapper.class)}.
 */
public class EpochMillisMapper {
    public static Long toEpochMillis(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
