package com.cboe.client.util.junit;

/**
 * junitPerformanceTests.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.client.util.*;
import com.cboe.client.util.queue.*;
import com.cboe.client.util.collections.*;

public class junitPerformanceTests extends JunitTestCase
{
    public void testGrowableCircularQueueVersusArrayList() throws Exception
    {
        int NUM = 200;
        Integer[] integers = IntegerHelper.integers;
        int total = integers.length * NUM;

        Stopwatch stopWatchA = new Stopwatch();

        ArrayList list = new ArrayList(16);

        for (int j = 0; j < NUM; j++)
        {
            for (int i = 0; i < integers.length; i++)
            {
                list.add(integers[i]);
            }
        }

        for (int i = 0; i < total; i++)
        {
            list.remove(0);
        }

        stopWatchA.stop();

        Stopwatch stopWatchB = new Stopwatch();

        CircularQueue queue = new CircularQueue(16);

        for (int j = 0; j < NUM; j++)
        {
            for (int i = 0; i < integers.length; i++)
            {
                queue.add(integers[i]);
            }
        }

        for (int i = 0; i < total; i++)
        {
            queue.remove();
        }

        stopWatchB.stop();

        System.out.print("[" + StringHelper.intToStringWithCommas(total) + " times] ArrayList " + stopWatchA + " vs growableCircularQueue " + stopWatchB + " ***> ");

        if (stopWatchA.elapsed() > stopWatchB.elapsed())
            System.out.println((float) (stopWatchA.elapsed() / stopWatchB.elapsed()) + " times faster");
        else
            System.out.println((float) (stopWatchB.elapsed() / stopWatchA.elapsed()) + " times slower");
    }

    public void testSystemArraycopy()
    {
        int NUM = 10000;
        int total = 1000 * NUM;
        char[][] array = StringHelper.zeroPaddedCharsValues_3;

        char[] buf = new char[3];
        int crcA = 0;
        int crcB = 0;

        Stopwatch stopWatchA = new Stopwatch();

        for (int j = 0; j < NUM; j++)
        {
            for (int i = 0; i < 1000; i++)
            {
                System.arraycopy(array[i], 0, buf, 0, 3);
                crcA += buf[0] + buf[1] + buf[2]; //this is just to prevent above being optimized away
            }
        }

        stopWatchA.stop();

        Stopwatch stopWatchB = new Stopwatch();

        for (int j = 0; j < NUM; j++)
        {
            for (int i = 0; i < 1000; i++)
            {
                buf[0] = array[i][0];
                buf[1] = array[i][1];
                buf[2] = array[i][2];
                crcB += buf[0] + buf[1] + buf[2]; //this is just to prevent above being optimized away
            }
        }

        stopWatchB.stop();

        if (crcA != crcB)
        {
            System.out.println("NOT EQUAL: crcA: " + crcA + " crcB:" + crcB);
        }

        System.out.print("[" + StringHelper.intToStringWithCommas(total) + " times] systemArrayCopy " + stopWatchA + " vs copy " + stopWatchB + " ***> ");

        if (stopWatchA.elapsed() > stopWatchB.elapsed())
            System.out.println(((float) stopWatchA.elapsed() / (float) stopWatchB.elapsed()) + " times faster");
        else
            System.out.println(((float) stopWatchB.elapsed() / (float) stopWatchA.elapsed()) + " times slower");
    }

    public void testIntMap()
    {
        String[] numbers = StringHelper.numbers;
        int NUM = 5;
        int total = numbers.length * NUM;
        HashMap    hashMap    = new HashMap();
        IntObjectMap intObjectMap = IntObjectMap.unsynchronizedMap();
        int crcA = 0;
        int crcB = 0;

        Stopwatch stopWatchA = new Stopwatch();

        for (int i = 0; i < numbers.length; i++)
        {
            hashMap.put(new Integer(i), numbers[i]);
        }

        for (int j = 0; j < NUM; j++)
        {
            for (int i = numbers.length - 1; i >= 0; i--)
            {
                crcA += hashMap.get(new Integer(i)).hashCode();
            }
        }

        stopWatchA.stop();

        Stopwatch stopWatchB = new Stopwatch();

        for (int i = 0; i < numbers.length; i++)
        {
            intObjectMap.putKeyValue(i, numbers[i]);
        }

        for (int j = 0; j < NUM; j++)
        {
            for (int i = numbers.length - 1; i >= 0; i--)
            {
                crcB += intObjectMap.getValueForKey(i).hashCode();
            }
        }

        stopWatchB.stop();

        if (crcA != crcB)
        {
            System.out.println("NOT EQUAL: crcA: " + crcA + " crcB:" + crcB);
        }

        System.out.print("[" + StringHelper.intToStringWithCommas(total) + " times] hashMap " + stopWatchA + " vs intObjectMap " + stopWatchB + " ***> ");
        if (stopWatchA.elapsed() < stopWatchB.elapsed())
            System.out.println(((float)stopWatchB.elapsed() / (float)stopWatchA.elapsed()) + " times slower");
        else if (stopWatchA.elapsed() > stopWatchB.elapsed())
            System.out.println(((float)stopWatchA.elapsed() / (float)stopWatchB.elapsed()) + " times faster");
        else
            System.out.println("same");
    }

    public void testIntToString() throws Exception
    {
        int NUM = 100;
        int HOWMANY = 10000;
        int START = 0;
        int total = HOWMANY * NUM;

        int crcA = 0;
        int crcB = 0;

        Stopwatch stopWatchA = new Stopwatch();

        for (int j = 0; j < NUM; j++)
        {
            for (int i = 0; i < HOWMANY; i++)
            {
                crcA += (Integer.toString(START + i).hashCode());
            }
        }

        stopWatchA.stop();

        Stopwatch stopWatchB = new Stopwatch();

        for (int j = 0; j < NUM; j++)
        {
            for (int i = 0; i < HOWMANY; i++)
            {
                crcB += (StringHelper.intToString(START + i).hashCode());
            }
        }

        stopWatchB.stop();

        if (crcA != crcB)
        {
            System.out.println("NOT EQUAL: crcA: " + crcA + " crcB:" + crcB);
        }

        System.out.print("[" + StringHelper.intToStringWithCommas(total) + " times] Integer.toString() " + stopWatchA + " vs StringHelper.intToString() " + stopWatchB + " ***> ");
        if (stopWatchA.elapsed() < stopWatchB.elapsed())
            System.out.println(((float)stopWatchB.elapsed() / (float)stopWatchA.elapsed()) + " times slower");
        else if (stopWatchA.elapsed() > stopWatchB.elapsed())
            System.out.println(((float)stopWatchA.elapsed() / (float)stopWatchB.elapsed()) + " times faster");
        else
            System.out.println("same");
    }

    protected String[] makeRandomStrings(int howMany, int length)
    {
        String[] array = new String[howMany];

        char[] buf = new char[length];

        for (int i = 0; i < howMany; i++)
        {
            for (int j = 0; j < length; j++)
            {
                buf[j] = (char) ('A' + (int) (Math.random() * 26));
            }

            array[i] = new String(buf);
        }

        return array;
    }
}
