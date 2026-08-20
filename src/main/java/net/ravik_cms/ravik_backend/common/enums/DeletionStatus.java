package net.ravik_cms.ravik_backend.common.enums;

/**
 * Soft-delete marker for entities that can't be hard-deleted safely
 */
public enum DeletionStatus {
    NOT_DELETED,
    DELETED
}
