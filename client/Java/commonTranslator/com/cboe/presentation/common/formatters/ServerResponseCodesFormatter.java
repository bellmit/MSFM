//
// -----------------------------------------------------------------------------------
// Source file: ServerResponseCodesFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.constants.ServerResponseCodes;

public class ServerResponseCodesFormatter
{
    private static final String SUCCESS_STR = "SUCCESS";
    private static final String ALREADY_EXISTS_EXCEPTION_STR = "Already Exists Exception";
    private static final String AUTHENTICATION_EXCEPTION_STR = "Authentication Exception";
    private static final String COMMUNICATION_EXCEPTION_STR = "Communication Exception";
    private static final String DATA_VALIDATION_EXCEPTION_STR = "Data Validation Exception";
    private static final String NOT_FOUND_EXCEPTION_STR = "Not Found Exception";
    private static final String NOT_ACCEPTED_EXCEPTION_STR = "Not Accepted Exception";
    private static final String NOT_SUPPORTED_EXCEPTION_STR = "Not Supported Exception";
    private static final String SYSTEM_EXCEPTION_STR = "System Exception";
    private static final String TRANSACTION_FAILED_EXCEPTION_STR = "Transaction Failed Exception";

    public static String toString(short serverResponseCode)
    {
        String responseCodeStr;
        switch (serverResponseCode)
        {
            case ServerResponseCodes.SUCCESS:
                responseCodeStr = SUCCESS_STR;
                break;
            case ServerResponseCodes.SYSTEM_EXCEPTION:
                responseCodeStr = SYSTEM_EXCEPTION_STR;
                break;
            case ServerResponseCodes.COMMUNICATION_EXCEPTION:
                responseCodeStr = COMMUNICATION_EXCEPTION_STR;
                break;
            case ServerResponseCodes.DATA_VALIDATION_EXCEPTION:
                responseCodeStr = DATA_VALIDATION_EXCEPTION_STR;
                break;
            case ServerResponseCodes.NOT_ACCEPTED_EXCEPTION:
                responseCodeStr = NOT_ACCEPTED_EXCEPTION_STR;
                break;
            case ServerResponseCodes.TRANSACTION_FAILED_EXCEPTION:
                responseCodeStr = TRANSACTION_FAILED_EXCEPTION_STR;
                break;
            case ServerResponseCodes.ALREADY_EXISTS_EXCEPTION:
                responseCodeStr = ALREADY_EXISTS_EXCEPTION_STR;
                break;
            case ServerResponseCodes.AUTHENTICATION_EXCEPTION:
                responseCodeStr = AUTHENTICATION_EXCEPTION_STR;
                break;
            case ServerResponseCodes.NOT_FOUND_EXCEPTION:
                responseCodeStr = NOT_FOUND_EXCEPTION_STR;
                break;
            case ServerResponseCodes.NOT_SUPPORTED_EXCEPTION:
                responseCodeStr = NOT_SUPPORTED_EXCEPTION_STR;
                break;
            default:
                responseCodeStr = "Unknown Server Response Code '"+serverResponseCode+"'";
                break;
        }
        return responseCodeStr;
    }
}
