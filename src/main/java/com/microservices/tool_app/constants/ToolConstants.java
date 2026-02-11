package com.microservices.tool_app.constants;

public final class ToolConstants {

    private ToolConstants() {}

    public static final String DEFAULT_TOOL_TYPE = "General";
    public static final String DEFAULT_TOOL_NAME = "Unnamed Tool";


    public static final String MESSAGE_201 = "Tool created successfully";
    public static final String MESSAGE_200_UPDATE = "Tool updated successfully";
    public static final String MESSAGE_200_DELETE = "Tool deleted successfully";


    public static final String MESSAGE_417_UPDATE =
            "Tool update failed. Please try again or contact the Dev team";

    public static final String MESSAGE_417_DELETE =
            "Tool deletion failed. Please try again or contact the Dev team";

    public static final String MESSAGE_404 = "Tool does not exist.";
    public static final String MESSAGE_400 = "Invalid tool Id.";
}
