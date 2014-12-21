package com.cboe.domain.util.intMaps;



public class ConcurrentIntHashMapTest extends IntHashMapTest
{
    public ConcurrentIntHashMapTest(String p_str)
    {
        super(p_str);
    }

    @Override
    public void setUp()
    {
        map = new ConcurrentIntHashMap<IntPair>(2, 0.75f, 2);
    }
    
    @Override
    public void testToString()
    {
        map.put(-1, new IntPair(0, -1));
        map.put(1, new IntPair(0, 1));
        String toS = map.toString();
        
        // Note: this expected string is not a deterministic ordering.  Test failure may not be
        // indicative of a code failure - any failure needs to be analyzed.
        String exS = "{-1=(0,-1), 1=(0,1)}";
        assertEquals("toString unexpected result", exS, toS);
    }
}
