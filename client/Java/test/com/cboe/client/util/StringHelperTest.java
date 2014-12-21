package com.cboe.client.util;

import com.cboe.idl.cmiUtil.PriceStruct;
import java.io.StringWriter;
import java.util.Arrays;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;      // annotation

public class StringHelperTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(StringHelperTest.class);
    }

    private static final String INT_MAX = Integer.toString(Integer.MAX_VALUE);
    private static final String INT_MIN = Integer.toString(Integer.MIN_VALUE);
    private static final int FIFTY_CENTS = 500000000;
    private static final int TEN_CENTS   = 100000000;
    private static final int ONE_CENT    =  10000000;

    @Test public void testPopulateBufFromInt()
    {
        char buf[];
        buf = new char[11];

        int start = StringHelper.populateBufFromInt(buf, 0, false);
        assertEquals(10, start);
        assertEquals('0', buf[10]);

        start = StringHelper.populateBufFromInt(buf, 42, false);
        assertEquals(9, start);
        assertEquals("42", new String(buf, start,buf.length-start));

        start = StringHelper.populateBufFromInt(buf, 0, true);
        assertEquals(9, start);
        assertEquals("-0", new String(buf, start,buf.length-start));

        start = StringHelper.populateBufFromInt(buf, Integer.MAX_VALUE, false);
        assertEquals(1, start);
        assertEquals(INT_MAX, new String(buf, start, buf.length-start));

        start = StringHelper.populateBufFromInt(buf, 1000000000, true);
        assertEquals(0, start);
        assertEquals("-1000000000", new String(buf));

        buf = new char[22];

        start = StringHelper.populateBufFromInt(buf, 0, 0, false);
        assertEquals(12, start);
        assertEquals("0.00000000", new String(buf, start, buf.length-start));

        start = StringHelper.populateBufFromInt(buf, 4, ONE_CENT, true);
        assertEquals(17, start);
        assertEquals("-4.01", new String(buf, start, buf.length-start));

        start = StringHelper.populateBufFromInt(
                buf, 16777216, -FIFTY_CENTS, true);
        assertEquals(11, start);
        assertEquals("-16777216.5",
                new String(buf, start, buf.length-start));
    }

    @Test public void testIntToString()
    {
        assertEquals("-1234", StringHelper.intToString(-1234));
        assertEquals("-16777216", StringHelper.intToString(-16777216));

        assertEquals("3", StringHelper.intToString(3));
        assertEquals("56789", StringHelper.intToString(56789));

        assertEquals(INT_MAX, StringHelper.intToString(Integer.MAX_VALUE));
        assertEquals(INT_MIN, StringHelper.intToString(Integer.MIN_VALUE));
    }

    @Test public void testPriceFractionToString()
    {
        assertEquals("12", StringHelper.priceFractionToString(12, 0));
        assertEquals("-12", StringHelper.priceFractionToString(-12, 0));
        assertEquals("25.50",
                StringHelper.priceFractionToString(25, -FIFTY_CENTS));
        assertEquals("-25.50",
                StringHelper.priceFractionToString(-25, FIFTY_CENTS));
        assertEquals("123456.50",
                StringHelper.priceFractionToString(123456, FIFTY_CENTS));
        assertEquals("-555111222.01",
                StringHelper.priceFractionToString(-555111222, ONE_CENT));
        assertEquals("-478.12345",
                StringHelper.priceFractionToString(-478, 123450000));
    }

    @Test public void testAppendPriceStruct() throws Exception
    {
        StringWriter sw;
        FastCharacterWriter fcw;
        PriceStruct ps = new PriceStruct();

        sw = new StringWriter();
        fcw = new FastCharacterWriter();
        ps.whole = -23975;
        ps.fraction = -TEN_CENTS;
        StringHelper.appendPriceStruct(sw, ps);
        StringHelper.appendPriceStruct(fcw, ps);
        assertEquals("-23975.10", sw.toString());
        assertEquals("-23975.10", fcw.toString());

        sw = new StringWriter();
        fcw = new FastCharacterWriter();
        ps.whole = 0;
        ps.fraction = -FIFTY_CENTS;
        StringHelper.appendPriceStruct(sw, ps);
        StringHelper.appendPriceStruct(fcw, ps);
        assertEquals("-0.50", sw.toString());
        assertEquals("-0.50", fcw.toString());

        sw = new StringWriter();
        fcw = new FastCharacterWriter();
        ps.whole = 125;
        ps.fraction = 0;
        StringHelper.appendPriceStruct(sw, ps);
        StringHelper.appendPriceStruct(fcw, ps);
        assertEquals("125", sw.toString());
        assertEquals("125", fcw.toString());

        sw = new StringWriter();
        fcw = new FastCharacterWriter();
        ps.whole = 123456789;
        ps.fraction = TEN_CENTS;
        StringHelper.appendPriceStruct(sw, ps);
        StringHelper.appendPriceStruct(fcw, ps);
        assertEquals("123456789.10", sw.toString());
        assertEquals("123456789.10", fcw.toString());

        sw = new StringWriter();
        fcw = new FastCharacterWriter();
        ps.whole = 1222333444;
        ps.fraction = ONE_CENT;
        StringHelper.appendPriceStruct(sw, ps);
        StringHelper.appendPriceStruct(fcw, ps);
        assertEquals("1222333444.01", sw.toString());
        assertEquals("1222333444.01", fcw.toString());

        sw = new StringWriter();
        fcw = new FastCharacterWriter();
        ps.whole = 42;
        ps.fraction = 567890123;
        StringHelper.appendPriceStruct(sw, ps);
        StringHelper.appendPriceStruct(fcw, ps);
        assertEquals("42.567890123", sw.toString());
        assertEquals("42.567890123", fcw.toString());
    }

    @Test public void testAppendInt() throws Exception
    {
        StringBuilder sb = new StringBuilder();
        StringWriter sw;
        FastCharacterWriter fcw;

        sw = new StringWriter();
        fcw = new FastCharacterWriter();
        StringHelper.appendInt(sb, -14);
        StringHelper.appendInt(sw, -14);
        StringHelper.appendInt(fcw, -14);
        assertEquals("-14", sb.toString());
        assertEquals("-14", sw.toString());
        assertEquals("-14", fcw.toString());

        sb.setLength(0);
        sw = new StringWriter();
        fcw = new FastCharacterWriter();
        StringHelper.appendInt(sb, Integer.MIN_VALUE);
        StringHelper.appendInt(sw, Integer.MIN_VALUE);
        StringHelper.appendInt(fcw, Integer.MIN_VALUE);
        assertEquals(INT_MIN, sb.toString());
        assertEquals(INT_MIN, sw.toString());
        assertEquals(INT_MIN, fcw.toString());

        sb.setLength(0);
        sw = new StringWriter();
        fcw = new FastCharacterWriter();
        StringHelper.appendInt(sb, 347);
        StringHelper.appendInt(sw, 347);
        StringHelper.appendInt(fcw, 347);
        assertEquals("347", sb.toString());
        assertEquals("347", sw.toString());
        assertEquals("347", fcw.toString());

        sb.setLength(0);
        sw = new StringWriter();
        fcw = new FastCharacterWriter();
        StringHelper.appendInt(sb, 312872);
        StringHelper.appendInt(sw, 312872);
        StringHelper.appendInt(fcw, 312872);
        assertEquals("312872", sb.toString());
        assertEquals("312872", sw.toString());
        assertEquals("312872", fcw.toString());

        sb.setLength(0);
        sw = new StringWriter();
        fcw = new FastCharacterWriter();
        StringHelper.appendInt(sb, 1627384950);
        StringHelper.appendInt(sw, 1627384950);
        StringHelper.appendInt(fcw, 1627384950);
        assertEquals("1627384950", sb.toString());
        assertEquals("1627384950", sw.toString());
        assertEquals("1627384950", fcw.toString());

        sb.setLength(0);
        sw = new StringWriter();
        fcw = new FastCharacterWriter();
        StringHelper.appendInt(sb, Integer.MAX_VALUE);
        StringHelper.appendInt(sw, Integer.MAX_VALUE);
        StringHelper.appendInt(fcw, Integer.MAX_VALUE);
        assertEquals(INT_MAX, sb.toString());
        assertEquals(INT_MAX, sw.toString());
        assertEquals(INT_MAX, fcw.toString());
    }

    @Test public void testBreakString()
    {
        String base = "must sell at tallest sum";
        assertEquals(base, StringHelper.breakString(base,base.length(),','));
        assertEquals(base, StringHelper.breakString(base,base.length()+1,','));
        assertEquals("must s,ell at, talle,st sum",
                StringHelper.breakString(base, 6, ','));
        assertEquals("must sell ,at tallest, sum",
                StringHelper.breakString(base, 10, ','));
    }

    @Test public void testIntToStringWithCommas()
    {
        assertEquals("0", StringHelper.intToStringWithCommas(0));
        assertEquals("-507", StringHelper.intToStringWithCommas(-507));
        assertEquals("-2,147,483,648",
                StringHelper.intToStringWithCommas(Integer.MIN_VALUE));
        assertEquals("112,223", StringHelper.intToStringWithCommas(112223));
        assertEquals("14,142,127",StringHelper.intToStringWithCommas(14142127));
        assertEquals("2,147,483,647",
                StringHelper.intToStringWithCommas(Integer.MAX_VALUE));
    }

    @Test public void testLongtoStringWithCommas()
    {
        assertEquals("0", StringHelper.longToStringWithCommas(0));
        assertEquals("-10,000", StringHelper.longToStringWithCommas(-10000L));
        assertEquals("-9,223,372,036,854,775,808",
                StringHelper.longToStringWithCommas(Long.MIN_VALUE));
        assertEquals("20,011,026",
                StringHelper.longToStringWithCommas(20011026L));
        assertEquals("9,223,372,036,854,775,807",
                StringHelper.longToStringWithCommas(Long.MAX_VALUE));
    }

    @Test public void testZeroPad()
    {
        assertEquals("-14", StringHelper.zeroPad(-14, 3));
        assertEquals("0", StringHelper.zeroPad(0, 1));
        assertEquals("00", StringHelper.zeroPad(0, 2));
        assertEquals("000", StringHelper.zeroPad(0, 3));
        assertEquals("0", StringHelper.zeroPad(0, 4));
        assertEquals("17", StringHelper.zeroPad(17, 1));
        assertEquals("17", StringHelper.zeroPad(17, 2));
        assertEquals("017", StringHelper.zeroPad(17, 3));
        assertEquals("17", StringHelper.zeroPad(17, 4));
        assertEquals("512", StringHelper.zeroPad(512, 1));
        assertEquals("512", StringHelper.zeroPad(512, 2));
        assertEquals("512", StringHelper.zeroPad(512, 3));
        assertEquals("512", StringHelper.zeroPad(512, 4));
    }

    @Test public void testZeroes()
    {
        assertEquals("0", StringHelper.zeroes(1));
        assertEquals("00000", StringHelper.zeroes(5));
        assertEquals("", StringHelper.zeroes(5000));
        assertEquals("", StringHelper.zeroes(-10));
    }

    @Test public void testCharToString()
    {
        assertEquals("\0", StringHelper.charToString('\0'));
        assertEquals("W", StringHelper.charToString('W'));
        assertEquals("\u5123", StringHelper.charToString('\u5123'));
        assertEquals("\uffff", StringHelper.charToString('\uffff'));
    }

    @Test public void testNumbers()
    {
        assertEquals("75", StringHelper.numbers(75));
        assertEquals(Integer.toString(StringHelper.numbers.length),
                StringHelper.numbers(StringHelper.numbers.length));
    }

    @Test public void testNumbersChars()
    {
        assertEquals("0", new String(StringHelper.numbersChars(0)));
        assertEquals("-613", new String(StringHelper.numbersChars(-613)));
        assertEquals(INT_MIN,
                new String(StringHelper.numbersChars(Integer.MIN_VALUE)));
        assertEquals(INT_MAX,
                new String(StringHelper.numbersChars(Integer.MAX_VALUE)));
    }

    @Test public void testNumbersLong()
    {
        assertEquals("42", StringHelper.numbers(42L));
        assertEquals("-99", StringHelper.numbers(-99L));
    }

    @Test public void testZeroPaddedStringValues_2()
    {
        assertEquals("00", StringHelper.zeroPaddedStringValues_2(0));
        assertEquals("08", StringHelper.zeroPaddedStringValues_2(8));
        assertEquals("33", StringHelper.zeroPaddedStringValues_2(33));
        assertEquals("30923", StringHelper.zeroPaddedStringValues_2(30923));
        assertEquals("-7", StringHelper.zeroPaddedStringValues_2(-7));
        assertEquals("-497", StringHelper.zeroPaddedStringValues_2(-497));
    }

    @Test public void testZeroPaddedStringValues_3()
    {
        assertEquals("000", StringHelper.zeroPaddedStringValues_3(0));
        assertEquals("008", StringHelper.zeroPaddedStringValues_3(8));
        assertEquals("033", StringHelper.zeroPaddedStringValues_3(33));
        assertEquals("701", StringHelper.zeroPaddedStringValues_3(701));
        assertEquals("30923", StringHelper.zeroPaddedStringValues_3(30923));
        assertEquals("-7", StringHelper.zeroPaddedStringValues_3(-7));
        assertEquals("-497", StringHelper.zeroPaddedStringValues_3(-497));
    }

    @Test public void testPennies()
    {
        assertEquals(".00", StringHelper.pennies(0));
        assertEquals(".01", StringHelper.pennies(1));
        assertEquals(".50", StringHelper.pennies(50));
        assertEquals(".-3", StringHelper.pennies(-3));      // todo weird result
        assertEquals(".100", StringHelper.pennies(100));    // todo weird result
    }

    @Test public void testLeftPad()
    {
        assertEquals("shazam", StringHelper.leftPad("shazam", 4, "*"));
        assertEquals("*shazam", StringHelper.leftPad("shazam", 7, "*"));
        assertEquals("!@#!shazam", StringHelper.leftPad("shazam", 10, "!@#"));
        assertEquals("shazam", StringHelper.leftPad("shazam", 4, '*'));
        assertEquals("*shazam", StringHelper.leftPad("shazam", 7, '*'));
        assertEquals("****shazam", StringHelper.leftPad("shazam", 10, '*'));
    }

    @Test public void testRightPad()
    {
        assertEquals("presto", StringHelper.rightPad("presto", 4, "*"));
        assertEquals("presto*", StringHelper.rightPad("presto", 7, "*"));
        assertEquals("presto!@#!", StringHelper.rightPad("presto", 10, "!@#"));
        assertEquals("presto", StringHelper.rightPad("presto", 4));
        assertEquals("presto ", StringHelper.rightPad("presto", 7));
        assertEquals("presto    ", StringHelper.rightPad("presto", 10));
        assertEquals("presto", StringHelper.rightPad("presto", 4, ' '));
        assertEquals("presto ", StringHelper.rightPad("presto", 7, ' '));
        assertEquals("presto    ", StringHelper.rightPad("presto", 10, ' '));
    }

    @Test public void testCopies()
    {
        assertEquals("", StringHelper.copies("", 5));
        assertEquals("", StringHelper.copies("abc", 0));
        assertEquals("abc", StringHelper.copies("abc", 1));
        assertEquals("-=-=-=", StringHelper.copies("-=", 3));
    }

    @Test public void testToBase36()
    {
        assertEquals("0", StringHelper.toBase36(0));
        assertEquals("Z", StringHelper.toBase36(35));
        assertEquals("Z00", StringHelper.toBase36(35*36*36));

        assertEquals("CR:LA", StringHelper.toBase36("CR:", 21*36+10));

        assertEquals("W", StringHelper.toBase36(32, 0));
        assertEquals("W", StringHelper.toBase36(32, 1));
        assertEquals("0W", StringHelper.toBase36(32, 2));
        assertEquals("00W", StringHelper.toBase36(32, 3));

        int cow = (12*36 + 24)*36 + 32;
        assertEquals("brown:COW", StringHelper.toBase36("brown:", cow, 0));
        assertEquals("brown:COW", StringHelper.toBase36("brown:", cow, 1));
        assertEquals("brown:COW", StringHelper.toBase36("brown:", cow, 3));
        assertEquals("brown:00COW", StringHelper.toBase36("brown:", cow, 5));
    }

    @Test public void testSpaces()
    {
        // todo: throws exception for negative length
        assertEquals("", StringHelper.spaces(0));
        assertEquals("     ", StringHelper.spaces(5));

        StringBuilder threeHundred = new StringBuilder();
        for (int i = 0; i < 30; ++i)
        {
            threeHundred.append("          ");
        }
        assertEquals(threeHundred.toString(), StringHelper.spaces(300));
    }

    @Test public void testStringGetChars()
    {
        char c[] = StringHelper.stringGetChars("hi");
        assertEquals(2, c.length);
        assertEquals('h', c[0]);
        assertEquals('i', c[1]);
    }

    @Test public void testStringGetBytes()
    {
        byte b[] = StringHelper.stringGetBytes("cat");
        assertEquals(3, b.length);
        assertEquals('c', b[0]);
        assertEquals('a', b[1]);
        assertEquals('t', b[2]);
    }

    @Test public void testCopyStringToCharArray()
    {
        char array[] = new char[20];
        int length;

        Arrays.fill(array, ' ');
        length = StringHelper.copyStringToCharArray(array, 1, "hello", 1, 3);
        assertEquals(3, length);
        assertEquals(' ', array[0]);
        assertEquals('e', array[1]);
        assertEquals('l', array[2]);
        assertEquals('l', array[3]);
        assertEquals(' ', array[4]);

        Arrays.fill(array, '-');
        length = StringHelper.copyStringToCharArray(array, 0, "hello", 2);
        assertEquals(2, length);
        assertEquals('h', array[0]);
        assertEquals('e', array[1]);
        assertEquals('-', array[2]);

        Arrays.fill(array, '.');
        length = StringHelper.copyStringToCharArray(array, 1, "help");
        assertEquals(4, length);
        assertEquals('.', array[0]);
        assertEquals('h', array[1]);
        assertEquals('e', array[2]);
        assertEquals('l', array[3]);
        assertEquals('p', array[4]);
        assertEquals('.', array[5]);

        Arrays.fill(array, ' ');
        length = StringHelper.copyStringToCharArray(array, 1, "yes", "no");
        assertEquals(5, length);
        assertEquals(' ', array[0]);
        assertEquals('y', array[1]);
        assertEquals('e', array[2]);
        assertEquals('s', array[3]);
        assertEquals('n', array[4]);
        assertEquals('o', array[5]);
        assertEquals(' ', array[6]);

        Arrays.fill(array, '?');
        length = StringHelper.copyStringToCharArray(array, 0, "a", "b", "c");
        assertEquals(3, length);
        assertEquals('a', array[0]);
        assertEquals('b', array[1]);
        assertEquals('c', array[2]);
        assertEquals('?', array[3]);

        Arrays.fill(array, '.');
        length = StringHelper.copyStringToCharArray(array, 1, "1", "x", "2", "y");
        assertEquals(4, length);
        assertEquals('.', array[0]);
        assertEquals('1', array[1]);
        assertEquals('x', array[2]);
        assertEquals('2', array[3]);
        assertEquals('y', array[4]);
        assertEquals('.', array[5]);
    }

    @Test public void testNewString()
    {
        char fog[] = { 'f', 'o', 'g' };
        String s = StringHelper.newString(fog);
        assertEquals("fog", s);

        s = StringHelper.newString(fog, 2, 1);
        assertEquals("g", s);
    }

    @Test public void testTrimNumberCharsThousand()
    {
        int num = 1234567890;
        char chars[];
        chars = StringHelper.trimNumberCharsThousand_x_xxx_xxx_000(num);
        assertEquals(3, chars.length);
        assertEquals('8', chars[0]);
        assertEquals('9', chars[1]);
        assertEquals('0', chars[2]);

        chars = StringHelper.trimNumberCharsThousand_x_xxx_000_xxx(num);
        assertEquals(3, chars.length);
        assertEquals('5', chars[0]);
        assertEquals('6', chars[1]);
        assertEquals('7', chars[2]);

        chars = StringHelper.trimNumberCharsThousand_x_000_xxx_xxx(num);
        assertEquals(3, chars.length);
        assertEquals('2', chars[0]);
        assertEquals('3', chars[1]);
        assertEquals('4', chars[2]);

        chars = StringHelper.trimNumberCharsThousand_0_xxx_xxx_xxx(num);
        assertEquals(1, chars.length);
        assertEquals('1', chars[0]);

        num = 1234567008;
        chars = StringHelper.trimNumberCharsThousand_x_xxx_xxx_000(num);
        assertEquals(1, chars.length);
        assertEquals('8', chars[0]);
    }

    @Test public void testZeroNumberCharsThousand()
    {
        int num = 1002003004;
        char chars[];

        chars = StringHelper.zeroNumberCharsThousand_x_xxx_xxx_000(num);
        assertEquals(3, chars.length);
        assertEquals('0', chars[0]);
        assertEquals('0', chars[1]);
        assertEquals('4', chars[2]);

        chars = StringHelper.zeroNumberCharsThousand_x_xxx_000_xxx(num);
        assertEquals(3, chars.length);
        assertEquals('0', chars[0]);
        assertEquals('0', chars[1]);
        assertEquals('3', chars[2]);

        chars = StringHelper.zeroNumberCharsThousand_x_000_xxx_xxx(num);
        assertEquals(3, chars.length);
        assertEquals('0', chars[0]);
        assertEquals('0', chars[1]);
        assertEquals('2', chars[2]);
    }
}
