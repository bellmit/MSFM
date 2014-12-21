//
// -----------------------------------------------------------------------------------
// Source file: MutableOrderId.java
//
// PACKAGE: com.cboe.interfaces.presentation.order
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.util.CBOEId;

public interface MutableOrderId extends MutableBusinessModel, OrderId
{
    public static final String PROPERTY_CBOE_ID = "PROPERTY_CBOE_ID";
    public static final String PROPERTY_EXECUTING_GIVEUP_FIRM = "PROPERTY_EXECUTING_GIVEUP_FIRM";
    public static final String PROPERTY_BRANCH = "PROPERTY_BRANCH";
    public static final String PROPERTY_BRANCH_SEQUENCE = "PROPERTY_BRANCH_SEQUENCE";
    public static final String PROPERTY_CORRESPONDENT_FIRM = "PROPERTY_CORRESPONDENT_FIRM";
    public static final String PROPERTY_ORDER_DATE = "PROPERTY_ORDER_DATE";

    public void setExecutingOrGiveUpFirm(ExchangeFirm newValue);

    public void setBranch(String newValue);

    public void setBranchSequenceNumber(Integer newValue);

    public void setCorrespondentFirm(String newValue);

    public void setOrderDate(String newValue);

    public void setCboeId(CBOEId newValue);
}