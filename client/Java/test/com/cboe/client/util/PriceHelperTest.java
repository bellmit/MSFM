package com.cboe.client.util;

import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiUtil.PriceStruct;
import java.io.StringWriter;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;      // annotation

public class PriceHelperTest
{
    private static final int ONE_CENT = 10000000;
    private static final int TWENTY_FIVE_CENTS = 250000000;
    private static final int FIFTY_CENTS = 500000000;
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(PriceHelperTest.class);
    }

    @Test public void testCreate()
    {
        PriceStruct p = PriceHelper.createNoPriceStruct();
        assertEquals(PriceTypes.NO_PRICE, p.type);
        assertEquals(0, p.whole);
        assertEquals(0, p.fraction);

        p = PriceHelper.createMarketPriceStruct();
        assertEquals(PriceTypes.MARKET, p.type);
        assertEquals(0, p.whole);
        assertEquals(0, p.fraction);

        p = PriceHelper.createPriceStruct(12);
        assertEquals(PriceTypes.VALUED, p.type);
        assertEquals(12, p.whole);
        assertEquals(0, p.fraction);

        p = PriceHelper.createPriceStruct(40, 2);
        assertEquals(PriceTypes.VALUED, p.type);
        assertEquals(40, p.whole);
        assertEquals(2, p.fraction);

        p = PriceHelper.createPriceStructFromDollarsAndCents(3, 50);
        assertEquals(PriceTypes.VALUED, p.type);
        assertEquals(3, p.whole);
        assertEquals(FIFTY_CENTS, p.fraction);

        p = PriceHelper.createPriceStruct(2.25);
        assertEquals(PriceTypes.VALUED, p.type);
        assertEquals(2, p.whole);
        assertEquals(TWENTY_FIVE_CENTS, p.fraction);

        p = PriceHelper.createPriceStruct("2");
        assertEquals(PriceTypes.VALUED, p.type);
        assertEquals(2, p.whole);
        assertEquals(0, p.fraction);

        p = PriceHelper.createPriceStruct("4.01");
        assertEquals(PriceTypes.VALUED, p.type);
        assertEquals(4, p.whole);
        assertEquals(ONE_CENT, p.fraction);

        byte b[] = { '1', '7', '.', '5', '0' };
        p = PriceHelper.createPriceStruct(b, 0, 0);
        assertEquals(PriceTypes.MARKET, p.type);
        assertEquals(0, p.whole);
        assertEquals(0, p.fraction);

        p = PriceHelper.createPriceStruct(b, 0, b.length);
        assertEquals(PriceTypes.VALUED, p.type);
        assertEquals(17, p.whole);
        assertEquals(FIFTY_CENTS, p.fraction);

        byte x[] = { '1', 'x' };
        p = PriceHelper.createPriceStruct(x, 0, x.length);
        assertNull(p);

        char c[] = { '3', '2', '.', '0', '1' };
        p = PriceHelper.createPriceStruct(c, 0, 0);
        assertEquals(PriceTypes.MARKET, p.type);
        assertEquals(0, p.whole);
        assertEquals(0, p.fraction);

        p = PriceHelper.createPriceStruct(c, 0, c.length);
        assertEquals(PriceTypes.VALUED, p.type);
        assertEquals(32, p.whole);
        assertEquals(ONE_CENT, p.fraction);

        byte y[] = { '1', 'x' };
        p = PriceHelper.createPriceStruct(y, 0, y.length);
        assertNull(p);
    }

    @Test public void testCreateDouble()
    {
        PriceStruct p = new PriceStruct(PriceTypes.VALUED, 4, FIFTY_CENTS);
        double d = PriceHelper.createDouble(p);
        assertEquals(4.50, d, 0.0);

        p.fraction = ONE_CENT;
        d = PriceHelper.createDouble(p);
        assertEquals(4.01, d, 0.005);
    }

    @Test public void testEquals()
    {
        PriceStruct a = new PriceStruct(PriceTypes.MARKET, 0, 0);
        PriceStruct b = new PriceStruct(PriceTypes.MARKET, 0, 0);
        assertTrue(PriceHelper.equals(a,b));
        b.type = PriceTypes.VALUED;
        assertFalse(PriceHelper.equals(a,b));
        b.whole = 7;
        a.type = PriceTypes.VALUED;
        a.whole = 7;
        assertTrue(PriceHelper.equals(a,b));
        a.fraction = ONE_CENT;
        assertFalse(PriceHelper.equals(a,b));
    }

    @Test public void testToString()
    {
        PriceStruct p = new PriceStruct(PriceTypes.NO_PRICE, 0, 0);
        assertEquals("0", PriceHelper.toString(p));

        p.type = PriceTypes.VALUED;
        p.whole = 3;
        assertEquals("3", PriceHelper.toString(p));
        p.fraction = ONE_CENT;
        assertEquals("3.01", PriceHelper.toString(p));
    }

    @Test public void testAppendPriceStruct() throws Exception
    {
        PriceStruct p = new PriceStruct(PriceTypes.NO_PRICE, 0, 0);
        StringWriter sw = new StringWriter();
        PriceHelper.appendPriceStruct(sw, p);
        assertEquals("0", sw.toString());
        FastCharacterWriter fcw = new FastCharacterWriter();
        PriceHelper.appendPriceStruct(fcw, p);
        assertEquals("0", fcw.toString());

        p.type = PriceTypes.VALUED;
        p.whole = 2;
        sw = new StringWriter();
        PriceHelper.appendPriceStruct(sw, p);
        assertEquals("2", sw.toString());
        fcw = new FastCharacterWriter();
        PriceHelper.appendPriceStruct(fcw, p);
        assertEquals("2", fcw.toString());

        p.fraction = ONE_CENT;
        sw = new StringWriter();
        PriceHelper.appendPriceStruct(sw, p);
        assertEquals("2.01", sw.toString());
        fcw = new FastCharacterWriter();
        PriceHelper.appendPriceStruct(fcw, p);
        assertEquals("2.01", fcw.toString());
    }

}
