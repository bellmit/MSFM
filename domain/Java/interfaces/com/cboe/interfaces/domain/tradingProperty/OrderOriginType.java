//
// -----------------------------------------------------------------------------------
// Source file: OrderOriginType.java
//
// PACKAGE: com.cboe.interfaces.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.tradingProperty;

public enum OrderOriginType
{
    PRINCIPAL_ACTING_AS_AGENT(1, com.cboe.idl.cmiConstants.OrderOriginsOperations.PRINCIPAL_ACTING_AS_AGENT),
    BROKER_DEALER(2, com.cboe.idl.cmiConstants.OrderOriginsOperations.BROKER_DEALER),
    CUSTOMER(3, com.cboe.idl.cmiConstants.OrderOriginsOperations.CUSTOMER),
    CUSTOMER_FBW(4, com.cboe.idl.cmiConstants.OrderOriginsOperations.CUSTOMER_FBW),
    CTI1Origin2(5, com.cboe.idl.cmiConstants.OrderOriginsOperations.CTI1Origin2),
    FIRM(6, com.cboe.idl.cmiConstants.OrderOriginsOperations. FIRM),
    CTI3Origin1(7, com.cboe.idl.cmiConstants.OrderOriginsOperations.CTI3Origin1),
    CTI3Origin2(8, com.cboe.idl.cmiConstants.OrderOriginsOperations.CTI3Origin2),
    MARKET_MAKER_IN_CROWD(9, com.cboe.idl.cmiConstants.OrderOriginsOperations.MARKET_MAKER_IN_CROWD),
    FIRM_FBW_ICM(10, com.cboe.idl.cmiConstants.OrderOriginsOperations.FIRM_FBW_ICM),
    BROKER_DEALER_FBW_ICM(11, com.cboe.idl.cmiConstants.OrderOriginsOperations.BROKER_DEALER_FBW_ICM),
    FIRM_FBW_NON_CUSTOMER(12, com.cboe.idl.cmiConstants.OrderOriginsOperations.FIRM_FBW_NON_CUSTOMER),
    MARKET_MAKER(13, com.cboe.idl.cmiConstants.OrderOriginsOperations.MARKET_MAKER),
    MARKET_MAKER_AWAY(14, com.cboe.idl.cmiConstants.OrderOriginsOperations.MARKET_MAKER_AWAY),
    CTI4Origin2(15, com.cboe.idl.cmiConstants.OrderOriginsOperations.CTI4Origin2),
    PRINCIPAL(16, com.cboe.idl.cmiConstants.OrderOriginsOperations.PRINCIPAL),
    CTI1Origin5(17, com.cboe.idl.cmiConstants.OrderOriginsOperations.CTI1Origin5),
    CTI3Origin5(18, com.cboe.idl.cmiConstants.OrderOriginsOperations.CTI3Origin5),
    SATISFACTION(19, com.cboe.idl.cmiConstants.OrderOriginsOperations.SATISFACTION),
    CTI4Origin5(20, com.cboe.idl.cmiConstants.OrderOriginsOperations.CTI4Origin5),
    M_N_Y_FBW(21, com.cboe.idl.cmiConstants.OrderOriginsOperations.M_N_Y_FBW),
    CTI1Origin1(22, com.cboe.idl.cmiConstants.OrderOriginsOperations.CTI1Origin1),
    BROKER_DEALER_FBW_NON_CUSTOMER(23, com.cboe.idl.cmiConstants.OrderOriginsOperations.BROKER_DEALER_FBW_NON_CUSTOMER),
    CUSTOMER_BROKER_DEALER(24, com.cboe.idl.cmiConstants.OrderOriginsOperations.CUSTOMER_BROKER_DEALER),
    UNDERLY_SPECIALIST(25, com.cboe.idl.cmiConstants.OrderOriginsOperations.UNDERLY_SPECIALIST),
    N_Y_FBW(26, com.cboe.idl.cmiConstants.OrderOriginsOperations.N_Y_FBW),
    ITS_POR(27, com.cboe.idl.cmiConstants.OrderOriginsOperations.ITS_POR);

    public int orderOriginId;
    public char orderOriginCode;

    OrderOriginType(int orderOriginId, char orderOriginCode)
    {
        this.orderOriginId = orderOriginId;
        this.orderOriginCode = orderOriginCode;
    }

    public int getOrderOriginId()
    {
        return orderOriginId;
    }

    public char getOrderOriginCode()
    {
        return orderOriginCode;
    }

    // convenience method to provide lookup from the IDL constant to the enum
    public static OrderOriginType findOrderOriginTypeEnum(int originTypeId)
    {
        OrderOriginType retVal = null;
        for(OrderOriginType tmpOrderOriginTypeId : OrderOriginType.values())
        {
            if(originTypeId == tmpOrderOriginTypeId.getOrderOriginId())
            {
                retVal = tmpOrderOriginTypeId;
                break;
            }
        }
        return retVal;
    }

    /**
     * 
     * @param orderOriginType
     * @return
     */
    public static OrderOriginType findOrderOriginTypeEnum(char orderOriginType)
    {
        OrderOriginType retVal = null;
        for(OrderOriginType tmpOrderOriginType : OrderOriginType.values())
        {
            if(tmpOrderOriginType.getOrderOriginCode() == orderOriginType)
            {
                retVal = tmpOrderOriginType;
                break;
            }
        }
        return retVal;
    }
}
