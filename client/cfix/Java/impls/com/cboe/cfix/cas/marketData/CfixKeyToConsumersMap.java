package com.cboe.cfix.cas.marketData;

/**
 * CfixKeyToConsumersMap.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.*;
import com.cboe.client.util.collections.*;
import com.cboe.interfaces.cfix.*;

public class CfixKeyToConsumersMap
{
    protected IntObjectMultipleValuesMap intObjectMultipleValuesMap = IntObjectMultipleValuesMap.synchronizedMap();
    protected String                     name                       = "unnamed";

    public CfixKeyToConsumersMap()
    {

    }

    public CfixKeyToConsumersMap(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return name;
    }

    public String getName()
    {
        return name;
    }

    public boolean isEmpty()
    {
        return intObjectMultipleValuesMap.isEmpty();
    }

    public int addConsumerToKey(int key, CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)
    {
        MutableInteger mutableInteger = MutableInteger.threadLocalMutableInteger.getMutableInteger();

        intObjectMultipleValuesMap.putKeyValue(key, cfixFixMarketDataConsumerHolder, mutableInteger);

        return mutableInteger.integer;
    }

    public int removeConsumerFromKey(int key, CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)
    {
        MutableInteger mutableInteger = MutableInteger.threadLocalMutableInteger.getMutableInteger();

        intObjectMultipleValuesMap.removeKeyValue(key, cfixFixMarketDataConsumerHolder, mutableInteger);

        return mutableInteger.integer;
    }

    public int countConsumersForKey(int key)
    {
        return intObjectMultipleValuesMap.countValues(key);
    }

    public void getConsumersForKey(int key, ObjectArrayHolder arrayHolder)
    {
        intObjectMultipleValuesMap.getValuesForKey(key, arrayHolder);
    }

    public void getKeysForConsumer(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, IntArrayHolder arrayHolder)
    {
        intObjectMultipleValuesMap.getKeysForValue(cfixFixMarketDataConsumerHolder, arrayHolder);
    }

    public void findConsumerHolders(final CfixMarketDataConsumer cfixFixMarketDataConsumer, final ObjectArrayHolder arrayHolder)
    {
        IntObjectVisitorIF visitor = new IntObjectVisitorIF()
        {
            public int visit(int key, Object value)
            {
                if (((CfixMarketDataConsumerHolder) (value)).getCfixMarketDataConsumer() == cfixFixMarketDataConsumer)
                {
                    arrayHolder.add(value);
                }

                return CONTINUE;
            }
        };

        intObjectMultipleValuesMap.acceptVisitor(visitor);
    }

    public void getData(IntObjectArrayHolder arrayHolder)
    {
        intObjectMultipleValuesMap.getData(arrayHolder);
    }

    public int size()
    {
        return intObjectMultipleValuesMap.size();
    }
}