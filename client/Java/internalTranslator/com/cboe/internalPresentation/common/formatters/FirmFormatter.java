//
// -----------------------------------------------------------------------------------
// Source file: FirmFormatter.java
//
// PACKAGE: com.cboe.internalPresentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

import com.cboe.interfaces.internalPresentation.firm.FirmModel;

public class FirmFormatter
{
    //todo: implement formatter styles, etc.
    public static String toString(FirmModel firmModel)
    {
        StringBuffer string = new StringBuffer(100);
        string.append(firmModel.getAcronym()).append(':').append(firmModel.getFirmNumber());
        string.append(" - ").append(firmModel.getFullName());
        string.append(" (").append(firmModel.isActive() ? "Active" : "InActive").append(')');
        string.append(" (").append(firmModel.getFirmExchange()).append(')');
        return string.toString();
    }
}
