//
// -----------------------------------------------------------------------------------
// Source file: UserTypeFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.constants.UserTypes;

public class UserTypeFormatter
{
    public static final short DPM_ACCOUNT_TYPE = UserTypes.DPM_ACCOUNT;
    public static final short FIRM_LOGIN_TYPE = UserTypes.FIRM_LOGIN;
    public static final short HELP_DESK_TYPE = UserTypes.HELP_DESK;
    public static final short INDIVIDUAL_ACCOUNT_TYPE = UserTypes.INDIVIDUAL_ACCOUNT;
    public static final short JOINT_ACCOUNT_TYPE = UserTypes.JOINT_ACCOUNT;
    public static final short SYSTEMS_OPERATIONS_TYPE = UserTypes.SYSTEMS_OPERATIONS;

    public static final String DPM_ACCOUNT_STRING = "DPM Account";
    public static final String FIRM_LOGIN_STRING = "Firm Account";
    public static final String HELP_DESK_STRING = "Help Desk Operator";
    public static final String INDIVIDUAL_ACCOUNT_STRING = "Individual Account";
    public static final String JOINT_ACCOUNT_STRING = "Joint Account";
    public static final String SYSTEMS_OPERATIONS_STRING = "System Operator";

    public static final String INVALID_TYPE_STRING = "Invalid Type:";

    public static final short[] ALL_TYPES = {DPM_ACCOUNT_TYPE, FIRM_LOGIN_TYPE, HELP_DESK_TYPE, INDIVIDUAL_ACCOUNT_TYPE,
                                             JOINT_ACCOUNT_TYPE, SYSTEMS_OPERATIONS_TYPE};

    public static String format(short type)
    {
        switch(type)
        {
            case DPM_ACCOUNT_TYPE:
                return DPM_ACCOUNT_STRING;
            case FIRM_LOGIN_TYPE:
                return FIRM_LOGIN_STRING;
            case HELP_DESK_TYPE:
                return HELP_DESK_STRING;
            case INDIVIDUAL_ACCOUNT_TYPE:
                return INDIVIDUAL_ACCOUNT_STRING;
            case JOINT_ACCOUNT_TYPE:
                return JOINT_ACCOUNT_STRING;
            case SYSTEMS_OPERATIONS_TYPE:
                return SYSTEMS_OPERATIONS_STRING;
            default:
                return INVALID_TYPE_STRING + Short.toString(type);
        }
    }
}
