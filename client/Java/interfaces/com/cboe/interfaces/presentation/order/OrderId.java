//
// -----------------------------------------------------------------------------------
// Source file: OrderId.java
//
// PACKAGE: com.cboe.interfaces.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.idl.cmiOrder.OrderIdStruct;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.util.CBOEId;

public interface OrderId extends BusinessModel, Comparable
{
    /**
     * Gets the underlying struct
     * @return OrderIdStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public OrderIdStruct getStruct();

    public ExchangeFirm getExecutingOrGiveUpFirm();

    public String getBranch();

    public Integer getBranchSequenceNumber();

    public String getCorrespondentFirm();

    public String getOrderDate();

    public CBOEId getCboeId();

    public String getFormattedBranchSequence();
}