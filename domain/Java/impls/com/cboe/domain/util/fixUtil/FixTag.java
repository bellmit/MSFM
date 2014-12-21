package com.cboe.domain.util.fixUtil;

import java.lang.reflect.Field;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

/**
 * Represents a FIX tag, which has a tag name and tag number.
 * 
 * Date: Aug 17, 2004
 */
public class FixTag implements Comparable
{
    public static final String INVALID_TAG_NAME = "Invalid Tag Name";
    public static final int INVALID_TAG_NUM = -1;
    protected String tagName;
    protected int tagNum;
    protected String renderString;

    private FixTag()
    {
    }

    /**
     * @param fixConstantClass must have public static fields "TAGNAME" and "TAGNUMBER".
     */ 
    public FixTag(Class fixConstantClass)
    {
        try
        {
            Field nameField = fixConstantClass.getDeclaredField("TAGNAME");
            Field numberField = fixConstantClass.getDeclaredField("TAGNUMBER");
            this.tagName = (String)nameField.get(null);
            this.tagNum = ((Integer)numberField.get(null)).intValue();
        }
        catch(NoSuchFieldException e)
        {
            throw new IllegalArgumentException("com.cboe.domain.util.fixUtil.FixTag: '"+fixConstantClass.getName()+"' doesn't have TAGNAME and/or TAGNUMBER field");
        }
        catch(IllegalAccessException e)
        {
            Log.exception(e);
        }
    }

    public FixTag(String name, int num)
    {
        this.tagName = name;
        this.tagNum = num;
    }
    
    public String getTagName()
    {
        return tagName;
    }

    public int getTagNumber()
    {
        return tagNum;
    }

    public boolean equals(Object o)
    {
        boolean retVal = false;
        if(o instanceof FixTag)
        {
            if(getTagNumber() == ((FixTag)o).getTagNumber())
            {
                retVal = true;            
            }
        }
        return retVal;
    }

    public String toString()
    {
        if(renderString == null)
        {
            renderString = tagName + " (" + tagNum +")";
        }
        return renderString;
    }

    // default ordering by tag number
    public int compareTo(Object obj)
    {
        if(this == obj)
        {
            return 0;
        }
        else if(obj instanceof FixTag)
        {
            int us = getTagNumber();
            int them = ((FixTag)obj).getTagNumber();
            return us < them ? -1 : (us == them ? 0 : 1);
        }
        else
        {
            return -1;
        }
    }
}
