package com.cboe.cfix.fix.net;

/**
 * testFixFileBlaster.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;
import java.net.*;
import java.util.*;

import com.cboe.cfix.fix.fix42.generated.messages.*;
import com.cboe.cfix.fix.fix42.session.*;
import com.cboe.cfix.fix.parser.*;
import com.cboe.cfix.fix.util.*;
import com.cboe.cfix.interfaces.*;
import com.cboe.cfix.util.*;
import com.cboe.client.util.*;

/**
 * blasts fix lines from a file to the fix engine
 *
 */

public class testFixFileBlaster extends Thread
{
           int                      received;
           int                      prevReceived;
           String                   sender = null;
           Map                      senderReplacements;
           int[]                    slotArray  = new int[60*60*24*2]; // 2 days worth of seconds
           long                     lastTime   = 0;
    static int                      seqNum     = 0;
    static int                      skip       = 0;
    static int                      lines      = Integer.MAX_VALUE;
    static long                     startTime;
    static boolean                  ignoreComments = true;
    static boolean                  nooutput   = false;
    static boolean                  resetTime  = false;
    static boolean                  showlines  = false;
    static boolean                  timestamp  = false;
    static boolean                  totalOnly  = false;
    static int                      socketdebugflags = 0;
    static int                      debugFlags = 0;
    static int                      endPause   = 0;
    static int                      firstPause = 0;
    static int                      pause      = 0;
    static int                      port       = 50000;
    static int                      connectionNumber;
    static int                      timeit = 0;
    static List                     files      = new ArrayList();
    static Map                      replacements = new HashMap();
    static Set                      alwaysfiles = new HashSet(16);
    static String                   host       = "localhost";
    static testFixFileBlaster[]     connections;
    static boolean                  changed;
    static boolean                  validatingSocket = false;

    public testFixFileBlaster()
    {
        senderReplacements = replacements;
    }

    public testFixFileBlaster(String sender)
    {
        senderReplacements = new HashMap(replacements);

        this.sender = sender;

        senderReplacements.put("49", sender);
    }

    public void run()
    {
        try
        {
            String line = null;
            Socket socket = null;
            final FixSocketAdapter fixSocketAdapter = new FixSocketAdapter();
            int startFix;
            int endFix;

            fixSocketAdapter.setFixPacketParser(new FixPacketParser());

            if (!"none".equalsIgnoreCase(host))
            {
                while (true)
                {
                    try
                    {
                        socket = new Socket(host, port);
                        break;
                    }
                    catch (IOException ex)
                    {
                        ThreadHelper.sleepSeconds(1);
                    }
                }

                fixSocketAdapter.resetSocket(socket);

                fixSocketAdapter.setDebugFlags(socketdebugflags);

                final int localEndPause = endPause;
                final int localDebugFlags = debugFlags;
                final int localTimeit = timeit;
                final boolean localNooutput = nooutput;

                new Thread(new Runnable()
                {
                    public void run()
                    {
                        FixPacketIF fixPacket;
                        FixMessageFactory fixMessageFactory = new FixMessageFactory();
                        String fixMessage;
//                        PackedIntArrayIF  foundErrors       = new GrowableIntArray();
                        InputStream istream = fixSocketAdapter.getSocketInputStream();
                        int readByte;
                        int count;
                        char[] buf = new char[4096];

                        while (true)
                        {
                            try
                            {
                                if (validatingSocket)
                                {
                                    fixPacket = fixSocketAdapter.read();

                                    if (fixPacket.isMessageDisconnected())
                                    {
                                        System.out.println("PEER DISCONNECTED");

                                        if (localEndPause > 0)
                                        {
                                            Thread.sleep(localEndPause);
                                        }

                                        System.exit(0);
                                    }

                                    fixMessage = fixPacket.getArrayAsString();
                                }
                                else
                                {
                                    fixMessage = null;
                                    count      = 0;

                                    while (true)
                                    {
                                        readByte = istream.read();
                                        if (readByte == -1)
                                        {
                                            System.out.println("PEER DISCONNECTED");

                                            if (localEndPause > 0)
                                            {
                                                Thread.sleep(localEndPause);
                                            }

                                            System.exit(0);
                                        }

                                        if (readByte == 0)
                                        {
                                            continue;
                                        }

                                        if (count == buf.length)
                                        {
                                            char newbuf[] = new char[Math.max(buf.length << 1, count + 1)];
                                            System.arraycopy(buf, 0, newbuf, 0, count);
                                            buf = newbuf;

                                            if (buf.length > 1024 * 100)
                                            {
                                                System.out.println("Message Over 100K -- EXITING");
                                                System.exit(1);
                                            }
                                        }

                                        buf[count] = (char) readByte;

                                        if (count > 6)
                                        {
                                            if (buf[count - 7] == FixFieldIF.SOHchar &&
                                                buf[count - 6] == '1' &&
                                                buf[count - 5] == '0' &&
                                                buf[count - 4] == '=' &&
                                                Character.isDigit(buf[count - 3]) &&
                                                Character.isDigit(buf[count - 2]) &&
                                                Character.isDigit(buf[count - 1]) &&
                                                buf[count] == FixFieldIF.SOHchar)
                                            {
                                                fixMessage = new String(buf, 0, count + 1);
                                                break;
                                            }
                                        }

                                        count++;
                                    }
                                }

                                changed = true;

                                received++;

                                if (localTimeit > 0)
                                {
                                    if (startTime == 0)
                                    {
                                        synchronized(testFixFileBlaster.class)
                                        {
                                            if (startTime == 0)
                                            {
                                                startTime = System.currentTimeMillis();
                                            }
                                        }
                                    }

                                    lastTime = System.currentTimeMillis();

                                    slotArray[(int) (lastTime - startTime) / 1000]++;
                                }

                                if (!localNooutput)
                                {
                                    if (localDebugFlags == 0)
                                    {
                                        System.out.println("RCVD [" + fixMessage + "]");
                                    }
                                    else
                                    {
                                        FixSessionDebugger.dumpFixMessage("RCVD ", fixMessage, fixMessageFactory, localDebugFlags);
                                    }
                                }

//                                if (fixPacket.getArray()[fixPacket.getValueOffset(2)] == FixTestRequestMessage.MsgTypeAsChar)
//                                {
//                                    foundErrors.clear();
//                                    FixTestRequestMessage fixTestRequestMessage = (FixTestRequestMessage) fixMessageFactory.createFixTestRequestMessage();
//                                    fixTestRequestMessage.build(fixPacket, foundErrors, FixMessageIF.VALIDATE_ONLY_USED_FIELDS);
//                          TODO: finish, but how to respond if I don't know what sequence# to use -- think about it later
//                                }

                            }
                            catch (Exception ex)
                            {

                            }
                        }
                    }
                }).start();
            }

            int localLines = lines;
            int localSeqNum = seqNum;

            for (Iterator iterator = files.iterator(); iterator.hasNext();)
            {
                String filename = (String) iterator.next();

                boolean ignoreSkips = false;

                if (alwaysfiles.contains(filename))
                {
                    ignoreSkips = true;
                }

                BufferedReader reader;

                if ("stdin".equalsIgnoreCase(filename))
                {
                    reader = new BufferedReader(new InputStreamReader(System.in));
                }
                else
                {
                    reader = new BufferedReader(new FileReader(filename));
                }

                for (int i = 0; (line = reader.readLine()) != null;)
                {
                    if (showlines)
                    {
                        System.out.println(line);
                    }

                    if (ignoreComments && line.startsWith("#"))
                    {
                        continue;
                    }

                    startFix = line.indexOf("8=FIX.4.2" + FixFieldIF.SOH);
                    endFix = line.indexOf(FixFieldIF.SOH + "10=");

                    if (startFix >= 0 && endFix > startFix)
                    {
                        i++;

                        if (!ignoreSkips && skip >= i)
                        {
                            continue;
                        }

                        if (!ignoreSkips && localLines <= 0)
                        {
                            break;
                        }

                        line = line.substring(startFix, endFix + FixFieldIF.FIX_TAG_10_LENGTH + FixFieldIF.FIX_SOH_LENGTH);

                        if (localSeqNum > 0)
                        {
                            startFix = line.indexOf(FixFieldIF.SOH + "34=");
                            endFix = line.indexOf(FixFieldIF.SOH, startFix + FixFieldIF.FIX_SOH_LENGTH);
                            line = line.substring(0, startFix + 4) + localSeqNum + line.substring(endFix);

                            localSeqNum++;
                        }

                        if (resetTime)
                        {
                            startFix = line.indexOf(FixFieldIF.SOH + "52=");
                            endFix = line.indexOf(FixFieldIF.SOH, startFix + FixFieldIF.FIX_SOH_LENGTH);
                            line = line.substring(0, startFix + 4) + DateHelper.stringizeDateInFixUTCTimeStampFormat() + line.substring(endFix);
                        }

                        if (!senderReplacements.isEmpty())
                        {
                            Map.Entry entry;

                            for (Iterator iter = senderReplacements.entrySet().iterator(); iter.hasNext(); )
                            {
                                entry = (Map.Entry) iter.next();

                                startFix = line.indexOf(FixFieldIF.SOH + entry.getKey() + "=");
                                if (startFix >= 0)
                                {
                                    endFix = line.indexOf(FixFieldIF.SOH, startFix + FixFieldIF.FIX_SOH_LENGTH);
                                    line = line.substring(0, startFix + ((String) entry.getKey()).length() + 2) + entry.getValue() + line.substring(endFix);
                                }
                            }
                        }

                        startFix = line.indexOf(FixFieldIF.SOH + "9=");
                        endFix = line.indexOf(FixFieldIF.SOH, startFix + FixFieldIF.FIX_SOH_LENGTH);
                        line = line.substring(0, startFix + FixFieldIF.FIX_TAG_9_EMPTY_LENGTH) + (line.length() - endFix - FixFieldIF.FIX_SOH_LENGTH - FixFieldIF.FIX_TAG_10_LENGTH) + line.substring(endFix);

                        startFix = line.indexOf(FixFieldIF.SOH + "10=");
                        endFix = line.indexOf(FixFieldIF.SOH, startFix + FixFieldIF.FIX_SOH_LENGTH);
                        line = line.substring(0, startFix + 4) + FixChecksumHelper.calculateFixChecksumToString(line.substring(0, startFix + FixFieldIF.FIX_SOH_LENGTH)) + FixFieldIF.SOH;

                        if (!ignoreSkips)
                        {
                            localLines--;
                        }

                        if (!"none".equalsIgnoreCase(host))
                        {
                            fixSocketAdapter.write(line);
                        }
                        else
                        {
                            System.out.println(line);
                        }

                        if (i == 0 && firstPause > 0)
                        {
                            Thread.sleep(firstPause);
                            continue;
                        }

                        if (pause > 0)
                        {
                            Thread.sleep(pause);
                        }
                    }
                }

                reader.close();
            }
        }
        catch (SocketException ex)
        {

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void Usage()
    {
        System.out.println("Usage:");
        System.out.println(" [-stdin]                     \"send from stdin instead of from file\"");
        System.out.println(" [-file FILE1] [-file FILE2]  \"file(s) to send\"");
        System.out.println(" [-host 'host']               \"host to connect to\" -default: localhost");
        System.out.println(" [-port #]                    \"port to connect to\" -default: 50000");
        System.out.println(" [-screenonly]                \"sends messages to screen only\"  -default send the lines to the host only");
        System.out.println(" [-firstpause MILLISECONDS]   \"pauses after the first message\" -default no pausing");
        System.out.println(" [-pause MILLISECONDS]        \"pauses after the every sent message\" -default no pausing");
        System.out.println(" [-endpause MILLISECONDS]     \"pauses after the server disconnects\" -default exits right away");
        System.out.println(" [-skip #]                    \"skips first # valid messages (not just lines)\" -default doesn't skip any messages");
        System.out.println(" [-lines #]                   \"sends no more than # messages\" -default sends all messages in the file");
        System.out.println(" [-resequence #]              \"changes the sequence numbers of all messages to start at #\" -default uses the sequence numbers from the file");
        System.out.println(" [-sender SENDER]             \"changes the SenderCompID of all messages\" -default doesn't");
        System.out.println(" [-target TARGET]             \"changes the TargetCompID of all messages\" -default doesn't");
        System.out.println(" [-session SESSION]           \"changes the TradingSessionID of any MarketDataRequest message\" -default doesn't");
        System.out.println(" [-user USER:PWD]             \"changes the SenderSubID of any Logon messages\" -default doesn't");
        System.out.println(" [-resettime]                 \"changes the SendingTime of all messages\" -default doesn't");
        System.out.println(" [-nocomments]                \"doesn't treat as comments lines that start with '#'\" -default treats lines starting with '#' as comments");
        System.out.println(" [-showlines]                 \"show lines as they are read from the file\" -default doesn't");
        System.out.println(" [-replace TAG# VALUE]        \"replaces FIX TAG# value with VALUE\" -default doesn't");
        System.out.println(" [-socketdebugflags #]        \"flags passed in to the socket\" -default 0");
        System.out.println(" [-debugFlags #]              \"decodes output\" -default doesn't");
        System.out.println(" [-timeit #]                  \"shows a summary to System.err every # millis\" -default doesn't");
        System.out.println(" [-totalonly]                 \"only show the totals for received messages\" -default show # received per second");
        System.out.println(" [-nooutput]                  \"turn off any output on received messages\" -default show");
        System.out.println(" [-multisender SENDER]        \"changes the SenderCompID of all messages (and creates a threaded connection)\" -default doesn't");
        System.out.println(" [-alwaysfile]                \"ignore 'skip' and 'lines' commands for specified file\"");
        System.out.println(" [-timestamp]                 \"show all output timestamped\"");
        System.out.println(" [-validatingsocket]          \"use the same socket reader as CFIX to validate received messages\"");

        System.exit(0);
    }

    public static void main(String[] args) throws Exception
    {
        Set multiSenders = new HashSet(128);
        List arguments    = new ArrayList(128);
        boolean inMultiSender = false;
        int i;

        for (i = 0; i < args.length; i++)
        {
            try
            {
                if (args[i].equalsIgnoreCase("-multisender"))
                {
                    inMultiSender = true;
                    multiSenders.add(args[++i]);
                    continue;
                }

                if (inMultiSender)
                {
                    if (args[i].startsWith("-"))
                    {
                        inMultiSender = false;
                        i--;
                        continue;
                    }

                    multiSenders.add(args[i]);
                    continue;
                }

                arguments.add(args[i]);
            }
            catch (Exception ex)
            {
                Usage();
            }
        }

        if (arguments.size() > 0)
        {
            args = (String[]) arguments.toArray(new String[arguments.size()]);
        }

        for (i = 0; i < args.length; i++)
        {
            try
            {
                if (args[i].equalsIgnoreCase("-host"))
                {
                    if (!"none".equalsIgnoreCase(host))
                    {
                        testFixFileBlaster.host = args[++i];
                    }
                }
                else if (args[i].equalsIgnoreCase("-screenonly"))
                {
                    testFixFileBlaster.host = "none";
                }
                else if (args[i].equalsIgnoreCase("-timeit"))
                {
                    testFixFileBlaster.timeit = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-nocomments"))
                {
                    testFixFileBlaster.ignoreComments = false;
                }
                else if (args[i].equalsIgnoreCase("-debugFlags"))
                {
                    testFixFileBlaster.debugFlags = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-socketdebugflags"))
                {
                    testFixFileBlaster.socketdebugflags = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-port"))
                {
                    testFixFileBlaster.port = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-showlines"))
                {
                    testFixFileBlaster.showlines = true;
                }
                else if (args[i].equalsIgnoreCase("-nooutput"))
                {
                    testFixFileBlaster.nooutput = true;
                }
                else if (args[i].equalsIgnoreCase("-validatingsocket"))
                {
                    testFixFileBlaster.validatingSocket = true;
                }
                else if (args[i].equalsIgnoreCase("-pause"))
                {
                    testFixFileBlaster.pause = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-skip"))
                {
                    testFixFileBlaster.skip = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-resequence"))
                {
                    testFixFileBlaster.seqNum = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-resettime"))
                {
                    testFixFileBlaster.resetTime = true;
                }
                else if (args[i].equalsIgnoreCase("-totalonly"))
                {
                    testFixFileBlaster.totalOnly = true;
                }
                else if (args[i].startsWith("-timestamp"))
                {
                    testFixFileBlaster.timestamp = true;
                }
                else if (args[i].equalsIgnoreCase("-sender"))
                {
                    multiSenders.add(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-user"))
                {
                    testFixFileBlaster.replacements.put("50", args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-session"))
                {
                    testFixFileBlaster.replacements.put("336", args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-target"))
                {
                    testFixFileBlaster.replacements.put("56", args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-firstpause"))
                {
                    testFixFileBlaster.firstPause = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-endpause"))
                {
                    testFixFileBlaster.endPause = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-lines"))
                {
                    testFixFileBlaster.lines = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-file"))
                {
                    testFixFileBlaster.files.add(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("-alwaysfile"))
                {
                    testFixFileBlaster.files.add(args[++i]);
                    testFixFileBlaster.alwaysfiles.add(args[i]);
                }
                else if (args[i].equalsIgnoreCase("-stdin"))
                {
                    testFixFileBlaster.files.add("stdin");
                }
                else if (args[i].equalsIgnoreCase("-replace"))
                {
                    testFixFileBlaster.replacements.put(args[i + 1], args[i + 2]);
                    i += 2;
                }
                else
                {
                    System.out.println();
                    System.out.println("Invalid Argument: " + args[i]);
                    System.out.println();

                    Usage();
                }
            }
            catch (Exception ex)
            {
                Usage();
            }
        }

        testFixFileBlaster testBlaster;

        if (files.isEmpty())
        {
            Usage();
        }

        if (!"none".equalsIgnoreCase(host) && (!totalOnly || timestamp))
        {
            System.setOut(new PrintTimeStampedStream(System.out, true, true));
        }

        System.setErr(System.out);

        if (multiSenders.size() > 0)
        {
            connections = new testFixFileBlaster[multiSenders.size()];

            i = 0;

            for (Iterator iterator = multiSenders.iterator(); iterator.hasNext(); i++)
            {
                testBlaster = new testFixFileBlaster((String) iterator.next());

                connections[i] = testBlaster;

                new Thread(testBlaster).start();
            }

            if (timeit > 0)
            {
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        int prevReceived = 0;
                        int highestTimeIndex;
                        testFixFileBlaster blaster;
						int highest = 0;
						int lowest = 1234567;
						int cur = 0;
						int numSecs = 0;
						int total5 = 0;
						int numSecs5 = 0;
						int received;
						int j;

                        while (true)
                        {
                            try
                            {
                                if (startTime > 0 && changed)
                                {
                                    changed = false;

                                    for (int x = 0; x < connections.length; x++)
                                    {
                                        blaster = connections[x];

                                        highest = 0;
                                        lowest = 1234567;
                                        cur = 0;
                                        numSecs = 0;
                                        total5 = 0;
                                        numSecs5 = 0;

                                        received     = blaster.received;
                                        prevReceived = blaster.prevReceived;

                                        highestTimeIndex = (int) (System.currentTimeMillis() - startTime) / 1000 - 1;

                                        for (j = 0; j <= highestTimeIndex; j++)
                                        {
                                            cur = blaster.slotArray[j];

                                            if (cur != 0)
                                            {
                                                if (!totalOnly)
                                                {
                                                    System.err.println("[" + j + "] " + cur);
                                                }

                                                if (cur > highest)
                                                {
                                                    highest = cur;
                                                }

                                                if (cur < lowest)
                                                {
                                                    lowest = cur;
                                                }

                                                numSecs++;
                                            }
                                        }

                                        System.err.print("[");
                                        DateHelper.makeHHMMSS(System.err, blaster.lastTime);
                                        System.err.print("]");
                                        System.err.print((blaster.sender != null ? blaster.sender + " " : "") + "TOTAL: " + received + " SINCELAST: " + (received - prevReceived) + " HIGHEST: " + highest);

                                        if (highestTimeIndex > 5)
                                        {
                                            for (j = highestTimeIndex; j >= 0 && numSecs5 < 5; j--)
                                            {
                                                cur = blaster.slotArray[j];
                                                if (cur != 0)
                                                {
                                                    numSecs5++;
                                                    total5 += cur;
                                                }
                                            }

                                            System.err.println(" AVG(LAST 5): " + ((float) (total5 / 5)));
                                        }
                                        else
                                        {
                                            System.err.println(" AVG(ALL): " + (float) (received / (numSecs == 0 ? 1 : numSecs)));
                                        }

                                        blaster.prevReceived = received;
                                    }
                                }
                            }
                            catch (Exception ex)
                            {
//                                System.err.println();
//                                System.err.println(ex);
//                                ex.printStackTrace(System.err);
//                                System.err.println();
                            }

                            ThreadHelper.sleep(startTime == 0 ? 300 : timeit);
                        }
                    }
                }).start();
            }

            Thread.sleep(1000);
        }
    }
}
