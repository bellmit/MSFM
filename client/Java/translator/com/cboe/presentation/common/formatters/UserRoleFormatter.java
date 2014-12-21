//
// -----------------------------------------------------------------------------------
// Source file: UserRoleFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.cmiConstants.UserRoles;

import com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy;
import com.cboe.interfaces.presentation.user.Role;

public class UserRoleFormatter extends Formatter implements UserRoleFormatStrategy
{
    /**
     * Constructor, defines styles and sets initial default style
     */
    public UserRoleFormatter()
    {
        addStyle(FULL_ROLE_NAME, FULL_ROLE_NAME_DESCRIPTION);
        addStyle(BRIEF_ROLE_NAME, BRIEF_ROLE_NAME_DESCRIPTION);

        setDefaultStyle(FULL_ROLE_NAME);
    }

    /**
     * Defines a method for formatting cmiConstant UserRole
     * @param role char to format
     * @return formatted string
     */
    public String format(char role)
    {
        return format(role, getDefaultStyle());
    }

    /**
     * Defines a method for formatting cmiConstant UserRole
     * @param role char to format
     * @param style to use
     * @return formatted string
     */
    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    public String format(char role, String style)
    {
        String text = null;
        if(!containsStyle(style))
        {
            throw new IllegalArgumentException("UserRoleFormatter - Unknown Style");
        }

        if(style.equals(FULL_ROLE_NAME))
        {
            switch (role)
            {
                case UserRoles.BROKER_DEALER:
                    text = BROKER_DEALER_STRING;
                    break;
                case UserRoles.CUSTOMER_BROKER_DEALER:
                    text = CUSTOMER_BROKER_DEALER_STRING;
                    break;
                case UserRoles.FIRM:
                    text = FIRM_STRING;
                    break;
                case UserRoles.HELP_DESK:
                    text = HELP_DESK_STRING;
                    break;
                case UserRoles.MARKET_MAKER:
                    text = MARKET_MAKER_STRING;
                    break;
                case UserRoles.DPM_ROLE:
                    text = DPM_ROLE_STRING;
                    break;
                case UserRoles.CLASS_DISPLAY:
                    text = CLASS_DISPLAY_STRING;
                    break;
                case UserRoles.FIRM_DISPLAY:
                    text = FIRM_DISPLAY_STRING;
                    break;
                case UserRoles.EXCHANGE_BROKER:
                    text = EXCHANGE_BROKER_STRING;
                    break;
                case UserRoles.PRODUCT_MAINTENANCE:
                    text = PRODUCT_MAINTENANCE_STRING;
                    break;
                case UserRoles.EXPECTED_OPENING_PRICE_ROLE:
                    text = EXPECTED_OPENING_PRICE_ROLE_STRING;
                    break;
                case UserRoles.TFL_ROLE:
                    text = TFL_ROLE_STRING;
                    break;
                case UserRoles.HELP_DESK_OMT:
                    text = HELPDESK_OMT_STRING;
                    break;
                case UserRoles.BOOTH_OMT:
                    text = BOOTH_OMT_STRING;
                    break;
                case UserRoles.DISPLAY_OMT:
                    text = DISPLAY_OMT_STRING;
                    break;
                case UserRoles.CROWD_OMT:
                    text = CROWD_OMT_STRING;
                    break;
                case UserRoles.REPORTING:
                    text = REPORTING_STRING;
                    break;
                case UserRoles.OPRA:
                    text = OPRA_STRING;
                    break;
                case UserRoles.UNKNOWN_ROLE:
                    text = UNKNOWN_ROLE_STRING;
                    break;
                default :
                    text = new StringBuffer(6).append("[ ").append(role).append(" ]").toString();
                    break;
            }
        }
        else if(style.equals(BRIEF_ROLE_NAME))
        {
            switch (role)
            {
                case UserRoles.BROKER_DEALER:
                    text = BROKER_DEALER_BRIEF_STRING;
                    break;
                case UserRoles.CUSTOMER_BROKER_DEALER:
                    text = CUSTOMER_BROKER_DEALER_BRIEF_STRING;
                    break;
                case UserRoles.FIRM:
                    text = FIRM_BRIEF_STRING;
                    break;
                case UserRoles.HELP_DESK:
                    text = HELP_DESK_BRIEF_STRING;
                    break;
                case UserRoles.MARKET_MAKER:
                    text = MARKET_MAKER_BRIEF_STRING;
                    break;
                case UserRoles.DPM_ROLE:
                    text = DPM_ROLE_BRIEF_STRING;
                    break;
                case UserRoles.CLASS_DISPLAY:
                    text = CLASS_DISPLAY_BRIEF_STRING;
                    break;
                case UserRoles.FIRM_DISPLAY:
                    text = FIRM_DISPLAY_BRIEF_STRING;
                    break;
                case UserRoles.EXCHANGE_BROKER:
                    text = EXCHANGE_BROKER_BRIEF_STRING;
                    break;
                case UserRoles.PRODUCT_MAINTENANCE:
                    text = PRODUCT_MAINTENANCE_BRIEF_STRING;
                    break;
                case UserRoles.TFL_ROLE:
                    text = TFL_ROLE_BRIEF_STRING;
                    break;
                case UserRoles.EXPECTED_OPENING_PRICE_ROLE:
                    text = EXPECTED_OPENING_PRICE_ROLE_BRIEF_STRING;
                    break;
                case UserRoles.HELP_DESK_OMT:
                    text = HELPDESK_OMT_BRIEF_STRING;
                    break;
                case UserRoles.BOOTH_OMT:
                    text = BOOTH_OMT_BRIEF_STRING;
                    break;
                case UserRoles.DISPLAY_OMT:
                    text = DISPLAY_OMT_BRIEF_STRING;
                    break;
                case UserRoles.CROWD_OMT:
                    text = CROWD_OMT_BRIEF_STRING;
                    break;
                case UserRoles.REPORTING:
                    text = REPORTING_BRIEF_STRING;
                    break;
                case UserRoles.OPRA:
                    text = OPRA_BRIEF_STRING;
                    break;
                case UserRoles.UNKNOWN_ROLE:
                    text = UNKNOWN_ROLE_BRIEF_STRING;
                    break;
                default :
                    text = new StringBuffer(6).append("[ ").append(role).append(" ]").toString();
                    break;
            }
        }
        return text;
    }

    public String format(Role role)
    {
        return format(role.getRoleChar());
    }

    public String format(Role role, String style)
    {
        return format(role.getRoleChar(), style);
    }
}
