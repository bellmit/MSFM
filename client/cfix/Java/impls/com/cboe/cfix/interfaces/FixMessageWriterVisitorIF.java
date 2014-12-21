package com.cboe.cfix.interfaces;

/**
 * FixMessageWriterVisitorIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

public interface FixMessageWriterVisitorIF
{
    public void accept(FixMessageBuilderIF writer) throws Exception;
}
