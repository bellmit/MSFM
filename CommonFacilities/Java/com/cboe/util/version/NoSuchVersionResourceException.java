//
// -----------------------------------------------------------------------------------
// Source file: NoSuchVersionResourceException.java
//
// PACKAGE: com.cboe.util.version
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.util.version;

/*
 * Designates that the resource for version info in the jar did not exist.
 */
public class NoSuchVersionResourceException extends Exception
{
    private String jarName;

    /**
     * Constructs an <code>Exception</code> with no specified detail message.
     */
    public NoSuchVersionResourceException()
    {
    }

    /**
     * Constructs an <code>Exception</code> with the specified detail message.
     * @param detailMessage
     */
    public NoSuchVersionResourceException(String detailMessage)
    {
        super(detailMessage);
    }

    /**
     * Constructs an <code>Exception</code> with the specified detail message.
     * @param detailMessage
     * @param jarName that does not contain a resource with version information
     */
    public NoSuchVersionResourceException(String detailMessage, String jarName)
    {
        this(detailMessage);
        this.jarName = jarName;
    }

    /**
     * Gets the jarName that does not contain a resource with version information
     * @return jarName that does not contain a resource with version information
     */
    public String getJarName()
    {
        return jarName;
    }
}
