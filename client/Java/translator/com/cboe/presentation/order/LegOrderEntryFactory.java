//
// -----------------------------------------------------------------------------------
// Source file: LegOrderDetailFactory.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.LegOrderDetailStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStructV2;

import com.cboe.interfaces.presentation.order.LegOrderEntry;
import com.cboe.interfaces.presentation.order.LegOrderDetail;

public class LegOrderEntryFactory
{
    public static LegOrderEntry createLegOrderEntry(LegOrderEntryStructV2 legOrderEntryStruct)
    {
        return new LegOrderEntryImpl(legOrderEntryStruct);
    }

    public static LegOrderEntry createLegOrderEntry(LegOrderDetailStruct legOrderDetailStruct)
    {
        LegOrderEntryStructV2 entryStruct = new LegOrderEntryStructV2();
        entryStruct.legOrderEntry.clearingFirm = legOrderDetailStruct.clearingFirm;
        entryStruct.legOrderEntry.coverage = legOrderDetailStruct.coverage;
        entryStruct.legOrderEntry.mustUsePrice = legOrderDetailStruct.mustUsePrice;
        entryStruct.legOrderEntry.positionEffect = legOrderDetailStruct.positionEffect;
        entryStruct.legOrderEntry.productKey = legOrderDetailStruct.productKey;
        entryStruct.side = legOrderDetailStruct.side;
        return new LegOrderEntryImpl(entryStruct);
    }

    public static LegOrderEntry[] createLegOrderEntrys(LegOrderDetailStruct[] legOrderDetailStructs)
    {
        LegOrderEntry[] entrys = new LegOrderEntry[legOrderDetailStructs.length];
        for(int i = 0; i < legOrderDetailStructs.length; i++)
        {
            entrys[i] = createLegOrderEntry(legOrderDetailStructs[i]);
        }

        return entrys;
    }

    public static LegOrderEntry[] createLegOrderEntrys(LegOrderDetail[] legOrderDetails)
    {
        LegOrderEntry[] entrys = new LegOrderEntry[legOrderDetails.length];
        LegOrderEntryImpl newImpl;
        LegOrderDetail detail;
        for(int i = 0; i < legOrderDetails.length; i++)
        {
            detail = legOrderDetails[i];

            newImpl = new LegOrderEntryImpl();
            newImpl.clearingFirm = detail.getClearingFirm();
            newImpl.coverage = detail.getCoverage();
            newImpl.mustUsePrice = detail.getMustUsePrice();
            newImpl.positionEffect = detail.getPositionEffect();
            newImpl.productKey = detail.getProductKey();
            newImpl.sellShortIndicator = detail.getSide();
            entrys[i] = newImpl;
        }

        return entrys;
    }
}