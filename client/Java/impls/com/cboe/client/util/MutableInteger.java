package com.cboe.client.util;

/**
 * MutableInteger.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * integer object that can be manipulated
 *
 */

public class MutableInteger
{
    public int integer;

    public MutableInteger()                {}
    public MutableInteger(int integer)     {this.integer = integer;}
    public MutableInteger(Integer integer) {this.integer = integer.intValue();}

    public int intValue()      	           {return integer;}
    public int reset()         	           {return integer = 0;}
    public int set(int set)    	           {return integer = set;}
    public int inc()           	           {return ++integer;}
    public int inc(int add)    	           {return integer += add;}
    public int dec()           	           {return --integer;}
    public int dec(int sub)    	           {return integer -= sub;}
    public int decZero()                   {--integer; if (integer < 0) integer = 0; return integer;}
    public int decZero(int sub)            {integer -= sub; if (integer < 0) integer = 0; return integer;}
    public int hashCode()                  {return integer;}
    public String toString()               {return StringHelper.intToString(integer);}

    public static class ThreadLocalMutableInteger extends ThreadLocal
    {
        public Object         initialValue()      {return new MutableInteger();}
        public MutableInteger getMutableInteger() {return (MutableInteger) get();}
    }

    public static final ThreadLocalMutableInteger threadLocalMutableInteger = new ThreadLocalMutableInteger();
}
