package com.cboe.ffUtil;

import java.io.*;

public class Util
{
    /**
     *  Add enough trailing spaces to fill padToLen chars.
     *  If trunctate is true, then str will be reduced to pasToLen chars if necessary.
     */
    public static void writePaddedString(Writer writer, String str, int padToLen, boolean truncate) throws IOException
    {
        if (truncate && str.length() > padToLen)
        {
            writer.write(str.substring(padToLen));
        }
        else
        {
            writer.write(str);
        }

        for (int i=str.length(); i < padToLen; i++)
        {
            writer.write(' ');
        }
    }
}
