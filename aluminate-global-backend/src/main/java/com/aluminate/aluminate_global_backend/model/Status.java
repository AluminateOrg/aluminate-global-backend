package com.aluminate.aluminate_global_backend.model;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended");

    private final String description;

    Status(String description) {
        this.description = description;
    }

}
