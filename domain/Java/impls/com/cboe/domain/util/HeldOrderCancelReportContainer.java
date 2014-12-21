/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 11, 2002
 * Time: 11:50:25 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.domain.util;

import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct;

public class HeldOrderCancelReportContainer
{

    private CboeIdStruct cancelRequestId;
    private CancelReportStruct cancelReportStruct;
    private HeldOrderStruct heldOrder;

    /**
      * Sets the internal fields to the passed values
      */
    public HeldOrderCancelReportContainer(HeldOrderStruct heldOrder, CboeIdStruct cancelId,  CancelReportStruct cancelReport)
    {
        this.cancelRequestId = cancelId;
        this.cancelReportStruct = cancelReport;
        this.heldOrder = heldOrder;

    }
    public CboeIdStruct getCancelRequestId()
    {
        return cancelRequestId;
    }

    public CancelReportStruct getCancelReport()
    {
        return cancelReportStruct;
    }

    public HeldOrderStruct getHeldOrder()
    {
        return heldOrder;
    }
}
