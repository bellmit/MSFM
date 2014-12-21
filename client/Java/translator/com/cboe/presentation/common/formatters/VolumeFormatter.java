//
// -----------------------------------------------------------------------------------
// Source file: Formatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import java.text.*;

import com.cboe.interfaces.presentation.common.formatters.VolumeFormatStrategy;
import com.cboe.presentation.common.formatters.Formatter;

/**
 * Responsible for formatting volumes.
 * @author Nick DePasquale
 */
class VolumeFormatter extends Formatter implements VolumeFormatStrategy
{
/**
 */
public VolumeFormatter()
{
    super();

    addStyle(STANDARD_VOLUME_NAME, STANDARD_VOLUME_DESCRIPTION);
    addStyle(TICKER_VOLUME_NAME, TICKER_VOLUME_DESCRIPTION);

    setDefaultStyle(STANDARD_VOLUME_NAME);
}
/**
 * Implements format definition from VolumeFormatStrategy
 */
public String format(int volume)
{
    return format(volume, getDefaultStyle());
}
/**
 * Implements format definition from VolumeFormatStrategy
 */
public String format(int volume, String styleName)
{
    validateStyle(styleName);
    String result = "";

    if(styleName.equals(STANDARD_VOLUME_NAME))
    {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(true);

        result = numberFormat.format(volume);
    }
    else if(styleName.equals(TICKER_VOLUME_NAME))
    {
        StringBuffer buffer = new StringBuffer(16);
        int value = volume % 100;
        // if a multiple of 100
        if(value == 0)
        {
            if(volume <= 100)
            {
                // nothing to append here
                // volString = "";
            }
            else if(volume < 1000)
            {
                buffer.append(Integer.toString(volume / 100)).append("s");
//                volString = volume / 100 + "s";
            }
            else if(volume < 10000)
            {
                buffer.append(Integer.toString(volume)).append("s");
//                volString = volume + "s";
            }

            else if(volume < 1000000)
            {
                buffer.append(Integer.toString(volume/1000)).append(".000s");
//                volString = volume/1000 + ".000s";
            }
            else
            {
                buffer.append(Integer.toString(volume)).append("s");
//                volString = volume + "s";
            }
        }
        else if(volume < 100)
        {
            buffer.append(Integer.toString(volume)).append(".s.");
//            volString = volume + ".s.";
        }
        else
        {
            buffer.append(Integer.toString(volume)).append("s");
//            volString = volume + "s";
        }
        result = buffer.toString();
    }
    return result;
}
}
