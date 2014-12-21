package com.cboe.domain.util;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiUtil.DateTimeStruct;
/**
 * This is container class for our data struct.
 * @author Connie Feng
 */
public class OrderIdCancelReportContainer {
    private OrderStruct order;
    private CancelReportStruct[] data;
    private DateTimeStruct dateTimeAtCreation;
    /**
      * Sets the internal fields to the passed values
      */
    public OrderIdCancelReportContainer(OrderStruct order, CancelReportStruct[] data) {
		this.order = order;
		this.data = data;
		this.dateTimeAtCreation = new DateWrapper().toDateTimeStruct();
    }
    public OrderStruct getOrderStruct()
    {
        return order;
    }

    public CancelReportStruct[] getCancelReportStruct()
    {
        return data;
    }
    public DateTimeStruct getDateTimeAtCreation()
    {
        return dateTimeAtCreation;
    }
}
