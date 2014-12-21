package com.cboe.domain.routingProperty;

import com.cboe.domain.util.ClientProductStructBuilder;
import com.cboe.domain.util.StructBuilder;
import com.cboe.idl.cmiConstants.ClassStates;
import com.cboe.idl.cmiConstants.ProductClass;
import com.cboe.idl.cmiConstants.ProductKeys;
import com.cboe.idl.cmiConstants.SessionNameValues;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.firm.FirmStruct;
import com.cboe.idl.session.TradingSessionStruct;

// -----------------------------------------------------------------------------------
// Source file: RoutingPropertyHelper
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Aug 22, 2006 3:01:04 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


public class RoutingPropertyHelper
{
    //  constants
    public static final String DEFAULT_STR_VALUE = "Default";
    public static final char DEFAULT_CHAR_VALUE = '*';

    public static final int DEFAULT_CLASS_KEY = ProductClass.DEFAULT_CLASS_KEY;
    public static final int DEFAULT_PRODUCT_KEY = ProductKeys.DEFAULT_PRODUCT_KEY;
    public static final String DEFAULT_EXCHANGE = DEFAULT_STR_VALUE;
    public static final String DEFAULT_FIRM_NUMBER = DEFAULT_STR_VALUE;
    public static final String DEFAULT_FIRM_ACRONYM = DEFAULT_STR_VALUE;
    public static final String DEFAULT_FIRM_FULLNAME = "Default Firm";
    public static final String DEFAULT_SESSION = SessionNameValues.ALL_SESSION_NAME;

    private RoutingPropertyHelper()
    {
    }

    public static FirmStruct buildDefaultFirmStruct()
    {
        FirmStruct struct = new FirmStruct();

        struct.firmAcronym = DEFAULT_FIRM_ACRONYM;
        struct.fullName = DEFAULT_FIRM_FULLNAME;
        struct.isActive = true;

        struct.lastModifiedTime = StructBuilder.buildDateTimeStruct();
        struct.firmNumber = StructBuilder.buildExchangeFirmStruct(DEFAULT_EXCHANGE, DEFAULT_FIRM_NUMBER);

        return struct;
    }

    public static TradingSessionStruct buildAllTradingSessionStruct()
    {
        TradingSessionStruct struct = new TradingSessionStruct();

        struct.sessionName = DEFAULT_SESSION;
        struct.sequenceNumber = 0;

        struct.businessDay = StructBuilder.buildDateStruct();
        struct.endTime = StructBuilder.buildTimeStruct();
        struct.startTime = StructBuilder.buildTimeStruct();

        struct.exchangeAcronym = "";
        struct.endOfSessionStrategy = "";

        return struct;
    }

    public static SessionClassStruct buildSessionClassStruct()
    {
        SessionClassStruct sc = new SessionClassStruct();

        sc.classStruct = ClientProductStructBuilder.buildClassStruct();
        sc.classState = ClassStates.NOT_IMPLEMENTED;
        sc.classStateTransactionSequenceNumber = 0;
        sc.eligibleSessions = new String[0];
        sc.sessionName = DEFAULT_SESSION;
        sc.underlyingSessionName = sc.sessionName;
        sc.classStruct.classKey = DEFAULT_CLASS_KEY;

        return sc;
    }

    public static SessionProductStruct buildSessionProductStruct()
    {
        SessionProductStruct spc = ClientProductStructBuilder.buildSessionProductStruct();
        spc.productState = ClassStates.NOT_IMPLEMENTED;
        spc.productStateTransactionSequenceNumber = 0;
        spc.sessionName = DEFAULT_SESSION;
        spc.productStruct.productKeys.classKey = DEFAULT_CLASS_KEY;
        spc.productStruct.productKeys.productKey = DEFAULT_PRODUCT_KEY;
        return spc;
    }

    public static String firstCharToUpper(String str)
    {
        if (str != null && str.length() > 0)
        {
            return Character.toUpperCase(str.charAt(0)) + str.substring(1, str.length());
        }
        else
        {
            return str;
        }
    }

}
