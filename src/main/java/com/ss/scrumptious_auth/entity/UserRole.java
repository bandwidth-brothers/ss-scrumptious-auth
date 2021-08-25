package com.ss.scrumptious_auth.entity;

public enum UserRole {

    CUSTOMER("ROLE_CUSTOMER"), DRIVER("ROLE_DRIVER"), ADMIN("ROLE_ADMIN");

    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRole() {
        return roleName;
    }

    public String getRoleName() {
        return roleName.replace("ROLE_", "");
    }
}
