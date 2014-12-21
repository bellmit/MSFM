//
// -----------------------------------------------------------------------------------
// Source file: ExchangeAcronymModel.java
//
// PACKAGE: com.cboe.interfaces.presentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.user;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;

public interface ExchangeAcronymModel extends MutableBusinessModel, ExchangeAcronym
{
    void setAcronym(String anAcronym);
    void setExchange(String anExchange);
    void setNeverBeenSaved(boolean neverBeenSaved);
}
