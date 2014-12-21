package com.cboe.domain.util;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.DateTimeStruct;
/**
 * This is container class for our data struct.
 * @author Connie Feng
 */
public class GroupCancelReportContainer {
    private int[] groups;
    private short statusChange;
	private OrderIdCancelReportContainer data;
    private String userId;
    private DateTimeStruct dateTimeAtCreation;
    private String eventInitiator;

    /**
      * Sets the internal fields to the passed values
      */
    public GroupCancelReportContainer(String userId, int[] groups, OrderIdCancelReportContainer data)
    {
        this( userId, groups, StatusUpdateReasons.NEW, data);
    }

    public GroupCancelReportContainer(String userId, int[] groups, short statusChange, OrderIdCancelReportContainer data) {
		this.groups = groups;
        this.statusChange = statusChange;
		this.data = data;
        this.userId = userId;
        this.dateTimeAtCreation = new DateWrapper().toDateTimeStruct();
    }

    public GroupCancelReportContainer(String userId, int[] groups, short statusChange, OrderIdCancelReportContainer data, String eventInitiator) {
        this(userId, groups, statusChange, data);
        this.eventInitiator = eventInitiator;
    }

    public String getUserId()
    {
        return userId;
    }

    public int[] getGroups()
    {
        return groups;
    }

    public short getStatusChange()
    {
        return statusChange;
    }

    public OrderIdCancelReportContainer getCancelReport()
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
