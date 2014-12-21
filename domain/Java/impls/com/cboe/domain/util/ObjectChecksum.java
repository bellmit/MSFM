//
// -----------------------------------------------------------------------------------
// Source file: ObjectChecksum.java
//
// PACKAGE: com.cboe.domain.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.util;

import java.util.zip.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;

/**
 * Determine the checksum of Object's.
 */
public class ObjectChecksum
{
    public static final Checksum DEFAULT_CHECKSUM = new CRC32();

    /**
     * Determine the checksum of the passed object using the CRC32 checksum algorithm
     * @param object to get checksum for
     * @return checksum
     * @exception IOException can occur when converting Object to stream for checksum calc
     */
    public static long calculateChecksum(Object object) throws IOException
    {
        return calculateChecksum(object, DEFAULT_CHECKSUM);
    }

    /**
     * Determine the checksum of the passed object using the checksum algorithm passed
     * @param object to get checksum for
     * @param checksum algorithm to use
     * @return checksum
     * @exception IOException can occur when converting Object to stream for checksum calc
     */
    public static long calculateChecksum(Object object, Checksum checksum) throws IOException
    {
        long result = 0;

        CheckedOutputStream checkedStream = new CheckedOutputStream(new ByteArrayOutputStream(), checksum);
        ObjectOutputStream objectStream = new ObjectOutputStream(checkedStream);

        try
        {
            objectStream.writeObject(object);
            objectStream.flush();

            result = checkedStream.getChecksum().getValue();
        }
        finally
        {
            objectStream.close();
            checkedStream.close();
        }

        return result;
    }
}
