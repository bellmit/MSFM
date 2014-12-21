//
// -----------------------------------------------------------------------------------
// Source file: ExchangeAcronymFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;

/**
 * Title:        ExchangeAcronymFormatStrategy
 * Description:  Interface for an ExchangeAcronym formatter
 * Copyright:    Copyright (c) 2002
 * Company:      Chicago Board Options Exchange
 * @version 1.0
 */

public interface ExchangeAcronymFormatStrategy extends FormatStrategy
{
    final public static String BRIEF="BRIEF";
    final public static String FULL="FULL";
    final public static String BRIEF_DESC="exchangeAcronym.userAcronym";
    final public static String FULL_DESC="Exchange: exchangeAcronym, Acronym: userAcronym";

    public String format(ExchangeAcronymStruct exchangeAcronym);
    public String format(ExchangeAcronymStruct exchangeAcronym, String styleName);

    public String format(ExchangeAcronym exchangeAcronym);
    public String format(ExchangeAcronym exchangeAcronym, String styleName);
}

