package com.cboe.client.util.junit;

/**
 * junitMap.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.collections.*;

public class junitMap extends JunitTestCase
{
    static final int[]    iNumbers = new int[10000];
    static final int[]    rNumbers = new int[10000];
    static final String[] sNumbers = new String[10000];
    static final int[]    bad_iNumbers = new int[10000];
    static final String[] bad_sNumbers = new String[10000];
    static final int[]    bad_rNumbers = new int[10000];

    static
    {
        for (int i = 0; i < iNumbers.length; i++)
        {
            iNumbers[i] = i;
            rNumbers[i] = i + 1000000;
            sNumbers[i] = Integer.toString(i);
            bad_iNumbers[i] = i + 5000000;
            bad_rNumbers[i] = i + 6000000;
            bad_sNumbers[i] = Integer.toString(i + 5000000);
        }
    }

    public void testIntObjectMap() throws Exception
    {
        IntObjectMap map = new IntObjectMap();
        int i;

        int[]    goodKeyMap   = iNumbers;
        String[] goodValueMap = sNumbers;
        int[]    badKeyMap    = bad_iNumbers;
        String[] badValueMap  = bad_sNumbers;

        assertEquals(0, map.size());

        map.putKeyValue(goodKeyMap[0], goodValueMap[0]);
        map.putKeyValue(goodKeyMap[8], goodValueMap[8]);
        map.putKeyValue(goodKeyMap[12], goodValueMap[12]);
        map.putKeyValue(goodKeyMap[4], goodValueMap[4]);
        map.putKeyValue(goodKeyMap[20], goodValueMap[20]);
        map.putKeyValue(goodKeyMap[16], goodValueMap[16]);

        assertEquals(goodValueMap[0], map.getValueForKey(goodKeyMap[0]));
        assertEquals(goodValueMap[8], map.getValueForKey(goodKeyMap[8]));
        assertEquals(goodValueMap[12], map.getValueForKey(goodKeyMap[12]));
        assertEquals(goodValueMap[4], map.getValueForKey(goodKeyMap[4]));
        assertEquals(goodValueMap[20], map.getValueForKey(goodKeyMap[20]));
        assertEquals(goodValueMap[16], map.getValueForKey(goodKeyMap[16]));

        assertEquals(6, map.size());

        map.clear();

        for (i = 0; i < goodValueMap.length; i++)
        {
            assertNotFoundInMap(map.getValueForKey(goodKeyMap[i]));
            map.putKeyValue(goodKeyMap[i], goodValueMap[i]);
        }

        assertEquals(goodValueMap.length, map.size());

        for (i = 0; i < goodValueMap.length; i++)
        {
            assertNotFoundInMap(map.getValueForKey(badKeyMap[i]));
            assertEquals(goodValueMap[i], map.getValueForKey(goodKeyMap[i]));
        }

        map.removeKey(goodKeyMap[2]);

        assertEquals(goodValueMap.length - 1, map.size());

        for (i = 0; i < 10; i++)
        {
            map.putKeyValue(goodKeyMap[2], goodValueMap[2]);
        }

        assertEquals(goodValueMap.length, map.size());
        assertEquals(goodValueMap[2], map.getValueForKey(goodKeyMap[2]));

        for (i = 0; i < goodValueMap.length; i++)
        {
            assertEquals(goodValueMap[i], map.getValueForKey(goodKeyMap[i]));
            assertTrue(map.containsKey(goodKeyMap[i]));
            assertTrue(map.containsValue(goodValueMap[i]));
            assertFalse(map.containsKey(badKeyMap[i]));
            assertFalse(map.containsValue(badValueMap[i]));
        }

        assertFalse(map.isEmpty());

        map.clear();

        assertTrue(map.isEmpty());

        assertNotFoundInMap(map.getValueForKey(goodKeyMap[2]));

        assertEquals(0, map.size());
    }

    public void testIntIntMap() throws Exception
    {
        IntIntMap map = new IntIntMap();
        int i;
        int[] goodKeyMap   = iNumbers;
        int[] goodValueMap = rNumbers;
        int[] badKeyMap    = bad_iNumbers;
        int[] badValueMap  = bad_rNumbers;

        assertEquals(0, map.size());

        for (i = 0; i < goodValueMap.length; i++)
        {
            assertNotFoundInMap(map.getValueForKey(goodKeyMap[i]));
            map.putKeyValue(goodKeyMap[i], goodValueMap[i]);
        }

        assertEquals(goodValueMap.length, map.size());

        for (i = 0; i < goodValueMap.length; i++)
        {
            assertNotFoundInMap(map.getValueForKey(badKeyMap[i]));
            assertEquals(goodValueMap[i], map.getValueForKey(goodKeyMap[i]));
        }

        map.removeKey(goodKeyMap[2]);

        assertEquals(goodValueMap.length - 1, map.size());

        for (i = 0; i < 10; i++)
        {
            map.putKeyValue(goodKeyMap[2], goodValueMap[2]);
        }

        assertEquals(goodValueMap.length, map.size());
        assertEquals(goodValueMap[2], map.getValueForKey(goodKeyMap[2]));

        for (i = 0; i < goodValueMap.length; i++)
        {
            assertEquals(goodValueMap[i], map.getValueForKey(goodKeyMap[i]));
            assertTrue(map.containsKey(goodKeyMap[i]));
            assertTrue(map.containsValue(goodValueMap[i]));
            assertFalse(map.containsKey(badKeyMap[i]));
            assertFalse(map.containsValue(badValueMap[i]));
        }

        assertFalse(map.isEmpty());

        map.clear();

        assertTrue(map.isEmpty());

        assertNotFoundInMap(map.getValueForKey(goodKeyMap[2]));

        assertEquals(0, map.size());
    }
}
