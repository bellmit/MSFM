package com.cboe.cfix.fix.fix42.test;

/**
 * testValidateMessage.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;
import java.util.*;

import com.cboe.cfix.fix.fix42.generated.messages.*;
import com.cboe.cfix.fix.fix42.session.*;
import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.*;

/**
 * validates messages from a file
 *
 */

public class testValidateMessage
{
    public static void main(String[] args)
    {
        System.setErr(System.out);

        int  lines = Integer.MAX_VALUE;
        List files = new ArrayList();
        int  debugFlags = 0;
        int  skip = 0;

        if (args.length == 0)
        {
            Usage();
        }

        for (int i = 0; i < args.length; i++)
        {
            try
            {
                if (args[i].equals("-lines"))
                {
                    lines = Integer.parseInt(args[++i]);
                }
                else if (args[i].equals("-file"))
                {
                    files.add(args[++i]);
                }
                else if (args[i].equals("-skip"))
                {
                    skip = Integer.parseInt(args[++i]);
                }
                else if (args[i].equals("-show"))
                {
                    i++;

                    if (args[i].startsWith("predecode"))
                    {
                        debugFlags |= FixSessionDebugIF.SESSION_SHOW_PREDECODE_MESSAGE_DATA;
                    }
                    else if (args[i].startsWith("transition"))
                    {
                        debugFlags |= FixSessionDebugIF.MESSAGE_SHOW_BUILD_TRANSITIONS;
                    }
                }
                else
                {
                    Usage();
                }
            }
            catch (Exception ex)
            {
                System.out.println("Exception: " + ExceptionHelper.getStackTrace(ex));
            }
        }

        try
        {
            if (!files.isEmpty())
            {
                String line = null;
                String message;
                int startFix;
                int endFix;

                FixMessageFactoryIF fixMessageFactory = new FixMessageFactory();

                for (Iterator iterator = files.iterator(); iterator.hasNext(); )
                {
                    String filename = (String) iterator.next();

                    BufferedReader reader = new BufferedReader(new FileReader(filename));

                    for (int i = 0; (line = reader.readLine()) != null;)
                    {
                       startFix = line.indexOf("8=FIX.4.2" + FixFieldIF.SOH);
                       endFix   = line.indexOf(FixFieldIF.SOH + "10=");

                       if (startFix >= 0 && endFix > startFix)
                       {
                            i++;

                            if (skip >= i)
                            {
                                continue;
                            }

                            if (lines-- <= 0)
                            {
                                break;
                            }

                            message = line.substring(startFix, endFix + FixFieldIF.FIX_TAG_10_LENGTH + 1);
                            System.out.println("MESSAGE_START " + i);
                            System.out.println();
                            System.out.println("FIX '" + message + "'");
                            System.out.println();
                            FixSessionDebugger.dumpFixMessage("DECODED ", message, fixMessageFactory, FixSessionDebugIF.SESSION_DECODE_SENT_MESSAGES | debugFlags);
                            System.out.println();
                            System.out.println("MESSAGE_END " + i);
                            System.out.println();
                       }
                    }

                    reader.close();
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
        }
    }

    public static void Usage()
    {
        System.out.println("Usage: [-file FILE1] [-file FILE2...] [-lines #] [-show predecode] [-show transitions] [skip #]");
        System.exit(0);
    }
}
