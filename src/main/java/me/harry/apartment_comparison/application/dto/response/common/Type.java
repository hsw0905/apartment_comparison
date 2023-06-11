package me.harry.apartment_comparison.application.dto.response.common;

public enum Type {
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE");

    private final String value;

    Type(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
