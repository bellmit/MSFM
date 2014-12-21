package com.cboe.interfaces.presentation.common.logging;

/**
 * This interface defines IGUILoggerProperty.
 */
public interface IGUILoggerProperty
{
    public int getKey();
    public String getName();
    public int hashCode();
    public boolean equals(Object property);
//    public IGUILoggerProperty[] getProperties();
}