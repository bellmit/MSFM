
package com.cboe.client.util;

import junit.framework.JUnit4TestAdapter;
import org.junit.*;
import static org.junit.Assert.*;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.DataValidationException;

/**
 * Created by IntelliJ IDEA.
 * @author Peng Li
 * Date: Jul 20, 2009
 * Time: 10:52:43 AM
 */

public class StrategyLegsWrapperTest {
    
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(StrategyLegsWrapperTest.class);
    }

    private StrategyLegsWrapper theWrapper;
    private StrategyLegsWrapper theSessionWrapper;
    private StrategyLegsWrapper theStringWrapper;

    @Before
    public void setUp()  throws NotFoundException, DataValidationException{
        StrategyLegStruct myLegs[] = new StrategyLegStruct[2];
        myLegs[0] = new StrategyLegStruct(611463667, 1, Sides.BUY);
        myLegs[1] = new StrategyLegStruct(611463668, 2, Sides.BUY);

        String legStrings[] = { "611463668,S,2", "611463667,S,1" };

        // Originally this test created mySessionLegs with sizes 4 and 2.
        // But StrategyLegsWrapper looks up the actual type of the product
        // key (special handling for Equity products) and that fails in a unit
        // test environment; as a result it can't reduce 4 and 2 to 2 and 1.
        SessionStrategyLegStruct mySessionLegs[] = new SessionStrategyLegStruct[2];
        mySessionLegs[0] = new SessionStrategyLegStruct("W_MAIN", 611463668, 2, Sides.SELL);
        mySessionLegs[1] = new SessionStrategyLegStruct("W_MAIN", 611463667, 1, Sides.SELL);

        theWrapper = new StrategyLegsWrapper(myLegs);
        theSessionWrapper = new StrategyLegsWrapper(mySessionLegs);
        theStringWrapper = new StrategyLegsWrapper(legStrings);
    }

    @Test
    public void testEquals(){
        assertTrue(theWrapper.equals(theSessionWrapper));
        assertTrue(theWrapper.equals(theStringWrapper));
        assertFalse(theWrapper.equals(new Object()));
    }

    @Test
    public void testHashcode(){
        assertEquals(theWrapper.hashCode(), theSessionWrapper.hashCode());
        assertEquals(theWrapper.hashCode(), theStringWrapper.hashCode());
    }

    @Test
    public void testIsValidStrategy()
    {
        assertTrue(theWrapper.isValidStrategy());
    }

    @Test
    public void testToString()
    {
        assertEquals("611463667S1;611463668S2;", theStringWrapper.toString());
    }
}