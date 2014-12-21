package com.cboe.domain.util;

import com.cboe.interfaces.domain.routingProperty.common.OrderLocation;

public enum UserRoleEnum
{
    FIRM('F'),
    BROKER_DEALER('B'),
    CUSTOMER_BROKER_DEALER('X'),
    MARKET_MAKER('M'),
    HELP_DESK('H'),
    DPM_ROLE('D'),
    UNKNOWN_ROLE('K'),
    CLASS_DISPLAY('C'),
    FIRM_DISPLAY('R'),
    EXCHANGE_BROKER('E'),
    EXPECTED_OPENING_PRICE_ROLE('Q'),
    PRODUCT_MAINTENANCE('P'),
    TFL_ROLE('T'),
    HELP_DESK_OMT('A'),
    BOOTH_OMT('G'),
    DISPLAY_OMT('J'),
    CROWD_OMT('L'),
    REPORTING('N'),
    OPRA('S');
    
    private char userRole;
    
    UserRoleEnum(char userRole)
    {
        this.userRole = userRole;
    }
    
    public char getUserRole()
    {
        return userRole;
    }
    
    public static UserRoleEnum getUserRoleEnum(char userRole)
    {
        UserRoleEnum retEnum = null;
        for(UserRoleEnum theEnum : UserRoleEnum.values())
        {
            if(theEnum.getUserRole() == userRole)
            {
                retEnum = theEnum;
                break;
            }
        }
        return retEnum;
    }
    
    public static OrderLocation getLocationByUserRole(UserRoleEnum userRole)
    {
        switch (userRole)
        {
            case HELP_DESK: return OrderLocation.HELP_DESK;
            case HELP_DESK_OMT: return OrderLocation.HELP_DESK;
            case BOOTH_OMT: return OrderLocation.BOOTH;
            case CROWD_OMT: return OrderLocation.CROWD;
            case DISPLAY_OMT: return OrderLocation.DISPLAY;
            default: return OrderLocation.UNSPECIFIED;
        }        
    }
}
