//
// -----------------------------------------------------------------------------------
// Source file: ExchangeFirmFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.interfaces.presentation.user.ExchangeFirm;

/**
 * Title:        ExchangeFirmFormatStrategy
 * Description:  Interface for an ExchangeFirm formatter
 * Copyright:    Copyright (c) 2002
 * Company:      Chicago Board Options Exchange
 * @version 1.0
 */

public interface ExchangeFirmFormatStrategy extends FormatStrategy
{
    final public static String BRIEF="BRIEF";
    final public static String FULL="FULL";
    final public static String BRIEF_DESC="exchangeAcronym.firmNumber";
    final public static String FULL_DESC="Exchange: exchangeAcronym, Firm: firmNumber";

    public String format(ExchangeFirmStruct exchangeFirm);
    public String format(ExchangeFirmStruct exchangeFirm, String styleName);

    public String format(ExchangeFirm exchangeFirm);
    public String format(ExchangeFirm exchangeFirm, String styleName);
}

