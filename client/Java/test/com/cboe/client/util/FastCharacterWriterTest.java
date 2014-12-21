package com.cboe.client.util;

import java.io.ByteArrayOutputStream;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;    // annotation
import org.junit.Test;      // annotation

public class FastCharacterWriterTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(FastCharacterWriterTest.class);
    }

    private FastCharacterWriter fcw;

    // Run this method before every @Test method
    @Before public void createFCW()
    {
        fcw = new FastCharacterWriter();
    }

    @Test public void testEmpty()
    {
        assertEquals(0, FastCharacterWriter.EMPTY_FAST_CHARACTER_WRITER.size());
        assertTrue(FastCharacterWriter.EMPTY_FAST_CHARACTER_WRITER.toString()
                   .equals(""));
    }

    @Test public void testDefaultConstructor()
    {
        assertNotNull(fcw);
        fcw.write("hello");
        assertEquals("hello", fcw.toString());
    }

    @Test public void testCapacityConstructor()
    {
        fcw = new FastCharacterWriter(20);
        assertNotNull(fcw);
        fcw.write("Boojum!");
        assertEquals("Boojum!", fcw.toString());
    }

    @Test public void testLockConstructor()
    {
        Object lockObject = new Object();
        fcw = new FastCharacterWriter(lockObject);
        assertNotNull(fcw);
        fcw.write("Zrillir");
        assertEquals("Zrillir", fcw.toString());
    }

    @Test public void testFcwConstructor()
    {
        fcw.write("I'm just drawn that way");

        FastCharacterWriter copy = new FastCharacterWriter(fcw);
        assertEquals("I'm just drawn that way", copy.toString());
    }

    @Test public void testClear()
    {
        fcw.write("nevermore");
        assertEquals("nevermore", fcw.toString());
        fcw.clear();
        assertEquals("", fcw.toString());
    }

    @Test public void testFlush()
    {
        fcw.write("Nextor");
        assertEquals("Nextor", fcw.toString());
        fcw.flush();
        assertEquals("Nextor", fcw.toString());
    }

    @Test public void testClose()
    {
        fcw.write("scrivener");
        assertEquals("scrivener", fcw.toString());
        fcw.close();
        assertEquals("scrivener", fcw.toString());
    }

    @Test public void testSize()
    {
        assertEquals(0, fcw.size());
        fcw.write("grievously");
        assertEquals(10, fcw.size());
        fcw.write(" golf");
        assertEquals(15, fcw.size());
    }

    @Test public void testToString()
    {
        fcw.write("finesse");
        assertEquals("finesse", fcw.toString());
    }

    @Test public void testToCharArray()
    {
        String message = "Car 54 where are you";
        fcw.write(message);
        char chars[] = fcw.toCharArray();
        assertEquals(message, new String(chars, 0, fcw.size()));
    }

    @Test public void testWriteint()
    {
        fcw.write('a');
        assertEquals("a", fcw.toString());
        fcw.write('b');
        assertEquals("ab", fcw.toString());
    }

    @Test public void testWriteCharArray()
    {
        char one[] = { 'o', 'n', 'e' };
        char two[] = { 't', 'w', 'o' };
        char three[] = { 't', 'r', 'i' };

        fcw.write(one);
        assertEquals("one", fcw.toString());
        fcw.write(two);
        assertEquals("onetwo", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(one, two);
        assertEquals("onetwo", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(one, two, three);
        assertEquals("onetwotri", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(three, one, two, three);
        assertEquals("trionetwotri", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(one, two, one, three, two);
        assertEquals("onetwoonetritwo", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(three, three, two, two, one, one);
        assertEquals("tritritwotwooneone", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(one, 'a');
        assertEquals("onea", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(one, 1, 1);
        assertEquals("n", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(two, '&', three, '.');
        assertEquals("two&tri.", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(three, 'x', 'y', 'z');
        assertEquals("trixyz", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(one, '!', "String", '?');
        assertEquals("one!String?", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(two, 'x', 42, 'y');
        assertEquals("twox42y", fcw.toString());
    }

    @Test public void testWriteString()
    {
        char one[] = { 'e', 'k', 'h' };
        String unu = "un";
        String du = "deux";
        String tri = "trois";
        String kvar = "quatre";

        fcw.write(unu, 'e');
        assertEquals("une", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(unu, '@', one, '.');
        assertEquals("un@ekh.", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(du, 'x', tri, 'a');
        assertEquals("deuxxtroisa", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(unu, '%', -5, 'W');
        assertEquals("un%-5W", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(kvar);
        assertEquals(kvar, fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(du, 1, 3);
        assertEquals("eux", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(unu, du);
        assertEquals("undeux", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(unu, du, tri);
        assertEquals("undeuxtrois", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.write(kvar, tri, du, unu);
        assertEquals("quatretroisdeuxun", fcw.toString());
    }

    @Test public void testWriteFastCharacterWriter()
    {
        String message = "You are old, father William";
        FastCharacterWriter other = new FastCharacterWriter();

        other.write(message);
        fcw.write(other);
        assertEquals(message, fcw.toString());
    }

    @Test public void testWriteInt()
    {
        fcw.writeInt(42);
        assertEquals("42", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.writeInt(Integer.MAX_VALUE);
        assertEquals("2147483647", fcw.toString());

        fcw.clear();
        assertEquals("", fcw.toString());
        fcw.writeInt(Integer.MIN_VALUE);
        assertEquals("-2147483648", fcw.toString());
    }

    @Test public void testInsert()
    {
        fcw.write("ABCDEFGH");
        fcw.insert(0, 10);
        assertEquals("10ABCDEFGH", fcw.toString());
        fcw.insert(1, 'w');
        assertEquals("1w0ABCDEFGH", fcw.toString());

        char chars1[] = { 'x', 'y' };
        fcw.insert(4, chars1);
        assertEquals("1w0AxyBCDEFGH", fcw.toString());

        char chars2[] = { '+', '-' };
        fcw.insert(8, chars2, chars1);
        assertEquals("1w0AxyBC+-xyDEFGH", fcw.toString());

        char chars3[] = { '$', '@', '.' };
        fcw.insert(3, chars3, chars1, chars2);
        assertEquals("1w0$@.xy+-AxyBC+-xyDEFGH", fcw.toString());

        fcw.clear();
        fcw.write("ABCDEFGH");
        fcw.insert(7, chars3, 'g');
        assertEquals("ABCDEFG$@.gH", fcw.toString());

        fcw.insert(9, chars3, 1, 2);
        assertEquals("ABCDEFG$@@..gH", fcw.toString());

        fcw.insert(11, chars1, 't', chars2, '4');
        assertEquals("ABCDEFG$@@.xyt+-4.gH", fcw.toString());

        fcw.insert(8, chars3, '*', '#', '!');
        assertEquals("ABCDEFG$$@.*#!@@.xyt+-4.gH", fcw.toString());

        fcw.clear();
        fcw.write("ABCDEFGH");

        String string1 = "()";
        fcw.insert(6, chars1, 'u', string1, 'v');
        assertEquals("ABCDEFxyu()vGH", fcw.toString());

        fcw.insert(4, chars2, '%', 17, '/');
        assertEquals("ABCD+-%17/EFxyu()vGH", fcw.toString());

        fcw.insert(7, string1, '=');
        assertEquals("ABCD+-%()=17/EFxyu()vGH", fcw.toString());

        fcw.insert(8, string1, '~', chars2, ':');
        assertEquals("ABCD+-%(()~+-:)=17/EFxyu()vGH", fcw.toString());

        fcw.clear();
        fcw.write("ABCDEFGH");

        String string2 = "[]";
        fcw.insert(7, string1, 'r', string2, 's');
        assertEquals("ABCDEFG()r[]sH", fcw.toString());

        fcw.insert(11, string2, 'a', -12, 'z');
        assertEquals("ABCDEFG()r[[]a-12z]sH", fcw.toString());

        fcw.insert(10, string1);
        assertEquals("ABCDEFG()r()[[]a-12z]sH", fcw.toString());

        fcw.insert(0, string1, 1, 1);
        assertEquals(")ABCDEFG()r()[[]a-12z]sH", fcw.toString());

        fcw.insert(0, string1, string2);
        assertEquals("()[])ABCDEFG()r()[[]a-12z]sH", fcw.toString());

        fcw.insert(8, string1, string2, string1);
        assertEquals("()[])ABC()[]()DEFG()r()[[]a-12z]sH", fcw.toString());

        fcw.insert(16, string2, string1, string2, string1);
        assertEquals("()[])ABC()[]()DE[]()[]()FG()r()[[]a-12z]sH", fcw.toString());

        FastCharacterWriter other = new FastCharacterWriter();
        other.write("abc");
        fcw.insert(11, other);
        assertEquals("()[])ABC()[abc]()DE[]()[]()FG()r()[[]a-12z]sH", fcw.toString());

        other = null;
        fcw.insert(11, other);
        assertEquals("()[])ABC()[abc]()DE[]()[]()FG()r()[[]a-12z]sH", fcw.toString());

        other = new FastCharacterWriter();
        fcw.insert(11, other);
        assertEquals("()[])ABC()[abc]()DE[]()[]()FG()r()[[]a-12z]sH", fcw.toString());        
    }

    @Test public void testReplaceString() throws Exception
    {
        fcw.write("ABCDEFGHIJ");
        try
        {
            fcw.replace(10, 2, "hi");
            fail();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        { /* We expected this, offset==10 is beyond end of content */ }

        try
        {
            fcw.replace(5, 11, "hi");
            fail();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        { /* We expected this, length==11 > size of content */ }

        try
        {
            fcw.replace(9, 2, "hi");
            fail();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {/* We expected this, extent exceeds size of content */ }

        try
        {
            fcw.replace(4, 3, "hi");
            fail();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        { /* We expected this, 3 > length of "hi" */ }

        fcw.replace(8, 2, "hi");
        assertEquals("ABCDEFGHhi", fcw.toString());

        fcw.replace(1, 2, "ice cream");
        assertEquals("AicDEFGHhi", fcw.toString());
    }

    @Test public void testReplaceChars()
    {
        fcw.write("ABCDEFGHIJ");

        char chars[] = "orange".toCharArray();
        try
        {
            fcw.replace(10, 2, chars);
            fail();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        { /* We expected this, offset==10 is beyond end of content */ }

        try
        {
            fcw.replace(5, 11, chars);
            fail();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        { /* We expected this, length==11 > size of content */ }

        try
        {
            fcw.replace(9, 2, chars);
            fail();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {/* We expected this, extent exceeds size of content */ }

        try
        {
            fcw.replace(4, 13, chars);
            fail();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        { /* We expected this, 13 > chars.length */ }

        fcw.replace(3, 3, chars);
        assertEquals("ABCoraGHIJ", fcw.toString());

        fcw.replace(0, 1, chars);
        assertEquals("oBCoraGHIJ", fcw.toString());
    }

    @Test public void testReplaceCharsOffset()
    {
        fcw.write("ABCDEFGHIJ");

        char chars[] = "microperferations".toCharArray();
        try
        {
            fcw.replace(10, 2, chars, 3);
            fail();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        { /* We expected this, offset==10 is beyond end of content */ }

        try
        {
            fcw.replace(5, 11, chars, 7);
            fail();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        { /* We expected this, length==11 > size of content */ }

        try
        {
            fcw.replace(9, 2, chars, 5);
            fail();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {/* We expected this, extent exceeds size of content */ }

        try
        {
            fcw.replace(4, 3, chars, chars.length - 1);
            fail();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        { /* We expected this, exceeding chars.length */ }

        try
        {
            fcw.replace(4, 2, chars, chars.length - 1);
            fail();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        { /* We expected this, exceeding chars.length */ }

        fcw.replace(4, 1, chars, chars.length-1);
        assertEquals("ABCDsFGHIJ", fcw.toString());

        fcw.replace(6, 2, chars, 5);
        assertEquals("ABCDsFpeIJ", fcw.toString());
    }

    @Test public void testWriteOutputStreamClone() throws Exception
    {
        String message = "In Ulm und um Ulm herum";

        fcw.write(message);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        fcw.write(os);
        assertEquals(message, os.toString());

        FastCharacterWriter another = (FastCharacterWriter) fcw.clone();
        assertNotSame(fcw, another);

        assertEquals(fcw.toString(), another.toString());
    }
}
