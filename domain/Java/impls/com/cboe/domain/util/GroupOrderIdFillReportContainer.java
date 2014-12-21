package com.cboe.domain.util;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.DateTimeStruct;
/**
 * This is container class for our data struct.
 * @author Connie Feng
 */
public class GroupOrderIdFillReportContainer {
    private int[] groups;
    private short statusChange;
    private OrderStruct order;
	private FilledReportStruct[] data;
	private DateTimeStruct dateTimeAtCreation;
    private String eventInitiator;

    /**
      * Sets the internal fields to the passed values
      */
    public GroupOrderIdFillReportContainer(int[] groups, OrderStruct order, FilledReportStruct[] data)
    {
        this( groups, StatusUpdateReasons.NEW, order, data );
    }

    public GroupOrderIdFillReportContainer(int[] groups, short statusChange, OrderStruct order, FilledReportStruct[] data) {
		this.groups = groups;
        this.statusChange = statusChange;
        this.order = order;
		this.data = data;
		this.dateTimeAtCreation = new DateWrapper().toDateTimeStruct();
    }

    public GroupOrderIdFillReportContainer(int[] groups, short statusChange, OrderStruct order, FilledReportStruct[] data, String eventInitiator) {
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

    public FilledReportStruct[] getFilledReportStruct()
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
