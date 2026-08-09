package net.ravik_cms.ravik_backend.attendance;

import java.util.UUID;

public record AttendanceSummaryProjection(Long membershipId,
                                          String userName,
                                          String jobTitle,
                                          Long daysPresent) {
}
