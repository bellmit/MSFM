package com.cboe.cfix.util;

/**
 * IntFastCharacterWriterPair.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * A pair of an int and a HasFastCharacterWriter
 *
 */

import com.cboe.client.util.*;

public class IntFastCharacterWriterPair implements FirstIntIF
{
    public int                 first;
    public FastCharacterWriter fastCharacterWriter;

    public IntFastCharacterWriterPair()
    {

    }

    public IntFastCharacterWriterPair(int first, FastCharacterWriter fastCharacterWriter)
    {
        this.first               = first;
        this.fastCharacterWriter = fastCharacterWriter;
    }

    public int getFirst()
    {
        return first;
    }

    public void setFirst(int first)
    {
        this.first = first;
    }

    public FastCharacterWriter getFastCharacterWriter()
    {
        return fastCharacterWriter;
    }

    public void setFastCharacterWriter(FastCharacterWriter fastCharacterWriter)
    {
        this.fastCharacterWriter = fastCharacterWriter;
    }

    public IntFastCharacterWriterPair reset(int first, FastCharacterWriter fastCharacterWriter)
    {
        this.first               = first;
        this.fastCharacterWriter = fastCharacterWriter;

        return this;
    }
}
