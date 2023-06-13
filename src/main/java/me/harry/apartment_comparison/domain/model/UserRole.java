package me.harry.apartment_comparison.domain.model;

public enum UserRole {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return value;
    }
}
