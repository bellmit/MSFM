//
// -----------------------------------------------------------------------------------
// Source file: FilledReport.java
//
// PACKAGE: com.cboe.interfaces.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.util.CBOEId;

public interface FilledReport
{
    /**
     * Gets the underlying struct
     * @return FilledReportStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public FilledReportStruct getStruct();

    public CBOEId getTradeId();
    public Short getFillReportType();
    public ExchangeFirm getExecutingOrGiveUpFirm();
    public String getUserId();
    public ExchangeAcronym getUserAcronym();
    public Integer getProductKey();
    public String getSessionName();
    public Integer getTradedQuantity();
    public Integer getLeavesQuantity();
    public Price getPrice();
    public Character getSide();
    public String getOrsId();
    public String getExecutingBroker();
    public ExchangeFirm getCmta();
    public String getAccount();
    public String getSubaccount();
    public ExchangeAcronym getOriginator();
    public String getOptionalData();
    public String getUserAssignedId();
    public String getExtensions();
    public ContraParty[] getContraParties();
    public DateTime getTimeSent();
    public Character getPositionEffect();
    public Integer getTransactionSequenceNumber();
    public String getExtensionField(String extensionField);
}