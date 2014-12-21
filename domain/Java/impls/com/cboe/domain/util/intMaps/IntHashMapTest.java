package com.cboe.domain.util.intMaps;

import java.util.Collection;
import java.util.Set;

import junit.framework.TestCase;

/**
 * test basic API of the IntHashMap.  note that these exact tests are also used
 * to test ConcurrentIntHashMap
 * 
 * @author Steve
 */
public class IntHashMapTest extends TestCase
{
    public static class IntPair
    {
        int v1;
        int v2;
        public IntPair(int v1, int v2)
        {
            this.v1 = v1;
            this.v2 = v2;
        }
        public boolean equals(Object rhs)
        {
            return (rhs instanceof IntPair) && ((IntPair)rhs).v1 == v1 && ((IntPair)rhs).v2 == v2;
        }
        public String toString()
        {
            return "(" + v1 + "," + v2 + ")";
        }
    }
    
    public IntHashMapTest(String p_str)
    {
        super(p_str);
    }

    IntMap<IntPair> map;
    
    @Override
    public void setUp()
    {
        map = new IntHashMap<IntPair>();
    }
    
    public void testPut()
    {
        // basic map behavior:
        int[] ex = { -1, 0, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, 255, 256, 1024, 111222333 };
        for (int i = 0; i < ex.length; i++)
        {
            assertTrue("Map cannot contain key "+ex[i]+" yet", !map.containsKey(ex[i]));
            map.put(ex[i], new IntPair(i, ex[i]));
            assertTrue("Map must contain key "+ex[i]+" now", map.containsKey(ex[i]));
            assertEquals("Size when i="+i, i+1, map.size());
        }
        for (int i = 0; i < ex.length; i++)
        {
            assertEquals("Bad value found for ex["+i+"]="+ex[i], new IntPair(i, ex[i]), map.get(ex[i]));
        }
    }
    
    public void testReput()
    {
        IntPair o1 = map.put(1, new IntPair(0,1));
        assertNull("o1", o1);
        IntPair o2 = map.put(1, new IntPair(1,1));  // (should return previous value)
        assertEquals("o2", new IntPair(0,1), o2);
        assertEquals("size after reput", 1, map.size());
    }
        
    public void testToString()
    {
        map.put(-1, new IntPair(0, -1));
        map.put(1, new IntPair(0, 1));
        String toS = map.toString();
        
        // Note: this expected string is not a deterministic ordering.  Test failure may not be
        // indicative of a code failure - any failure needs to be analyzed.
        String exS = "{1=(0,1), -1=(0,-1)}";
        assertEquals("toString unexpected result", exS, toS);
    }

    public void testContainsValue()
    {
        map.put(0, new IntPair(0, 0));
        map.put(1, new IntPair(0, 1));
        map.put(1, new IntPair(1, 1)); // replace 0,1 with 1,1
        assertTrue("Contains(0,0)", map.containsValue(new IntPair(0,0)));
        assertTrue("Contains(1,1)", map.containsValue(new IntPair(1,1)));
        assertTrue("!Contains(0,1)", !map.containsValue(new IntPair(0,1)));
        assertTrue("!Contains(9,9)", !map.containsValue(new IntPair(9,9)));
    }

    public void testResize()
    {
        for (int i=0; i < 1000; i++)
        {
            map.put(i, new IntPair(i,i));
        }
        for (int i=0; i < 1000; i++)
        {
            assertEquals("fail for i="+i, new IntPair(i,i), map.get(i));
        }
    }

    public void testPutAll()
    {
        map.put(0, new IntPair(0, 0)); // this entry should be preserved
        map.put(1, new IntPair(0, 1)); // this entry should be replaced
        
        IntHashMap<IntPair> frMap = new IntHashMap<IntPair>();
        frMap.put(1, new IntPair(1, 1)); // replace an old value
        frMap.put(2, new IntPair(1, 2)); // add new value
        
        map.putAll(frMap);
        
        assertEquals("Map size after put", 3, map.size());
        assertTrue("contains key 0", map.containsKey(0));
        assertTrue("contains key 1", map.containsKey(1));
        assertTrue("contains key 2", map.containsKey(2));
        assertTrue("contains value 0,0", map.containsValue(new IntPair(0,0)));
        assertTrue("contains value 1,1", map.containsValue(new IntPair(1,1)));
        assertTrue("contains value 1,2", map.containsValue(new IntPair(1,2)));
        assertTrue("!contains value 0,1", !map.containsValue(new IntPair(0,1)));
        assertEquals("get 0", new IntPair(0,0), map.get(0));
        assertEquals("get 1", new IntPair(1,1), map.get(1));
        assertEquals("get 2", new IntPair(1,2), map.get(2));
    }

    public void testRemove()
    {
        assertNull("remove from empty map", map.remove(1));
        map.put(2, new IntPair(0,2));
        assertNull("remove non-existent key from map", map.remove(1));
        assertEquals("removed key=2", new IntPair(0,2), map.remove(2));
    }

    public void testClear()
    {
        map.clear();
        map.put(1, new IntPair(1,1));
        assertEquals("size 1", 1, map.size());
        assertTrue("!empty", !map.isEmpty());
        map.clear();
        assertTrue("key=1 is gone", !map.containsKey(1));
        assertTrue("val=(1,1) is gone", !map.containsValue(new IntPair(1,1)));
        assertNull("get(i) is null", map.get(1));
        assertEquals("Map size()==0", 0, map.size());
        assertTrue("empty", map.isEmpty());
    }

    public void testKeySet()
    {
        assertTrue("empty keyset", map.keySet().isEmpty());

        final int num = 4;
        for (int i = 0; i < num; i++)
        {
            map.put(i, new IntPair(0,i));
        }

        Set<Integer> keys = map.keySet();
        assertEquals("size",num,keys.size());
        for (int i = 0; i < num; i++)
        {
            assertTrue("list["+i+"]", keys.contains(new Integer(i)));
        }
    }

    public void testValues()
    {
        assertTrue("empty keyset", map.keySet().isEmpty());
        final int num = 4;
        for (int i = 0; i < num; i++)
        {
            map.put(i, new IntPair(0,i));
        }
        
        Collection<IntPair> vals = map.values();
        assertEquals("size",num,vals.size());
        for (int i = 0; i < num; i++)
        {
            assertTrue("vals contains pair["+i+"]", vals.contains(new IntPair(0,i)));
        }
    }
}
