//
// -----------------------------------------------------------------------------------
// Source file: StructBuilder.java
//
// PACKAGE: com.cboe.presentation.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.util;

import com.cboe.idl.cmiOrder.ContraPartyStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;

public class StructBuilder
{
    public static ContraPartyStruct cloneContraPartyStruct(ContraPartyStruct origStruct)
    {
        ContraPartyStruct clonedStruct = null;
        if (origStruct != null)
        {
            clonedStruct = new ContraPartyStruct();
            clonedStruct.user = cloneExchangeAcronymStruct(origStruct.user);
            clonedStruct.firm = cloneExchangeFirmStruct(origStruct.firm);
            clonedStruct.quantity = origStruct.quantity;
        }
        return clonedStruct;
    }

    public static ExchangeAcronymStruct cloneExchangeAcronymStruct(ExchangeAcronymStruct struct)
    {
        ExchangeAcronymStruct result = null;
        if (struct != null)
        {
            result = new ExchangeAcronymStruct(StringCache.get(struct.exchange), StringCache.get(struct.acronym));
        }
        return result;
    }

    public static ExchangeFirmStruct cloneExchangeFirmStruct(ExchangeFirmStruct struct)
    {
        ExchangeFirmStruct result = null;
        if (struct != null)
        {
            result = new ExchangeFirmStruct(StringCache.get(struct.exchange), StringCache.get(struct.firmNumber));
        }
        return result;
    }

    public static CancelReportStruct cloneCancelReportStruct(CancelReportStruct origStruct)
    {
        CancelReportStruct clonedStruct = null;

        if(origStruct != null)
        {
            clonedStruct = new CancelReportStruct();
            clonedStruct.orderId = cloneOrderIdStruct(origStruct.orderId);
            clonedStruct.cancelReportType = origStruct.cancelReportType;
            clonedStruct.cancelReason = origStruct.cancelReason;
            clonedStruct.productKey = origStruct.productKey;
            clonedStruct.sessionName = StringCache.get(origStruct.sessionName);
            clonedStruct.cancelledQuantity = origStruct.cancelledQuantity;
            clonedStruct.tlcQuantity = origStruct.tlcQuantity;
            clonedStruct.mismatchedQuantity = origStruct.mismatchedQuantity;
            clonedStruct.timeSent = com.cboe.domain.util.StructBuilder.cloneDateTime(origStruct.timeSent);
            clonedStruct.orsId = StringCache.get(origStruct.orsId);
            clonedStruct.totalCancelledQuantity = origStruct.totalCancelledQuantity;
            clonedStruct.transactionSequenceNumber = origStruct.transactionSequenceNumber;
            clonedStruct.userAssignedCancelId = StringCache.get(origStruct.userAssignedCancelId);
        }
        return clonedStruct;
    }

    public static FilledReportStruct cloneFilledReportStruct(FilledReportStruct origStruct)
    {
        FilledReportStruct clonedStruct = null;
        if (origStruct != null)
        {
            clonedStruct = new FilledReportStruct();
            clonedStruct.tradeId = com.cboe.domain.util.StructBuilder.cloneCboeId(origStruct.tradeId);
            clonedStruct.fillReportType = origStruct.fillReportType;
            clonedStruct.executingOrGiveUpFirm = cloneExchangeFirmStruct(origStruct.executingOrGiveUpFirm);
            clonedStruct.userId = StringCache.get(origStruct.userId);
            clonedStruct.userAcronym = cloneExchangeAcronymStruct(origStruct.userAcronym);
            clonedStruct.productKey = origStruct.productKey;
            clonedStruct.sessionName = StringCache.get(origStruct.sessionName);
            clonedStruct.tradedQuantity = origStruct.tradedQuantity;
            clonedStruct.leavesQuantity = origStruct.leavesQuantity;
            clonedStruct.price = com.cboe.domain.util.StructBuilder.clonePrice(origStruct.price);
            clonedStruct.side = origStruct.side;
            clonedStruct.orsId = StringCache.get(origStruct.orsId);
            clonedStruct.executingBroker = StringCache.get(origStruct.executingBroker);
            clonedStruct.cmta = cloneExchangeFirmStruct(origStruct.cmta);
            clonedStruct.account = StringCache.get(origStruct.account);
            clonedStruct.subaccount = StringCache.get(origStruct.subaccount);
            clonedStruct.originator = cloneExchangeAcronymStruct(origStruct.originator);
            clonedStruct.optionalData = StringCache.get(origStruct.optionalData);
            clonedStruct.userAssignedId = StringCache.get(origStruct.userAssignedId);
            clonedStruct.extensions = StringCache.get(origStruct.extensions);
            clonedStruct.contraParties = new ContraPartyStruct[origStruct.contraParties.length];
            for (int i = 0; i < clonedStruct.contraParties.length; i++)
            {
                clonedStruct.contraParties[i] = cloneContraPartyStruct(origStruct.contraParties[i]);
            }
            clonedStruct.timeSent = com.cboe.domain.util.StructBuilder.cloneDateTime(origStruct.timeSent);
            clonedStruct.positionEffect = origStruct.positionEffect;
            clonedStruct.transactionSequenceNumber = origStruct.transactionSequenceNumber;
        }
        return clonedStruct;
    }

    public static OrderIdStruct cloneOrderIdStruct(OrderIdStruct origStruct)
    {
        OrderIdStruct clonedStruct = null;
        if(origStruct != null)
        {
            clonedStruct = new OrderIdStruct();
            clonedStruct.executingOrGiveUpFirm = cloneExchangeFirmStruct(origStruct.executingOrGiveUpFirm);
            clonedStruct.branch = StringCache.get(origStruct.branch);
            clonedStruct.branchSequenceNumber = origStruct.branchSequenceNumber;
            clonedStruct.correspondentFirm = StringCache.get(origStruct.correspondentFirm);
            clonedStruct.orderDate = StringCache.get(origStruct.orderDate);
            clonedStruct.highCboeId = origStruct.highCboeId;
            clonedStruct.lowCboeId = origStruct.lowCboeId;
        }
        return clonedStruct;
    }
}
