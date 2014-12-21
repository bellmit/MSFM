package com.cboe.domain.util;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiConstants.*;
/**
 * This is container class for our data struct.
 * @author Connie Feng
 */
public class GroupOrderStructContainer {
    private int[] groups;
    private short statusChange;
    private OrderStruct data;
    private String eventInitiator;

    /**
      * Sets the internal fields to the passed values
      */
    public GroupOrderStructContainer(int[] groups, OrderStruct data)
    {
        this( groups, StatusUpdateReasons.NEW, data );
    }

    public GroupOrderStructContainer(int[] groups, short statusChange, OrderStruct data) {
		this.groups = groups;
        this.statusChange = statusChange;
		this.data = data;
    }

    public GroupOrderStructContainer(int[] groups, OrderStruct data, String eventInitiator) {
        this(groups, StatusUpdateReasons.NEW, data);
        this.eventInitiator = eventInitiator;
    }

    public GroupOrderStructContainer(int[] groups, short statusChange, OrderStruct data, String eventInitiator) {
        this(groups, statusChange, data);
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
        return data;
    }
    public String getEventInitiator()
    {
        return eventInitiator;
    }
}
