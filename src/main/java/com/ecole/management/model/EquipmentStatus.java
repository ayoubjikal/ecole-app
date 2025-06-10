package com.ecole.management.model;

/**
 * Enum representing the status of equipment in the system
 */
public enum EquipmentStatus {
    /**
     * Equipment is active and available for use
     */
    ACTIVE("Actif"),

    /**
     * Equipment has been marked as suppressed/deleted but remains in the system for tracking
     */
    SUPPRIME("Supprimé");

    private final String displayName;

    /**
     * Constructor for EquipmentStatus enum
     * @param displayName The human-readable display name in French
     */
    EquipmentStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the display name for this status
     * @return The French display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get status from display name
     * @param displayName The display name to search for
     * @return The corresponding EquipmentStatus or null if not found
     */
    public static EquipmentStatus fromDisplayName(String displayName) {
        for (EquipmentStatus status : values()) {
            if (status.getDisplayName().equals(displayName)) {
                return status;
            }
        }
        return null;
    }

    /**
     * Check if this status represents an active equipment
     * @return true if status is ACTIVE, false otherwise
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * Check if this status represents a suppressed equipment
     * @return true if status is SUPPRIME, false otherwise
     */
    public boolean isSupprime() {
        return this == SUPPRIME;
    }
}