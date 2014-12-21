package com.cboe.client.util.junit;

/**
 * junitReferenceCountMap.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.collections.*;

public class junitReferenceCountMap extends JunitTestCase
{
    public void testStringReferenceCountMap() throws Exception
    {
        StringReferenceCountMap map = new StringReferenceCountMap();
        int val;

        map.incKeyValue("15");

        val = map.getValueForKey("100");
        assertEquals(0, val);

        map.putKeyValue("10", 5);
        val = map.getValueForKey("10");
        assertEquals(5, val);

        val = map.getValueForKey("15");
        assertEquals(1, val);

        map.decKeyValue("15");
        val = map.getValueForKey("15");
        assertEquals(0, val);

        map.incKeyValue("15", 3);
        val = map.getValueForKey("15");
        assertEquals(3, val);

        map.decKeyValuePositive("15");
        val = map.getValueForKey("15");
        assertEquals(2, val);

        map.decKeyValuePositive("15");
        val = map.getValueForKey("15");
        assertEquals(1, val);

        map.decKeyValuePositive("15");
        val = map.getValueForKey("15");
        assertEquals(0, val);

        map.decKeyValuePositive("15");
        val = map.getValueForKey("15");
        assertEquals(0, val);

        map.incKeyValue("15", 3);
        val = map.getValueForKey("15");
        assertEquals(3, val);

        map.decKeyValuePositive("15", 10);
        val = map.getValueForKey("15");
        assertEquals(0, val);

        map.incKeyValue("15", 10);
        val = map.getValueForKey("15");
        assertEquals(10, val);

        map.decKeyValue("15", 12);
        val = map.getValueForKey("15");
        assertEquals(-2, val);

        map.putKeyValue("15", 12);
        val = map.getValueForKey("15");
        assertEquals(12, val);

        ObjectIntVisitorIF visitor = new ObjectIntVisitorIF()
        {
            public int visit(Object key, int value)
            {
                if ("15".equals(key))
                {
                    assertEquals(12, value);
                }
                else if ("10".equals(key))
                {
                    assertEquals(5, value);
                }

                return CONTINUE;
            }
        };

        map.acceptVisitor(visitor);
    }

    public void testIntReferenceCountMap() throws Exception
    {
        IntReferenceCountMap map = new IntReferenceCountMap();
        int val;

        map.incKeyValue(15);

        val = map.getValueForKey(100);
        assertEquals(0, val);

        map.putKeyValue(10, 5);
        val = map.getValueForKey(10);
        assertEquals(5, val);

        val = map.getValueForKey(15);
        assertEquals(1, val);

        map.decKeyValue(15);
        val = map.getValueForKey(15);
        assertEquals(0, val);

        map.incKeyValue(15, 3);
        val = map.getValueForKey(15);
        assertEquals(3, val);

        map.decKeyValuePositive(15);
        val = map.getValueForKey(15);
        assertEquals(2, val);

        map.decKeyValuePositive(15);
        val = map.getValueForKey(15);
        assertEquals(1, val);

        map.decKeyValuePositive(15);
        val = map.getValueForKey(15);
        assertEquals(0, val);

        map.decKeyValuePositive(15);
        val = map.getValueForKey(15);
        assertEquals(0, val);

        map.incKeyValue(15, 3);
        val = map.getValueForKey(15);
        assertEquals(3, val);

        map.decKeyValuePositive(15, 10);
        val = map.getValueForKey(15);
        assertEquals(0, val);

        map.incKeyValue(15, 10);
        val = map.getValueForKey(15);
        assertEquals(10, val);

        map.decKeyValue(15, 12);
        val = map.getValueForKey(15);
        assertEquals(-2, val);

        map.putKeyValue(15, 12);
        val = map.getValueForKey(15);
        assertEquals(12, val);

        IntIntVisitorIF visitor = new IntIntVisitorIF()
        {
            public int visit(int key, int value)
            {
                if (15 == key)
                {
                    assertEquals(12, value);
                }
                else if (10  == key)
                {
                    assertEquals(5, value);
                }

                return CONTINUE;
            }
        };

        map.acceptVisitor(visitor);
    }

    public void testObjectReferenceCountMap() throws Exception
    {
        ObjectReferenceCountMap map = new ObjectReferenceCountMap();
        int val;

        map.incKeyValue("15");

        val = map.getValueForKey("100");
        assertEquals(0, val);

        map.putKeyValue("10", 5);
        val = map.getValueForKey("10");
        assertEquals(5, val);

        val = map.getValueForKey("15");
        assertEquals(1, val);

        map.decKeyValue("15");
        val = map.getValueForKey("15");
        assertEquals(0, val);

        map.incKeyValue("15", 3);
        val = map.getValueForKey("15");
        assertEquals(3, val);

        map.decKeyValuePositive("15");
        val = map.getValueForKey("15");
        assertEquals(2, val);

        map.decKeyValuePositive("15");
        val = map.getValueForKey("15");
        assertEquals(1, val);

        map.decKeyValuePositive("15");
        val = map.getValueForKey("15");
        assertEquals(0, val);

        map.decKeyValuePositive("15");
        val = map.getValueForKey("15");
        assertEquals(0, val);

        map.incKeyValue("15", 3);
        val = map.getValueForKey("15");
        assertEquals(3, val);

        map.decKeyValuePositive("15", 10);
        val = map.getValueForKey("15");
        assertEquals(0, val);

        map.incKeyValue("15", 10);
        val = map.getValueForKey("15");
        assertEquals(10, val);

        map.decKeyValue("15", 12);
        val = map.getValueForKey("15");
        assertEquals(-2, val);

        map.putKeyValue("15", 12);
        val = map.getValueForKey("15");
        assertEquals(12, val);

        ObjectIntVisitorIF visitor = new ObjectIntVisitorIF()
        {
            public int visit(Object key, int value)
            {
                if ("15".equals(key))
                {
                    assertEquals(12, value);
                }
                else if ("10".equals(key))
                {
                    assertEquals(5, value);
                }

                return CONTINUE;
            }
        };

        map.acceptVisitor(visitor);
    }
}
