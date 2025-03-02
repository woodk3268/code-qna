package com.codeqna.constant;

import lombok.Getter;

public enum UserRole {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    @Getter
    private final String name;

    UserRole(String name) {
        this.name = name;
    }
}
