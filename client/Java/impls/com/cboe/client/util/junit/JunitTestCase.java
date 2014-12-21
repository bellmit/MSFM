package com.cboe.client.util.junit;

/**
 * JunitTestCase.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.client.util.*;
import com.cboe.idl.cmiUtil.*;
import junit.framework.*;

public class JunitTestCase extends TestCase
{
    public void testShutUpJUnitTesterComplainingAboutMissingTestMethods()
    {
        // just to shut up the junit tester about this class
    }

    public void assertNotFoundInMap(Object actual)
    {
        if (actual != null)
        {
	        failNotEquals(null, actual);
        }
    }

    public void assertNull(int actual)
    {
        if (actual != 0)
        {
	        failNotEquals(0, actual);
        }
    }

    public void assertNotFoundInMap(int actual)
    {
        if (actual != IntegerHelper.INVALID_VALUE)
        {
	        failNotEquals(IntegerHelper.INVALID_VALUE, actual);
        }
    }

    public void assertEquals(DateStruct expected, DateStruct actual)
    {
        if (!DateHelper.equals(expected, actual))
        {
            char[] expectedBuf = new char[8];
            char[] actualBuf   = new char[8];
	        failNotEquals(DateHelper.makeYYYYMMDD(expectedBuf, expected.year, expected.month, expected.day), DateHelper.makeHHMMSS(actualBuf, actual.year, actual.month, actual.day));
        }
    }

    public void assertEquals(TimeStruct expected, TimeStruct actual)
    {
        if (!DateHelper.equalsHHMMSS(expected, actual))
        {
            char[] expectedBuf = new char[8];
            char[] actualBuf   = new char[8];
	        failNotEquals(DateHelper.makeHHMMSS(expectedBuf, expected.hour, expected.minute, expected.second), DateHelper.makeHHMMSS(actualBuf, actual.hour, actual.minute, actual.second));
        }
    }

    public void assertEqualsDate(Date expected, Date actual)
    {
        Calendar expectedCal = GregorianCalendar.getInstance();
        Calendar actualCal   = GregorianCalendar.getInstance();

        expectedCal.setTime(expected);
        actualCal.setTime(actual);

        if (expectedCal.get(Calendar.YEAR)  != actualCal.get(Calendar.YEAR) ||
            expectedCal.get(Calendar.MONTH) != actualCal.get(Calendar.MONTH) ||
            expectedCal.get(Calendar.DATE)  != actualCal.get(Calendar.DATE))
        {
            expectedCal.clear(Calendar.HOUR_OF_DAY);
            expectedCal.clear(Calendar.MINUTE);
            expectedCal.clear(Calendar.SECOND);
            expectedCal.clear(Calendar.MILLISECOND);
            expectedCal.clear(Calendar.AM_PM);

            actualCal.clear(Calendar.HOUR_OF_DAY);
            actualCal.clear(Calendar.MINUTE);
            actualCal.clear(Calendar.SECOND);
            actualCal.clear(Calendar.MILLISECOND);
            actualCal.clear(Calendar.AM_PM);

            failNotEquals(expectedCal, actualCal);
        }
    }

    public void assertEqualsTime(Date expected, Date actual)
    {
        Calendar expectedCal = GregorianCalendar.getInstance();
        Calendar actualCal   = GregorianCalendar.getInstance();

        expectedCal.setTime(expected);
        actualCal.setTime(actual);

        if (expectedCal.get(Calendar.HOUR_OF_DAY) != actualCal.get(Calendar.HOUR_OF_DAY) ||
            expectedCal.get(Calendar.MINUTE)      != actualCal.get(Calendar.MINUTE) ||
            expectedCal.get(Calendar.SECOND)      != actualCal.get(Calendar.SECOND) ||
            expectedCal.get(Calendar.MILLISECOND) != actualCal.get(Calendar.MILLISECOND))
        {
            expectedCal.clear(Calendar.YEAR);
            expectedCal.clear(Calendar.MONTH);
            expectedCal.clear(Calendar.DATE);

            actualCal.clear(Calendar.YEAR);
            actualCal.clear(Calendar.MONTH);
            actualCal.clear(Calendar.DATE);

	        failNotEquals(expectedCal, actualCal);
        }
    }

    public void assertEquals(String expected, StringBuffer actual)
    {
        String act = actual.toString();

        if (!expected.equals(act))
        {
	        failNotEquals(expected, act);
        }
    }

    public void assertEqualsOffset(char[] expected, char[] actual, int expected_offset)
    {
        if (expected.length != actual.length - expected_offset)
        {
		    failNotEquals(expected, actual);
        }

        for (int i = 0; i < expected.length; i++)
        {
            if (expected[i] != actual[expected_offset + i])
            {
    		    failNotEquals(expected, actual);
            }
        }
    }

    public void assertEquals(char[] expected, char[] actual)
    {
        if (!Arrays.equals(expected, actual))
        {
		    failNotEquals(expected, actual);
        }
    }

    public void assertEquals(char[] expected, char[] actual, int offset, int length)
    {
        if (expected == actual)
        {
            return;
        }

        if (offset + length > expected.length || offset + length > actual.length)
        {
		    failNotEquals(expected, actual);
        }

        length += offset;

        for (int i = offset; i < length; i++)
        {
            if (expected[i] != actual[i])
            {
    		    failNotEquals(expected, actual);
            }
        }
    }

    public void assertEquals(Object[] expected, Object[] actual)
    {
        if (!Arrays.equals(expected, actual))
        {
		    failNotEquals(expected, actual);
        }
    }

    public void assertEquals(Object[] expected, Object[] actual, int offset, int length)
    {
        if (expected == actual)
        {
            return;
        }

        if (offset + length > expected.length || offset + length > actual.length)
        {
		    failNotEquals(expected, actual);
        }

        length += offset;

        for (int i = offset; i < length; i++)
        {
            if (expected[i] != actual[i])
            {
    		    failNotEquals(expected, actual);
            }
        }
    }

    public void assertEquals(int[] expected, int[] actual)
    {
        if (!Arrays.equals(expected, actual))
        {
		    failNotEquals(expected, actual);
        }
    }

    public void assertEquals(int[] expected, int[] actual, int offset, int length)
    {
        if (expected == actual)
        {
            return;
        }

        if (offset + length > expected.length || offset + length > actual.length)
        {
		    failNotEquals(expected, actual);
        }

        length += offset;

        for (int i = offset; i < length; i++)
        {
            if (expected[i] != actual[i])
            {
    		    failNotEquals(expected, actual);
            }
        }
    }

    public void assertEquals(long[] expected, long[] actual)
    {
        if (!Arrays.equals(expected, actual))
        {
		    failNotEquals(expected, actual);
        }
    }

    public void assertEquals(long[] expected, long[] actual, int offset, int length)
    {
        if (expected == actual)
        {
            return;
        }

        if (offset + length > expected.length || offset + length > actual.length)
        {
		    failNotEquals(expected, actual);
        }

        length += offset;

        for (int i = offset; i < length; i++)
        {
            if (expected[i] != actual[i])
            {
    		    failNotEquals(expected, actual);
            }
        }
    }

    protected static void failNotEquals(int expected, int actual)
    {
        fail("expected:<" + expected + "> but was:<" + actual + ">");
    }

    protected static void failNotEquals(long expected, long actual)
    {
        fail("expected:<" + expected + "> but was:<" + actual + ">");
    }

    protected static void failNotEquals(Object expected, Object actual)
    {
        fail("expected:<" + expected + "> but was:<" + actual + ">");
    }

	protected static void failNotEquals(String expected, String actual)
    {
		fail("expected:<" + expected + "> but was:<" + actual + ">");
	}

	protected static void failNotEquals(Object[] expected, Object[] actual)
    {
		fail("expected:<" + expected + "::" + expected.length + "> but was:<" + actual + "::" + actual.length + ">");
	}

	protected static void failNotEquals(int[] expected, int[] actual)
    {
		fail("expected:<" + expected + "::" + expected.length + "> but was:<" + actual + "::" + actual.length + ">");
	}

	protected static void failNotEquals(long[] expected, long[] actual)
    {
		fail("expected:<" + expected + "::" + expected.length + "> but was:<" + actual + "::" + actual.length + ">");
	}

	protected static void failNotEquals(char[] expected, char[] actual)
    {
		fail("expected:<" + new String(expected) + "::" + expected.length + "> but was:<" + new String(actual) + "::" + actual.length + ">");
	}

	protected static void failNotEquals(char[] expected, char[] actual, int expected_offset)
    {
		fail("expected:<" + new String(expected, expected_offset, expected.length - expected_offset) + "::" + (expected.length - expected_offset) + "> but was:<" + new String(actual) + "::" + actual.length + ">");
	}
}
