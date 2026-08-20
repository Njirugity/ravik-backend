package net.ravik_cms.ravik_backend.common.response;

import lombok.Builder;

import java.time.LocalDate;

/**
 * Generic success-message envelope for endpoints that mutate state (create/update/delete)
 * but don't need to hand the caller a full resource DTO back. Mirrors {@link net.ravik_cms.ravik_backend.common.exception.ApiErrors}'s shape.
 */
@Builder
public record ApiMessageResponse(
        String message,
        int status,
        LocalDate timestamp
) {
    public static ApiMessageResponse of(String message, int status) {
        return ApiMessageResponse.builder()
                .message(message)
                .status(status)
                .timestamp(LocalDate.now())
                .build();
    }
}
