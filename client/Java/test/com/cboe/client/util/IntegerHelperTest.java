package com.cboe.client.util;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;  // annotation

public class IntegerHelperTest
{
    static final boolean POSITIVE = false;
    static final boolean NEGATIVE = true;

    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(IntegerHelperTest.class);
    }

    @Test public void createIdentical()
    {
        Integer a = IntegerHelper.createInteger(1);
        Integer b = IntegerHelper.createInteger(1);
        assertSame(a, b);
        assertEquals(a, b);
    }

    @Test public void createDifferent()
    {
        Integer a = IntegerHelper.createInteger(-500);
        Integer b = IntegerHelper.createInteger(-500);
        assertNotSame(a, b);
        assertEquals(a, b);
    }

    @Test public void testInc()
    {
        Integer one = IntegerHelper.createInteger(1);
        Integer two = IntegerHelper.createInteger(2);
        assertNotSame(one, two);
        assertFalse(one.equals(two));
        Integer x = IntegerHelper.incInteger(one);
        assertSame(two, x);
        assertEquals(two, x);
        assertNotSame(one, x);

        Integer mD = IntegerHelper.createInteger(-500);
        Integer mDI = IntegerHelper.createInteger(-501);
        x = IntegerHelper.incInteger(mDI);
        assertNotSame(mD, x);
        assertEquals(mD, x);
    }

    @Test public void testDec()
    {
        Integer four = IntegerHelper.createInteger(4);
        Integer five = IntegerHelper.createInteger(5);
        assertNotSame(four, five);
        assertFalse(four.equals(five));
        Integer x = IntegerHelper.decInteger(five);
        assertSame(four, x);
        assertEquals(four, x);
        assertNotSame(five, x);

        Integer mD = IntegerHelper.createInteger(-500);
        Integer DI = IntegerHelper.createInteger(-501);
        x = IntegerHelper.decInteger(mD);
        assertNotSame(DI, x);
        assertEquals(DI, x);
    }

    @Test public void testParseByte()
    {
        byte nullByte[] = null;
        assertTrue(IntegerHelper.INVALID_VALUE == IntegerHelper.parseInt(nullByte, 0, 0));

        byte emptyByte[] = new byte[0];
        assertTrue(IntegerHelper.INVALID_VALUE == IntegerHelper.parseInt(emptyByte, 0, emptyByte.length));

        byte oneTwoThree[] = { '1', '2', '3' };
        assertEquals(123, IntegerHelper.parseInt(oneTwoThree, 0, oneTwoThree.length));
        assertEquals(1,  IntegerHelper.parseInt(oneTwoThree, 0, 1));
        assertEquals(2, IntegerHelper.parseInt(oneTwoThree, 1, 1));
        assertEquals(3, IntegerHelper.parseInt(oneTwoThree, 2, 1));
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(oneTwoThree, 3, 1));
        assertEquals(12, IntegerHelper.parseInt(oneTwoThree, 0, 2));
        assertEquals(23, IntegerHelper.parseInt(oneTwoThree, 1, 2));
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(oneTwoThree, 2, 2));

        byte minusTwo[] = { '-', '2' };
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(minusTwo, 0, minusTwo.length));

        byte onePointFive[] = { '1', '.', '5' };
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(onePointFive, 0, onePointFive.length));

        byte tooBig[] = { '2', '1', '4', '7', '4', '8', '3', '6', '4', '9' };
        assertEquals(-2147483647, IntegerHelper.parseInt(tooBig, 0, tooBig.length));
    }

    @Test public void testParseChar()
    {
        char nullChar[] = null;
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(nullChar, 0, 0));

        char emptyChar[] = new char[0];
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(emptyChar, 0, emptyChar.length));

        char oneTwoThree[] = { '1', '2', '3' };
        assertEquals(123, IntegerHelper.parseInt(oneTwoThree, 0, oneTwoThree.length));
        assertEquals(1, IntegerHelper.parseInt(oneTwoThree, 0, 1));
        assertEquals(2, IntegerHelper.parseInt(oneTwoThree, 1, 1));
        assertEquals(3, IntegerHelper.parseInt(oneTwoThree, 2, 1));
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(oneTwoThree, 3, 1));
        assertEquals(12, IntegerHelper.parseInt(oneTwoThree, 0, 2));
        assertEquals(23, IntegerHelper.parseInt(oneTwoThree, 1, 2));
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(oneTwoThree, 2, 2));

        char minusTwo[] = { '-', '2' };
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(minusTwo, 0, minusTwo.length));

        char onePointFive[] = { '1', '.', '5' };
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(onePointFive, 0, onePointFive.length));

        char tooBig[] = { '2', '1', '4', '7', '4', '8', '3', '6', '4', '9' };
        assertEquals(-2147483647, IntegerHelper.parseInt(tooBig, 0, tooBig.length));
    }

    @Test public void testParseString()
    {
        String nullString = null;
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(nullString));

        String emptyString = "";
        assertEquals(0, IntegerHelper.parseInt(emptyString));

        String oneTwoThree = "123";
        assertEquals(123, IntegerHelper.parseInt(oneTwoThree));

        String minusTwo = "-2";
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(minusTwo));

        String onePointFive = "1.5";
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(onePointFive));

        String tooBig = "2147483649";
        assertEquals(-2147483647, IntegerHelper.parseInt(tooBig));
    }

    @Test public void testParseStringOffset()
    {
        String oneTwoThree = "123";
        assertEquals(123, IntegerHelper.parseInt(oneTwoThree, 0));
        assertEquals(23, IntegerHelper.parseInt(oneTwoThree, 1));
        assertEquals(3, IntegerHelper.parseInt(oneTwoThree, 2));
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(oneTwoThree, 3));
    }

    @Test public void testParseStringOffsetLength()
    {
        String oneTwoThree = "123";
        assertEquals(123, IntegerHelper.parseInt(oneTwoThree, 0, oneTwoThree.length()));
        assertEquals(1, IntegerHelper.parseInt(oneTwoThree, 0, 1));
        assertEquals(2, IntegerHelper.parseInt(oneTwoThree, 1, 1));
        assertEquals(3, IntegerHelper.parseInt(oneTwoThree, 2, 1));
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(oneTwoThree, 3, 1));
        assertEquals(12, IntegerHelper.parseInt(oneTwoThree, 0, 2));
        assertEquals(23, IntegerHelper.parseInt(oneTwoThree, 1, 2));
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInt(oneTwoThree, 2, 2));
    }

    private void minusOne(int ints[])
    {
        for (int i = 0; i < ints.length; ++i)
        {
            ints[i] = -1;
        }
    }

    @Test public void testParseInts()
    {
        String three = "12 23 34";
        String badMiddle = "10 -6 42";
        String badEnd = "5 9 18.3";

        int oneInt[] = new int[1];
        int threeInt[] = new int[3];
        int fourInt[] = new int[4];

        minusOne(oneInt);
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInts(three, oneInt));
        minusOne(threeInt);
        assertEquals(3, IntegerHelper.parseInts(three, threeInt));
        assertEquals(23, threeInt[1]);
        assertEquals(12, threeInt[0]);
        assertEquals(34, threeInt[2]);

        minusOne(fourInt);
        assertEquals(3, IntegerHelper.parseInts(three, fourInt));
        assertEquals(12, fourInt[0]);
        assertEquals(23, fourInt[1]);
        assertEquals(34, fourInt[2]);
        assertEquals(0, fourInt[3]);

        minusOne(threeInt);
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInts(badMiddle, threeInt));
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.parseInts(badEnd, threeInt));
    }

    @Test public void testZeroIfNullInteger()
    {
        Integer five = 5;
        Integer zero = 0;
        Integer nullInteger = null;

        assertEquals(5, IntegerHelper.zeroIfNull(five));
        assertEquals(0, IntegerHelper.zeroIfNull(zero));
        assertEquals(0, IntegerHelper.zeroIfNull(nullInteger));
    }

    @Test public void testZeroIfNullString()
    {
        String noString = null;

        assertEquals(5, IntegerHelper.zeroIfNull("5"));
        assertEquals(0, IntegerHelper.zeroIfNull("0"));
        assertEquals(0, IntegerHelper.zeroIfNull(noString));
        assertEquals(IntegerHelper.INVALID_VALUE, IntegerHelper.zeroIfNull("-5"));
    }

    @Test public void testCountDigits()
    {
        assertEquals(1, IntegerHelper.countDigits(3));
        assertEquals(2, IntegerHelper.countDigits(-3));
        assertEquals(2, IntegerHelper.countDigits(21));
        assertEquals(3, IntegerHelper.countDigits(-21));
        assertEquals(3, IntegerHelper.countDigits(512));
        assertEquals(4, IntegerHelper.countDigits(-512));
        assertEquals(4, IntegerHelper.countDigits(1789));
        assertEquals(5, IntegerHelper.countDigits(-1789));
        assertEquals(5, IntegerHelper.countDigits(16501));
        assertEquals(6, IntegerHelper.countDigits(-16501));
        assertEquals(6, IntegerHelper.countDigits(587423));
        assertEquals(7, IntegerHelper.countDigits(-587423));
        assertEquals(7, IntegerHelper.countDigits(7865800));
        assertEquals(8, IntegerHelper.countDigits(-7865800));
        assertEquals(8, IntegerHelper.countDigits(27182818));
        assertEquals(9, IntegerHelper.countDigits(-27182818));
        assertEquals(9, IntegerHelper.countDigits(314159265));
        assertEquals(10, IntegerHelper.countDigits(-314159265));
        assertEquals(10, IntegerHelper.countDigits(Integer.MAX_VALUE));
        assertEquals(11, IntegerHelper.countDigits(Integer.MIN_VALUE));
    }

    @Test public void testCountDigitsSign()
    {
        assertEquals(1, IntegerHelper.countDigits(7, POSITIVE));
        assertEquals(2, IntegerHelper.countDigits(7, NEGATIVE));
        assertEquals(2, IntegerHelper.countDigits(42, POSITIVE));
        assertEquals(3, IntegerHelper.countDigits(42, NEGATIVE));
        assertEquals(3, IntegerHelper.countDigits(999, POSITIVE));
        assertEquals(4, IntegerHelper.countDigits(999, NEGATIVE));
        assertEquals(4, IntegerHelper.countDigits(1000, POSITIVE));
        assertEquals(5, IntegerHelper.countDigits(1000, NEGATIVE));
        assertEquals(5, IntegerHelper.countDigits(94114, POSITIVE));
        assertEquals(6, IntegerHelper.countDigits(94114, NEGATIVE));
        assertEquals(6, IntegerHelper.countDigits(123456, POSITIVE));
        assertEquals(7, IntegerHelper.countDigits(123456, NEGATIVE));
        assertEquals(7, IntegerHelper.countDigits(3463478, POSITIVE));
        assertEquals(8, IntegerHelper.countDigits(3463478, NEGATIVE));
        assertEquals(8, IntegerHelper.countDigits(17762010, POSITIVE));
        assertEquals(9, IntegerHelper.countDigits(17762010, NEGATIVE));
        assertEquals(9, IntegerHelper.countDigits(816357492, POSITIVE));
        assertEquals(10, IntegerHelper.countDigits(816357492, NEGATIVE));
        assertEquals(10, IntegerHelper.countDigits(2147483646, POSITIVE));
        assertEquals(11, IntegerHelper.countDigits(2147483646, NEGATIVE));
    }

    @Test public void testHigherPowerOf2()
    {
        assertEquals(1, IntegerHelper.higherPowerOf2(0));
        assertEquals(1, IntegerHelper.higherPowerOf2(1));
        assertEquals(2, IntegerHelper.higherPowerOf2(2));
        assertEquals(4, IntegerHelper.higherPowerOf2(3));
        assertEquals(16777216, IntegerHelper.higherPowerOf2(9000000));
        // (todo) note: fails for numbers greater than Integer.MAX_VALUE
    }
}
