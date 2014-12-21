package com.cboe.cfix.interfaces;

/**
 * FixMessageBuilderIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * This object knows how to help build the Fix message from all the fields
 *
 */

import java.io.*;

import com.cboe.idl.cmiUtil.*;

public interface FixMessageBuilderIF
{
    public Writer getWriter();
    public int  size();
    public void clear();
    public void append(FixFieldIF field);
    public void append(String string);
    public void append(String tag, PriceStruct priceStruct);
    public void append(String tag, String value);
    public void append(String tag, byte value);
    public void append(String tag, char value);
    public void append(String tag, int value);
    public void append(char[] chars);
    public void append(char[] tag, PriceStruct priceStruct);
    public void append(char[] tag, int price, byte priceScale);
    public void append(char[] tag, String value);
    public void append(char[] tag, byte value);
    public void append(char[] tag, char[] value);
    public void append(char[] tag, char value);
    public void append(char[] tag, int value);
    public void appendDateInFixUTCDateFormat(char[] tag, DateStruct dateStruct);
    public void appendDateInFixUTCDateFormat(String tag, DateStruct dateStruct);
    public void appendDateInFixUTCDateFormat(char[] tag, long millis);
    public void appendDateInFixUTCDateFormat(String tag, long millis);
    public void appendDateInFixUTCTimeOnlyFormat(char[] tag, long millis);
    public void appendDateInFixUTCTimeOnlyFormat(String tag, long millis);
    public void appendDateInFixUTCTimeOnlyFormat(char[] tag, TimeStruct timeStruct);
    public void appendDateInFixUTCTimeOnlyFormat(String tag, TimeStruct timeStruct);
    public void appendDateInFixUTCTimeStampFormat(char[] tag, DateStruct dateStruct, TimeStruct timeStruct);
    public void appendDateInFixUTCTimeStampFormat(String tag, DateStruct dateStruct, TimeStruct timeStruct);
    public void appendDateInFixUTCTimeStampFormat(char[] tag, long millis);
    public void appendDateInFixUTCTimeStampFormat(String tag, long millis);
    public void appendDateInFixLocalMktDateFormat(char[] tag, DateStruct dateStruct);
    public void appendDateInFixLocalMktDateFormat(String tag, DateStruct dateStruct);
    public void appendDateInFixLocalMktDateFormat(char[] tag, long millis);
    public void appendDateInFixLocalMktDateFormat(String tag, long millis);
    public void insert(String tag, byte value);
    public void insert(String tag, char value);
    public void insert(String tag, int value);
    public void insert(String tag, String value);
    public void insert(char[] tag, String value);
    public void insert(char[] tag, byte value);
    public void insert(char[] tag, char[] value);
    public void insert(char[] tag, char value);
    public void insert(char[] tag, int value);
    public void replace(int offset, int length, char[] value);
    public void replace(int offset, int length, String value);
}
