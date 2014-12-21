package com.cboe.util;

/**
 * Pair.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * A pair of two objects
 *
 */

public class Pair
{
    public Object first;
    public Object second;

    public Pair()
    {

    }

    public Pair(Object first, Object second)
    {
        this.first  = first;
        this.second = second;
    }

    public Pair setFirst(Object first)
    {
        this.first = first;

        return this;
    }

    public Object getFirst()
    {
        return first;
    }

    public String getFirstAsString()
    {
        return (String) first;
    }

    public StringBuffer getFirstAsStringBuffer()
    {
        return (StringBuffer) first;
    }

    public Integer getFirstAsInteger()
    {
        return (Integer) first;
    }

    public Long getFirstAsLong()
    {
        return (Long) first;
    }

    public Float getFirstAsFloat()
    {
        return (Float) first;
    }

    public Double getFirstAsDouble()
    {
        return (Double) first;
    }

    public Boolean getFirstAsBoolean()
    {
        return (Boolean) first;
    }

    public Pair setSecond(Object second)
    {
        this.second = second;

        return this;
    }

    public Object getSecond()
    {
        return second;
    }

    public String getSecondAsString()
    {
        return (String) second;
    }

    public StringBuffer getSecondAsStringBuffer()
    {
        return (StringBuffer) second;
    }

    public Integer getSecondAsInteger()
    {
        return (Integer) second;
    }

    public Long getSecondAsLong()
    {
        return (Long) second;
    }

    public Float getSecondAsFloat()
    {
        return (Float) second;
    }

    public Double getSecondAsDouble()
    {
        return (Double) second;
    }

    public Boolean getSecondAsBoolean()
    {
        return (Boolean) second;
    }

    public Pair reset(Object first, Object second)
    {
        this.first  = first;
        this.second = second;

        return this;
    }

    public String toString()
    {
        String one = first.toString();
        String two = second.toString();
        StringBuilder result = new StringBuilder(one.length()+two.length()+15);
        result.append("Pair([").append(first).append("],[").append(second).append("])");
        return result.toString();
    }
}
