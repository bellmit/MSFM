/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 18, 2002
 * Time: 10:07:39 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.domain.util;

import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct;

public class HeldOrderFilledReportContainer {
    private HeldOrderStruct heldOrder;
	private FilledReportStruct[] data;

    /**
      * Sets the internal fields to the passed values
      */

    public HeldOrderFilledReportContainer(HeldOrderStruct heldOrder, FilledReportStruct[] data) {
        this.heldOrder = heldOrder;
		this.data = data;
    }

    public HeldOrderStruct getHeldOrderStruct()
    {
        return heldOrder;
    }

    public FilledReportStruct[] getFilledReportStruct()
    {
        return data;
    }
}
