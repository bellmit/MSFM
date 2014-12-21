package com.cboe.cfix.fix.parser;

/**
 * testFixPacketParser.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;

import com.cboe.cfix.interfaces.*;

/**
 * Can parse and validate a FIX line
 *
 */

public class testFixPacketParser
{
    public static void main(String[] args)
    {
        FixPacketParserIF parser = new FixPacketParser();
        FixPacketIF fixPacket;

        System.setErr(System.out);

        if (false)
        {
            String s = "8=FIX.4.29=9735=A34=149=WOLVERINE_CHI150=KJL:KJL52=20021223-12:01:3756=PFIX20257=PROD98=0108=50141=Y10=199";
            int readChecksum = 0;
            int to = s.length() - 7;
            for (int i = 0; i < to; i++)
            {
                readChecksum += s.charAt(i);
                System.out.println("Chksum[" + i + "] byte(" + (byte) s.charAt(i) + "/" + s.charAt(i) + ") chksum(" + readChecksum + ") chksum%256(" + (readChecksum % 256) + ")");
            }

            readChecksum %= 256;

            if (to != 10000000)
            {
                System.exit(0);
            }
        }

        if (args.length == 0)
        {
            String[] test =
            {
                "8=FIX.4.29=9135=A34=249=WOLVERINE_CHI150=KJL:KJL52=20021223-12:02:3056=PFIX20257=PROD98=0108=5010=143",
                "8=FIX.4.29=3135=A34=258======95=396=ABC10=230",
                "8=FIX.4.29=9135=A34=249=WOLVERINE_CHI150=KJL:KJL52=20021223-12:02:3056=PFIX20257=PROD98=0108=508=FIX.4.29=9135=A34=249=WOLVERINE_CHI150=KJL:KJL52=20021223-12:02:3056=PFIX20257=PROD98=0108=5010=143",
                "8=FIX.4.29=9235=A34=249=WOLVERINE_CHI150=KJL:KJL52=20021223-12:02:3056=PFIX20257=PROD98=0108=5010=143",
                "8=FIX.4.29=9135=A34=249=WOLVERINE_CHI150=KJL:KJL52=20021223-12:02:3056=PFIX20257=PROD98=0108=5010=142",
                "8=FIX.4.29=9135=A34=249=WOLVERINE_CHI150=KJL:KJL52=20021223-12:02:3056=PFIX20257=PROD98=0108=5011=142",
                null
            };

            for (int i = 0; i < test.length - 1; i++)
            {
                try
                {
                    fixPacket = parser.parse(new StringBufferInputStream(test[i]), FixPacketParser.SKIP_GARBAGE_PREFIX | FixPacketParser.MAXIMUM_MATCH, FixSessionDebugIF.DEBUG_ALL);

                    if (fixPacket.isGoodFixMessage())
                    {
                        System.out.println("FixPacket[" + i + "] " + fixPacket);
                    }
                    else
                    {
                        System.out.println("FixPacket[" + i + "] " + fixPacket);
                    }
                }
                catch (Exception ex)
                {
                    System.out.println("Exception[" + i + "] " + ex);
                    ex.printStackTrace();
                }
            }

            return;
        }

        int  linesWithFixMessages = 0;
        int  linesWithValidFixMessages = 0;
        int  totalLines = 0;
        long startTime = System.currentTimeMillis();

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String         line;

            for (totalLines = 0; (line = reader.readLine()) != null; totalLines++)
            {
//                System.out.println("Processing Line[" + (totalLines+1) + "]");
                if (line.length() < 5)
                {
                    continue;
                }

                try
                {
                    fixPacket = parser.parse(new StringBufferInputStream(line), FixPacketParser.SKIP_GARBAGE_PREFIX | FixPacketParser.MAXIMUM_MATCH, FixSessionDebugIF.DEBUG_ALL);

                    if (fixPacket.isBadFixMessage())
                    {
                        linesWithFixMessages++;
                    }

                    if (fixPacket.isGoodFixMessage())
                    {
                        linesWithValidFixMessages++;
                    }

                    if (!fixPacket.isGoodFixMessage() && !fixPacket.isGarbageMessage())
                    {
                        System.out.println();
                        System.out.println();
                        System.out.println("BEGIN_FixPacket[" + (totalLines+1) + "] ");
                        System.out.println(fixPacket);
                        System.out.println(line);
                        System.out.println();
                        System.out.println("END_FixPacket[" + (totalLines+1) + "]");
                    }
                }
                catch (Exception ex)
                {
                    System.out.println("Exception[" + (totalLines+1) + "] " + ex);
                    ex.printStackTrace();
                    System.exit(0);
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
        }

        long millis  = System.currentTimeMillis() - startTime;

        System.out.println();
        System.out.println("Summary");
        System.out.println("TotalLines=" + totalLines);
        System.out.println("LinesWithFixMessages=" + linesWithFixMessages);
        System.out.println("LinesWithValidFixMessages=" + linesWithValidFixMessages);
        System.out.println("ElapsedTime=TotalMillis[" + millis + "]  == Hours[" + (millis / (1000*60*60)) + "] Minutes[" + ((millis / (1000*60*60))%60000) + "] Seconds[" + ((millis / (1000))%60) + "] Millis[" + (millis % 1000) + "]");
        System.out.println("Messages/Second=" + (int) ((linesWithFixMessages * 1000.0) / millis));
    }
}
