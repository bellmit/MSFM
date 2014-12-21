package com.cboe.cfix.fix.util;

/**
 * FixMessageBuilder.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * This object knows how to help build the Fix message from all the fields
 *
 */

import java.io.*;

import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.*;
import com.cboe.idl.cmiUtil.*;

public class FixMessageBuilder implements FixMessageBuilderIF
{
    public FastCharacterWriter fastCharacterWriter;

    public FixMessageBuilder()
    {
        this.fastCharacterWriter = new FastCharacterWriter();
    }

    public FixMessageBuilder(int capacity)
    {
        this.fastCharacterWriter = new FastCharacterWriter(capacity);
    }

    public FixMessageBuilder(FastCharacterWriter fastCharacterWriter)
    {
        this.fastCharacterWriter = fastCharacterWriter;
    }

    public void clear()
    {
        fastCharacterWriter.clear();
    }

    public String toString()
    {
        return fastCharacterWriter.toString();
    }

    public FastCharacterWriter getFastCharacterWriter()
    {
        return fastCharacterWriter;
    }

    public Writer getWriter()
    {
        return fastCharacterWriter;
    }

    public int size()
    {
        return fastCharacterWriter.size();
    }

    public void append(char[] tag, String value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public void append(char[] tag, char[] value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public void append(char[] tag, char value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public void append(char[] tag, byte value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, (char) value, FixFieldIF.SOHchar);
    }

    public void append(char[] tag, boolean value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, (value ? FixFieldIF.FIX_YESchar : FixFieldIF.FIX_NOchar), FixFieldIF.SOHchar);
    }

    public void append(char[] tag, int value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public void append(String tag, String value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public void append(String tag, char value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public void append(String tag, byte value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, (char) value, FixFieldIF.SOHchar);
    }

    public void append(String tag, boolean value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, (value ? FixFieldIF.FIX_YESchar : FixFieldIF.FIX_NOchar), FixFieldIF.SOHchar);
    }

    public void append(String tag, int value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public void append(FixFieldIF field)
    {
        if (field != null)
        {
            fastCharacterWriter.write(field.getTagAsString(), FixFieldIF.EQUALSchar, field.getValue(), FixFieldIF.SOHchar);
        }
    }

    public void append(String string)
    {
        fastCharacterWriter.write(string);
    }

    public void append(char[] chars)
    {
        fastCharacterWriter.write(chars);
    }

    public void append(char[] tag, PriceStruct priceStruct)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        PriceHelper.appendPriceStruct(fastCharacterWriter, priceStruct);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void append(char[] tag, int price, byte priceScale)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        StringHelper.appendPriceWithScale(fastCharacterWriter, price, priceScale);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }


    public void append(String tag, PriceStruct priceStruct)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        PriceHelper.appendPriceStruct(fastCharacterWriter, priceStruct);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixUTCDateFormat(char[] tag, DateStruct dateStruct)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixUTCDateFormat(fastCharacterWriter, dateStruct);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixUTCDateFormat(String tag, DateStruct dateStruct)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixUTCDateFormat(fastCharacterWriter, dateStruct);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixUTCDateFormat(char[] tag, long millis)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixUTCDateFormat(fastCharacterWriter, millis);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixUTCDateFormat(String tag, long millis)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixUTCDateFormat(fastCharacterWriter, millis);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixUTCTimeOnlyFormat(char[] tag, long millis)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixUTCTimeOnlyFormat(fastCharacterWriter, millis);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixUTCTimeOnlyFormat(String tag, long millis)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixUTCTimeOnlyFormat(fastCharacterWriter, millis);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixUTCTimeOnlyFormat(char[] tag, TimeStruct timeStruct)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixUTCTimeOnlyFormat(fastCharacterWriter, timeStruct);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixUTCTimeOnlyFormat(String tag, TimeStruct timeStruct)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixUTCTimeOnlyFormat(fastCharacterWriter, timeStruct);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixUTCTimeStampFormat(char[] tag, DateStruct dateStruct, TimeStruct timeStruct)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixUTCTimeStampFormat(fastCharacterWriter, dateStruct, timeStruct);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixUTCTimeStampFormat(String tag, DateStruct dateStruct, TimeStruct timeStruct)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixUTCTimeStampFormat(fastCharacterWriter, dateStruct, timeStruct);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixUTCTimeStampFormat(char[] tag, long millis)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixUTCTimeStampFormat(fastCharacterWriter, millis);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixUTCTimeStampFormat(String tag, long millis)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixUTCTimeStampFormat(fastCharacterWriter, millis);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixLocalMktDateFormat(char[] tag, DateStruct dateStruct)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixLocalMktDateFormat(fastCharacterWriter, dateStruct);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixLocalMktDateFormat(String tag, DateStruct dateStruct)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixLocalMktDateFormat(fastCharacterWriter, dateStruct);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixLocalMktDateFormat(char[] tag, long millis)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixLocalMktDateFormat(fastCharacterWriter, millis);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void appendDateInFixLocalMktDateFormat(String tag, long millis)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar);
        DateHelper.appendDateInFixLocalMktDateFormat(fastCharacterWriter, millis);
        fastCharacterWriter.write(FixFieldIF.SOHchar);
    }

    public void insert(char[] tag, String value)
    {
        fastCharacterWriter.insert(0, tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public void insert(char[] tag, char[] value)
    {
        fastCharacterWriter.insert(0, tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public void insert(String tag, int value)
    {
        fastCharacterWriter.insert(0, tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public void insert(String tag, char value)
    {
        fastCharacterWriter.insert(0, tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public void insert(String tag, String value)
    {
        fastCharacterWriter.insert(0, tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public void insert(String tag, byte value)
    {
        fastCharacterWriter.insert(0, tag, FixFieldIF.EQUALSchar, (char) value, FixFieldIF.SOHchar);
    }

    public void insert(char[] tag, char value)
    {
        fastCharacterWriter.insert(0, tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public void insert(char[] tag, byte value)
    {
        fastCharacterWriter.insert(0, tag, FixFieldIF.EQUALSchar, (char) value, FixFieldIF.SOHchar);
    }

    public void insert(char[] tag, int value)
    {
        fastCharacterWriter.insert(0, tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public void replace(int offset, int length, char[] value)
    {
        fastCharacterWriter.replace(offset, length, value);
    }
    public void replace(int offset, int length, String value)
    {
        fastCharacterWriter.replace(offset, length, value);
    }

    public static final void writeFixString(FastCharacterWriter fastCharacterWriter, String tag, String value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public static final void writeFixString(FastCharacterWriter fastCharacterWriter, String tag, int value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public static final void writeFixString(FastCharacterWriter fastCharacterWriter, String tag, boolean value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, (value ? FixFieldIF.FIX_YESchar : FixFieldIF.FIX_NOchar), FixFieldIF.SOHchar);
    }

    public static final void writeFixString(FastCharacterWriter fastCharacterWriter, char[] tag, String value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public static final void writeFixString(FastCharacterWriter fastCharacterWriter, char[] tag, int value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }

    public static final void writeFixString(FastCharacterWriter fastCharacterWriter, char[] tag, boolean value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, (value ? FixFieldIF.FIX_YESchar : FixFieldIF.FIX_NOchar), FixFieldIF.SOHchar);
    }

    public static final void writeFixString(FastCharacterWriter fastCharacterWriter, char[] tag, char[] value)
    {
        fastCharacterWriter.write(tag, FixFieldIF.EQUALSchar, value, FixFieldIF.SOHchar);
    }
}
