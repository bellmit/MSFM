package com.cboe.domain.util;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiConstants.*;
/**
 * This is container class for our data struct.
 * @author Connie Feng
 */
public class GroupOrderStructSequenceContainer
{
    private int[] groups;
    private short statusChange;
    private OrderStruct[] data;

    /**
      * Sets the internal fields to the passed values
      */
    public GroupOrderStructSequenceContainer(int[] groups, OrderStruct[] data)
    {
        this( groups, StatusUpdateReasons.NEW, data );
    }

    public GroupOrderStructSequenceContainer(int[] groups, short statusChange, OrderStruct[] data)
    {
		this.groups = groups;
        this.statusChange = statusChange;
		this.data = data;
    }

    public int[] getGroups()
    {
        return groups;
    }

    public short getStatusChange()
    {
        return statusChange;
    }

    public OrderStruct[] getOrderStructSequence()
    {
        return data;
    }
}
