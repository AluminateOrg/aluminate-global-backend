package com.aluminate.aluminate_global_backend.config.exception;

public class InactiveOrganizationException extends Throwable {
    public InactiveOrganizationException(String organizationIsNotActive) {
        super(organizationIsNotActive);
    }
}
