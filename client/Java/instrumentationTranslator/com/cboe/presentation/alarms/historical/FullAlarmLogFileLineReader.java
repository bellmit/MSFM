//
// -----------------------------------------------------------------------------------
// Source file: FullAlarmLogFileLineReader.java
//
// PACKAGE: com.cboe.presentation.alarms.historical
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms.historical;

import java.io.IOException;
import java.io.Reader;

import com.cboe.interfaces.domain.Delimeter;

public class FullAlarmLogFileLineReader extends AbstractAlarmLogFileLineReader
{
    private static final String LINE_SEPARATOR = "\nlow debug ";
    private static final String FIRST_QUOTE_MARKER = " \"";
    private static final String EOL_MARKER = Delimeter.PROPERTY_DELIMETER + "\" ";

    public FullAlarmLogFileLineReader(Reader reader)
    {
        super(reader);
    }

    /**
     * Read a line of text. A line is determined from logic based on how
     * the Alarm Log File is written.
     * @return A String containing the contents of the line, not including
     * any line-termination characters, or null if the end of the
     * stream has been reached
     * @exception IOException  If an I/O error occurs
     */
    @SuppressWarnings({"NonPrivateFieldAccessedInSynchronizedContext"})
    public String readLine() throws IOException
    {
        synchronized(lockObject)
        {
            String aLine = null;

            ensureOpen();

            if(buffer.length() == 0)
            {
                fillBuffer();
                startPosition = 0;
            }

            boolean isFound = false;
            while(!isFound && charRead != -1)
            {
                int nextEOLMarker = buffer.indexOf(LINE_SEPARATOR, startPosition);
                if(nextEOLMarker == -1 && charRead < CHAR_READ_BUFFER_LENGTH)
                {
                    nextEOLMarker = buffer.indexOf(EOL_MARKER, startPosition);
                }
                if(nextEOLMarker > -1)
                {
                    int firstQuotePos = buffer.indexOf(FIRST_QUOTE_MARKER, startPosition);

                    aLine = buffer.substring(firstQuotePos + 2, nextEOLMarker + 1);
                    isFound = true;

                    startPosition = nextEOLMarker + 2;
                }
                else
                {
                    StringBuilder newBuffer =
                            new StringBuilder(CHAR_READ_BUFFER_LENGTH * 2);
                    newBuffer.append(buffer.substring(startPosition, buffer.length()));
                    buffer = newBuffer;
                    startPosition = 0;
                    fillBuffer();
                }
            }

            return aLine;
        }
    }
}
