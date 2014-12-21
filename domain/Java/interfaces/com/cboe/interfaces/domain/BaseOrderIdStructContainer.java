package com.cboe.interfaces.domain;

/**
 * Common interface for creating and managing OrderIdStruct related
 * hash containers.
 */
public interface BaseOrderIdStructContainer
{

    /**
     * create a hash code for the given object
     */
    public int hashCode();

    /**
     * provides comparison for hash indexing
     */
    public boolean equals( Object obj );

    /**
     * provides test for valid struct container components
     */
    public boolean isValid();

    /**
     * provide a method to produce a printable string of contents
     */
    public String toString();


}
