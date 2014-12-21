
//------------------------------------------------------------------------------------------------------------------
// FILE:    ValidationErrorCodes.java
//
// PACKAGE: com.cboe.interfaces.presentation.validation
//
//-------------------------------------------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
//-------------------------------------------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.validation;

public interface ValidationErrorCodes
{
    public final static int NO_ERROR = 0;
    public final static int VALID = 0;
    public final static int QUOTE_BID_PRICE_INVALID = 1;
    public final static int QUOTE_BID_QUANTITY_INVALID = 2;
    public final static int QUOTE_ASK_PRICE_INVALID = 3;
    public final static int QUOTE_ASK_QUANTITY_INVALID = 4;
    public final static int QUOTE_EMPTY = 5;
    public final static int QUOTE_INVALID = 10;

    public final static int ORDER_PRICE_INVALID = 11;
    public final static int ORDER_QUANTITY_INVALID = 12;
    public final static int ORDER_BRANCH_INVALID = 13;
    public final static int ORDER_OPTIONAL_DATA_INVALID = 14;

    public final static int ORDER_INVALID = 20;

    public final static int PRODUCT_NO_REPORTING_CLASS = 30;
    public final static int PRODUCT_INVALID_OPRA_MONTH_CODE = 31;
    public final static int PRODUCT_INVALID_OPRA_PRICE_CODE = 32;

    public final static int BLOCK_TRADE_INVALID_ASOF_DATE = 50;

    public final static int BLOCK_TRADE_NO_PRODUCT = 101;
    public final static int BLOCK_TRADE_PRICE_INVALID = 102;
    public final static int BLOCK_TRADE_QUANTITY_INVALID = 103;
    public final static int BLOCK_TRADE_NO_USER = 105;
    public final static int BLOCK_TRADE_INVALID_USER = 106;
    public final static int BLOCK_TRADE_ACCOUNT_INVALID = 107;
    public final static int BLOCK_TRADE_NO_CLEARING_FIRM = 108;
    
    public final static int FLOOR_TRADE_INVALID_ASOF_DATE = 120;
    public final static int FLOOR_TRADE_NO_PRODUCT = 121;
    public final static int FLOOR_TRADE_PRICE_INVALID = 122;
    public final static int FLOOR_TRADE_QUANTITY_INVALID = 124;
    public final static int FLOOR_TRADE_ACCOUNT_INVALID = 125;
    public final static int FLOOR_TRADE_NO_CLEARING_FIRM = 126;
    
    
}