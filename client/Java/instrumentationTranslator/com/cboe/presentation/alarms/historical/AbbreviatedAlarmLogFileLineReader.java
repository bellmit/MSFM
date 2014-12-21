//
// -----------------------------------------------------------------------------------
// Source file: AbbreviatedAlarmLogFileLineReader.java
//
// PACKAGE: com.cboe.presentation.alarms.historical
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms.historical;

import java.io.IOException;
import java.io.Reader;

public class AbbreviatedAlarmLogFileLineReader extends AbstractAlarmLogFileLineReader
{
    public static final String EOL_MARKER = "\u0002";
    public static final String EOL_MARKER_WITH_NEWLINE = EOL_MARKER + '\n';
    private static final int EOL_MARKER_LENGTH = EOL_MARKER_WITH_NEWLINE.length();

    public AbbreviatedAlarmLogFileLineReader(Reader reader)
    {
        super(reader);
    }

    /**
     * Read a line of text. A line is determined from logic based on how
     * the Alarm Log File is written.
     * @return A String containing the contents of the line, not including
     * any line-termination characters, or null if the end of the
     * stream has been reached
     * @exception java.io.IOException  If an I/O error occurs
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
                int nextEOLMarker = buffer.indexOf(EOL_MARKER_WITH_NEWLINE, startPosition);
                if(nextEOLMarker == -1 && charRead < CHAR_READ_BUFFER_LENGTH)
                {
                    nextEOLMarker = buffer.indexOf(EOL_MARKER, startPosition);
                }
                if(nextEOLMarker > -1)
                {
                    aLine = buffer.substring(startPosition, nextEOLMarker + 2);
                    isFound = true;

                    startPosition = nextEOLMarker + EOL_MARKER_LENGTH;
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
