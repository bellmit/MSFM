package com.cboe.domain.util;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiConstants.*;
/**
 * This is container class for our data struct.
 * @author Connie Feng
 */
public class QuoteNotificationStructContainer
{
    private int[] groups;
    private LockNotificationStruct[] data;

    /**
      * Sets the internal fields to the passed values
      */
    public QuoteNotificationStructContainer(LockNotificationStruct[] data)
    {
		this.data = data;
    }

    public LockNotificationStruct[] getLockNotificationStruct()
    {
        return data;
    }
}
