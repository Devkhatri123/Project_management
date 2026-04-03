package com.project_management.project_management.enums.User_Enums;

public enum Authority {
    // Workspace owner actions
    CAN_DO_WRITE_OPERATION,
    CAN_DO_DELETE_OPERATION,

    CAN_WRITE,
    CAN_INVITE,
    CAN_ASSIGN,
    CAN_DELETE,


    CAN_CREATE_WORKSPACE,
    CAN_INVITE_NEW_USER,
    CAN_CREATE_PROJECT,
    CAN_CREATE_TASK,
    CAN_ASSIGN_TASK_TO_MEMBERS,
    CAN_REMOVE_MEMBER,
    CAN_DELETE_PROJECT,
    CAN_DELETE_WORKSPACE,

    // Normal User actions
    CAN_COMPLETE_TASK,
    CAN_VIEW_ASSIGNED_TASK,
    CAN_CHAT_WITH_USERS,

}
