package net.ravik_cms.ravik_backend.authorization;

/**
 * Logical operation applied when a {@link RequirePermission} annotation lists more than
 * one permission.
 */
public enum Operation {
    /** The caller must hold every listed permission. */
    AND,
    /** The caller must hold at least one of the listed permissions. */
    OR
}
