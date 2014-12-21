package com.cboe.client.util.collections;

/**
 * ObjectObjectArrayHolderIF.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED BY VELOCITY TEMPLATE ENGINE FROM /vobs/dte/client/generator/DV_XYArrayHolderIF.java (KEY_TYPE=Object, VALUE_TYPE=Object)
 *
 */

import com.cboe.client.util.*;

public interface ObjectObjectArrayHolderIF extends HasSizeIF
{
    public int size();
    public boolean isEmpty();
    public int capacity();
    public int ensureCapacity(int capacity);
    public Object[] keys();
    public Object[] getKeys();
    public Object[] values();
    public Object[] getValues();
    public void add(Object key, Object value);
    public Object getKey(int index);
    public Object getValue(int index);
    public boolean containsKey(Object key);
    public boolean containsValue(Object value);
    public ObjectObjectArrayHolderIF clear();
    public ObjectObjectVisitorIF acceptVisitor(ObjectObjectVisitorIF visitor);
}
