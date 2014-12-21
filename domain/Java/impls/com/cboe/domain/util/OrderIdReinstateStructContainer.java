package com.cboe.domain.util;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiConstants.*;
/**
 * This is container class for our data struct.
 * @author Connie Feng
 */
public class OrderIdReinstateStructContainer {
    private String userId;
    private short statusChange;
    private OrderStruct order;
    private BustReinstateReportStruct data;
    private int[] groups;
    private String eventInitiator;
    /**
      * Sets the internal fields to the passed values
      */
    public OrderIdReinstateStructContainer(String userId, OrderStruct order, BustReinstateReportStruct data)
    {
        this( new int[0], userId, StatusUpdateReasons.NEW, order, data );
    }

    public OrderIdReinstateStructContainer(int[] groups, String userId, short statusChange, OrderStruct order, BustReinstateReportStruct data) {
        this.groups = groups;
        this.userId = userId;
        this.statusChange = statusChange;
		this.order = order;
		this.data = data;
    }

    public OrderIdReinstateStructContainer(int[] groups, String userId, short statusChange, OrderStruct order, BustReinstateReportStruct data, String eventInitiator) {
        this(groups, userId, statusChange, order, data);
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

    public OrderStruct getOrderStruct()
    {
        return order;
    }

    public BustReinstateReportStruct getBustReinstateReportStruct()
    {
        return data;
    }
    public String getEventInitiator()
    {
        return eventInitiator;
    }
}
