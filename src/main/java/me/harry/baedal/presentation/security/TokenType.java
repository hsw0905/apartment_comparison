package me.harry.baedal.presentation.security;

public enum TokenType {
    ACCESS("ACCESS"),
    REFRESH("REFRESH");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
