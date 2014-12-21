package com.cboe.client.util.collections;

/**
 * CalculateSizeVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.lang.reflect.*;
import java.util.*;

import com.cboe.client.util.collections.*;
import com.cboe.client.util.*;

public class CalculateSizeVisitor implements ObjectVisitorIF
{
    public int     size   = 0;
    public boolean wasRun = false;

    public CalculateSizeVisitor clear()
    {
        size   = 0;
        wasRun = false;

        return this;
    }

    public boolean wasRun()
    {
        return wasRun;
    }

    public int size()
    {
        return size;
    }

    public int visit(Object value)
    {
        wasRun = true;

        if (value == null)
        {
            size++;
            return ObjectVisitorIF.CONTINUE;
        }

        if (value instanceof HasSizeIF)
        {
            size += ((HasSizeIF) value).size();
            return ObjectVisitorIF.CONTINUE;
        }

        if (value.getClass().isArray())
        {
            size += Array.getLength(value);
            return ObjectVisitorIF.CONTINUE;
        }

        if (value instanceof Collection)
        {
            size += ((Collection) value).size();
            return ObjectVisitorIF.CONTINUE;
        }

        size++;
        return ObjectVisitorIF.CONTINUE;
    }
}

