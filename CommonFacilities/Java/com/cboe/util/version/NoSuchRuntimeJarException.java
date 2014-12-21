//
// -----------------------------------------------------------------------------------
// Source file: NoSuchRuntimeJarException.java
//
// PACKAGE: com.cboe.util.version
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.util.version;

/*
 * Designates that the jar runtime did not exist.
 */
public class NoSuchRuntimeJarException extends Exception
{
    private String jarName;

    /**
     * Constructs an <code>Exception</code> with no specified detail message.
     */
    public NoSuchRuntimeJarException()
    {
    }

    /**
     * Constructs an <code>Exception</code> with the specified detail message.
     * @param detailMessage
     */
    public NoSuchRuntimeJarException(String detailMessage)
    {
        super(detailMessage);
    }

    /**
     * Constructs an <code>Exception</code> with the specified detail message.
     * @param detailMessage
     * @param jarName that does not exist in runtime
     */
    public NoSuchRuntimeJarException(String detailMessage, String jarName)
    {
        this(detailMessage);
        this.jarName = jarName;
    }

    /**
     * Gets the jarName that did not exist in the runtime
     * @return jarName that does not exist in runtime
     */
    public String getJarName()
    {
        return jarName;
    }
}
