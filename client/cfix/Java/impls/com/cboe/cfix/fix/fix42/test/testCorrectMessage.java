package com.cboe.cfix.fix.fix42.test;

/**
 * testCorrectMessage.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;

import com.cboe.cfix.fix.fix42.generated.fields.*;
import com.cboe.cfix.fix.fix42.generated.messages.*;
import com.cboe.cfix.fix.util.*;
import com.cboe.cfix.interfaces.*;

/**
 *
 */

public class testCorrectMessage
{
    public static void main(String[] args)
    {
        System.setErr(System.out);
        String filename = (args.length == 0 ? "d:\\0" : args[0]);

        try
        {
            String line;
            int index35;
            int index10;
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            FixMessageFactoryIF fixMessageFactory = new FixMessageFactory();

            while ((line = reader.readLine()) != null)
            {
                index35 = line.indexOf(FixFieldIF.SOH + "35=");
                if (index35 < 0)
                {
                    if (line.startsWith("35="))
                    {
                        index35 = 0;
                    }
                    else
                    {
                        continue;
                    }
                }
                else
                {
                    index35++;
                }

                index10 = line.indexOf(FixFieldIF.SOH + "10=");
                if (index10 < 0)
                {
                    if (!line.endsWith(FixFieldIF.SOH))
                    {
                        line += FixFieldIF.SOH;
                    }

                    index10 = line.length();
                }
                else
                {
                    index10++;
                }

                line = line.substring(index35, index10);

                FixMessageBuilder fixMessageWriter = new FixMessageBuilder();

                fixMessageWriter.append(FixBeginStringField.TagIDAsChars,   fixMessageFactory.getFixVersionAsString());
                fixMessageWriter.append(FixBodyLengthField.TagIDAsChars,    line.length());

                fixMessageWriter.append(line);

                fixMessageWriter.append(FixCheckSumField.TagIDAsChars, FixChecksumHelper.calculateFixChecksumToString(fixMessageWriter.getFastCharacterWriter()));

                System.out.println(fixMessageWriter.toString());
            }

            reader.close();
        }
        catch (Exception ex)
        {
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
        }
    }
}
