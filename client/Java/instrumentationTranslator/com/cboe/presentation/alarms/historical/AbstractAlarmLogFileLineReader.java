//
// -----------------------------------------------------------------------------------
// Source file: AbstractAlarmLogFileLineReader.java
//
// PACKAGE: com.cboe.presentation.alarms.historical
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms.historical;

import java.io.IOException;
import java.io.Reader;

public abstract class AbstractAlarmLogFileLineReader
{
    protected static final int CHAR_READ_BUFFER_LENGTH = 2048;

    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    protected StringBuilder buffer;
    protected int charRead;
    protected int startPosition;

    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    private Reader reader;

    protected final Object lockObject = new Object();

    private char[] charBuffer;

    protected AbstractAlarmLogFileLineReader(Reader reader)
    {
        if(reader == null)
        {
            throw new IllegalArgumentException("reader may not be null.");
        }
        this.reader = reader;
        buffer = new StringBuilder(CHAR_READ_BUFFER_LENGTH * 2);
        charBuffer = new char[CHAR_READ_BUFFER_LENGTH];
        charRead = 0;
        startPosition = 0;
    }

    /**
     * Read a line of text. A line is determined from logic based on how
     * the Alarm Log File is written.
     * @return A String containing the contents of the line, not including
     * any line-termination characters, or null if the end of the
     * stream has been reached
     * @exception java.io.IOException  If an I/O error occurs
     */
    public abstract String readLine() throws IOException;

    /**
     * Close the stream. Will forward to the contained reader.
     * @exception java.io.IOException If an I/O error occurs
     */
    public void close() throws IOException
    {
        synchronized(lockObject)
        {
            if(reader != null)
            {
                reader.close();
            }
            reader = null;
        }
        buffer = null;
        charBuffer = null;
    }

    /**
     * Tell whether this reader is ready to be read.
     * @exception java.io.IOException  If an I/O error occurs
     */
    @SuppressWarnings({"NonPrivateFieldAccessedInSynchronizedContext"})
    public boolean isReady() throws IOException
    {
        synchronized(lockObject)
        {
            boolean isReady;

            ensureOpen();

            if(startPosition < buffer.length())
            {
                isReady = true;
            }
            else
            {
                isReady = reader.ready();
            }
            return isReady;
        }
    }

    protected void fillBuffer() throws IOException
    {
        if(isReady() && charRead > -1)
        {
            charRead = reader.read(charBuffer, 0, CHAR_READ_BUFFER_LENGTH);
            if(charRead > 0)
            {
                buffer.append(charBuffer, 0, charRead);
            }
        }
    }

    protected void ensureOpen() throws IOException
    {
        if(buffer == null)
        {
            throw new IOException("Stream closed");
        }
    }
}
