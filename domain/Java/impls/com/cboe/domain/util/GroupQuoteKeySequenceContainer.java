package com.cboe.domain.util;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiConstants.*;

/**
 * This is container class for our data struct.
 * @author Connie Feng
 */

public class GroupQuoteKeySequenceContainer
{
    private int[] quoteKeys;
    private int[] groups;

    /**
      * Sets the internal fields to the passed values
      */
    public GroupQuoteKeySequenceContainer(int[] groups, int[] quoteKeys)
    {
        this.groups         = groups;
		this.quoteKeys      = quoteKeys;
    }

    public int[] getGroups()
    {
        return groups;
    }

    public int[] getQuoteKeys()
    {
        return quoteKeys;
    }

}
