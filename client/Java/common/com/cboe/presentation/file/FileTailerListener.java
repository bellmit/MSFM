//
//-----------------------------------------------------------------------------------
//Source file: FileTailerListener.java
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.presentation.file;

/**
 *  
 */
public interface FileTailerListener
{
    /**
     * Print the supplied String.
     * 
     * @param s - String to be printed
     */
    public void print(String s);

    /**
     * Clear all previous text.
     */
    public void clear();
}