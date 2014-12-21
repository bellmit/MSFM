package com.cboe.cfix.util;

/**
 * PrettyPrintWriterVisitorIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

public interface PrettyPrintWriterVisitorIF
{
    public void accept(PrettyPrintWriterIF prettyPrintWriter);
}
