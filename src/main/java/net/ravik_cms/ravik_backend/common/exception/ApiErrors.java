package net.ravik_cms.ravik_backend.common.exception;

import java.time.LocalDate;

public record ApiErrors(
        String message,
        int status,
        LocalDate timestamp
) {}
