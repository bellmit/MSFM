package com.cboe.application.cache;

import java.util.*;

public abstract class AbstractGroupIterator implements Iterator
{
    protected Object  fromObject = null;
    protected boolean hasNext = true;

    public AbstractGroupIterator(Object o)
    {
        fromObject = o;
    }

    public boolean hasNext() {return hasNext;}
    public void remove() {throw new UnsupportedOperationException();};
}

