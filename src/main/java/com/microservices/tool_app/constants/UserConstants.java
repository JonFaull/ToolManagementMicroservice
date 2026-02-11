package com.microservices.tool_app.constants;

public final class UserConstants {

    private UserConstants() {
        // Prevent instantiation
    }

    public static final String MESSAGE_201 = "User created successfully";

    public static final String MESSAGE_404 = "User does not exist.";
    public static final String MESSAGE_400 = "Invalid user Id.";

    public static final String MESSAGE_417_UPDATE =
            "User update failed. Please try again or contact the Dev team";

    public static final String MESSAGE_417_DELETE =
            "User deletion failed. Please try again or contact the Dev team";
}
