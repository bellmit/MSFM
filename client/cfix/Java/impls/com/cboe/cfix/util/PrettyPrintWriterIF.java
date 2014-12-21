package com.cboe.cfix.util;

/**
 * PrettyPrintWriterIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

public interface PrettyPrintWriterIF
{
    public StringBuffer getBuffer();
    public void setBuffer(StringBuffer stringBuffer);
    public int getIndentationAmount();
    public void setIndentationAmount(int indentationAmount);
    public PrettyPrintWriterIF incLevel();
    public PrettyPrintWriterIF decLevel();
    public PrettyPrintWriterIF startPrintingGroup(String string);
    public PrettyPrintWriterIF printGroupItem(Object object);
    public PrettyPrintWriterIF endPrintingGroup();
    public PrettyPrintWriterIF printItem(Object object);
}
