package com.cboe.domain.util;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.DateTimeStruct;
/**
 * This is container class for our data struct.
 * @author Connie Feng
 */
public class OrderIdBustStructContainer {
    private short statusChange;
    private OrderStruct order;
    private BustReportStruct[] data;
    private int[] groups;
    private DateTimeStruct dateTimeAtCreation;
    private String eventInitiator;
    /**
      * Sets the internal fields to the passed values
      */
    public OrderIdBustStructContainer(OrderStruct order, BustReportStruct[] data)
    {
        this( new int[0], StatusUpdateReasons.NEW, order, data );
    }

    public OrderIdBustStructContainer(int[] groups, short statusChange, OrderStruct order, BustReportStruct[] data) {
        this.groups = groups;
		this.statusChange = statusChange;
        this.order = order;
		this.data = data;
		this.dateTimeAtCreation = new DateWrapper().toDateTimeStruct();
    }

    public OrderIdBustStructContainer(int[] groups, short statusChange, OrderStruct order, BustReportStruct[] data, String eventInitiator) {
        this(groups, statusChange, order, data);
        this.eventInitiator = eventInitiator;
    }

    public int[] getGroups()
    {
        return groups;
    }

    public short getStatusChange()
    {
        return statusChange;
    }

    public OrderStruct getOrderStruct()
    {
        return order;
    }

    public BustReportStruct[] getBustReportStruct()
    {
        return data;
    }

    public DateTimeStruct getDateTimeAtCreation()
    {
        return dateTimeAtCreation;
    }
    public String getEventInitiator()
    {
        return eventInitiator;
    }
}
