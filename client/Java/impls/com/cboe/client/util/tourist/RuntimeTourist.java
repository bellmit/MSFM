package com.cboe.client.util.tourist;

/**
 * RuntimeTourist.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Tourist Parameters: what=memory|threads|gc|debug|dumpgc|process|pargs|systemProperties
 *
 */

import java.io.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.*;

import com.cboe.client.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public final class RuntimeTourist extends AbstractTourist
{
    protected String[] mandatoryKeys = new String[]{"what"};

    public String[] getMandatoryKeys()
    {
        return mandatoryKeys;
    }

    public Writer visit(Writer writer) throws Exception
    {
        String what = getValue("what");

        writer.write("<runtime>");

        if ("memory".equals(what))
        {
            int nRepeat = IntegerHelper.parseInt(getValue("repeat"));
            if (nRepeat == IntegerHelper.INVALID_VALUE)
            {
                nRepeat = 0;
            }

            int sleepSeconds = IntegerHelper.parseInt(getValue("sleepSeconds"));
            if (sleepSeconds == IntegerHelper.INVALID_VALUE)
            {
                sleepSeconds = 0;
            }

            if (nRepeat > 0 && sleepSeconds == 0)
            {
                sleepSeconds = 1;
            }

            StringBuilder sb = new StringBuilder(100);
            for (int i = 0; i <= nRepeat; i++)
            {
                long total = Runtime.getRuntime().totalMemory();
                long free  = Runtime.getRuntime().freeMemory();
                long used  = total - free;

                if (nRepeat == 0)
                {
                    sb.append("<memory used=\"").append(StringHelper.longToStringWithCommas(used))
                      .append("\" free=\"").append(StringHelper.longToStringWithCommas(free))
                      .append("\" total=\"").append(StringHelper.longToStringWithCommas(total))
                      .append("\"/>");
                    writer.write(sb.toString());
                }
                else
                {
                    sb.setLength(0);
                    sb.append("<memory used=\"").append(StringHelper.longToStringWithCommas(used))
                      .append("\" free=\"").append(StringHelper.longToStringWithCommas(free))
                      .append("\" total=\"").append(StringHelper.longToStringWithCommas(total))
                      .append("\" time=\"").append(DateHelper.stringizeDateInLogFormat())
                      .append("\"/>");
                    writer.write(sb.toString());
                }

                if (nRepeat > 0 && sleepSeconds > 0)
                {
                    ThreadHelper.sleepSeconds(sleepSeconds);
                }
            }
        }
        else if ("threads".equals(what))
        {
            ThreadHelper.dumpThreadGroups(writer, true);
        }
        else if ("systemProperties".equals(what))
        {
            Object key;

            Properties properties = System.getProperties();

            Map map = new TreeMap();

            for (Enumeration enumeration = properties.propertyNames(); enumeration.hasMoreElements(); )
            {
                key = enumeration.nextElement();

                map.put(key, properties.get(key));
            }

            Map.Entry mapEntry;

            StringBuilder sb = new StringBuilder(100);
            for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); )
            {
                mapEntry = (Map.Entry) iterator.next();

                sb.setLength(0);
                sb.append("<property key=\"").append(mapEntry.getKey())
                  .append("\" value=\"").append(mapEntry.getValue())
                  .append("\"/>");
                writer.write(sb.toString());
            }
        }
        else if ("process".equals(what))
        {
            byte[] bytes = new byte[128];
            DecimalFormat format = new DecimalFormat("0.00");

            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("/proc/self/psinfo"));
            int iBytesRead = bufferedInputStream.read(bytes);
            bufferedInputStream.close();

            if (iBytesRead < 128)
            {
                writer.write("<process action=\"can't get pid\"/>");
            }
            else
            {
                int pid = solarisU32(bytes, 8);
                int cpuSeconds = solarisU32(bytes, 72);

                StringBuilder sb = new StringBuilder(300);
                sb.append("<process action=\"read\"")
                  .append(" pid=\"").append(pid).append("\"")
                  .append(" startTime=\"").append(DateHelper.stringizeDateInLogFormat(solarisU64(bytes, 64) * 1000)).append("\"")
                  .append(" threadCount=\"").append(StringHelper.intToString(solarisU32(bytes, 4))).append("\"")
                  .append(" imageKilobytes=\"").append(StringHelper.intToString(solarisU32(bytes, 44))).append("\"")
                  .append(" residentKilobytes=\"").append(StringHelper.intToString(solarisU32(bytes, 48))).append("\"")
                  .append(" machineCpuPercent=\"").append(format.format((double) solarisU16(bytes, 60) / (1024 * 32) * 100.00F)).append("\"")
                  .append(" machineMemPercent=\"").append(format.format((double) solarisU16(bytes, 62) / (1024 * 32) * 100.00F)).append("\"")
                  .append(" totalCpuTime=\"").append(StringHelper.intToString(cpuSeconds / DateHelper.MINUTES_PER_HOUR / DateHelper.SECONDS_PER_MINUTE))
                      .append(":").append(StringHelper.intToString(cpuSeconds / DateHelper.MINUTES_PER_HOUR))
                      .append(":").append(StringHelper.intToString(cpuSeconds % DateHelper.SECONDS_PER_MINUTE)).append("\"")
                  .append("/>");
                writer.write(sb.toString());
            }
        }
        else if ("pargs".equals(what))
        {
            byte[] bytes = new byte[16];

            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("/proc/self/psinfo"));
            int iBytesRead = bufferedInputStream.read(bytes);
            bufferedInputStream.close();

            if (iBytesRead < 16)
            {
                writer.write("<process action=\"can't get pid\"/>");
            }
            else
            {
                int pid = solarisU32(bytes, 8);

                Process p = Runtime.getRuntime().exec("pargs " + pid);
                if (p.waitFor() == 0)
                {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    int i = 0;
                    StringBuilder sb = new StringBuilder(100);
                    while ((line = reader.readLine()) != null)
                    {
                        sb.setLength(0);
                        sb.append("<parg id=\"").append(++i).append("\" value=\"").append(line).append("\"/>");
                        writer.write(sb.toString());
                    }
                }
            }
        }
        else if ("dumpgc".equals(what))
        {
            byte[] bytes = new byte[16];

            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("/proc/self/psinfo"));
            int iBytesRead = bufferedInputStream.read(bytes);
            bufferedInputStream.close();

            if (iBytesRead < 16)
            {
                writer.write("<process action=\"can't get pid\"/>");
            }
            else
            {
                int pid = solarisU32(bytes, 8);

                StringBuilder sb = new StringBuilder(40);
                String title = getValue("title");
                if (title != null)
                {
                    sb.append("DUMPGC: ").append(title);
                    System.out.println(sb.toString());
                    sb.setLength(0);
                }

                sb.append("kill -3 ").append(pid);
                Process p = Runtime.getRuntime().exec(sb.toString());
                p.waitFor();
            }
        }
        else if ("gc".equals(what))
        {
            int sleepSeconds = 2;

            long total = Runtime.getRuntime().totalMemory();
            long free  = Runtime.getRuntime().freeMemory();
            long used  = total - free;

            StringBuilder sb = new StringBuilder(100);
            sb.append("<memory when=\"beforeGC\" used=\"").append(StringHelper.longToStringWithCommas(used))
              .append("\" free=\"").append(StringHelper.longToStringWithCommas(free))
              .append("\" total=\"").append(StringHelper.longToStringWithCommas(total))
              .append("\"/>");
            writer.write(sb.toString());

            System.gc();

            String sleep = getValue("sleepSeconds");

            if (sleep != null)
            {
                sleepSeconds = Integer.parseInt(sleep);
            }

            if (sleepSeconds < 0 || sleepSeconds > 60)
            {
                sleepSeconds = 2;
            }

            ThreadHelper.sleepSeconds(sleepSeconds);

            total = Runtime.getRuntime().totalMemory();
            free  = Runtime.getRuntime().freeMemory();
            used  = total - free;

            sb.setLength(0);
            sb.append("<memory when=\"afterGC\" used=\"").append(StringHelper.longToStringWithCommas(used))
              .append("\" free=\"").append(StringHelper.longToStringWithCommas(free)).append("\" total=\"")
              .append(StringHelper.longToStringWithCommas(total))
              .append("\"/>");
            writer.write(sb.toString());
        }
        else if ("debug".equals(what))
        {
            String command = getValue("command");

            if ("on".equals(command))
            {
                if (Log.isDebugOn())
                {
                    writer.write("<debug status=\"unchanged\"/>");
                }
                else
                {
                    Field field = Log.class.getDeclaredField("debugStatus");
                    boolean oldAccessible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(null, Boolean.TRUE);
                    field.setAccessible(oldAccessible);
                    writer.write("<debug status=\"on\"/>");
                }
            }
            else if ("off".equals(command))
            {
                if (!Log.isDebugOn())
                {
                    writer.write("<debug status=\"unchanged\"/>");
                }
                else
                {
                    Field field = Log.class.getDeclaredField("debugStatus");
                    boolean oldAccessible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(null, Boolean.FALSE);
                    field.setAccessible(oldAccessible);
                    writer.write("<debug status=\"off\"/>");
                }
            }
            else if ("view".equals(command))
            {
                StringBuilder sb = new StringBuilder(50);
                sb.append("<debug status=\"").append(Log.isDebugOn() ? "on" : "off").append("\"/>");
                writer.write(sb.toString());
            }
        }

        writer.write("</runtime>");

        return writer;
    }

    public static int solarisU8(byte[] bytes, int offset)
    {
        int byte1 = bytes[offset    ];
        if (byte1 < 0) byte1 += 256;

        return byte1;
    }

    public static int solarisU16(byte[] bytes, int offset)
    {
        int byte1 = bytes[offset    ];
        if (byte1 < 0) byte1 += 256;

        int byte2 = bytes[offset + 1];
        if (byte2 < 0) byte2 += 256;

        return (byte1 << 8) + byte2;
    }

    public static int solarisU32(byte[] bytes, int offset)
    {
        int byte1 = bytes[offset    ];
        if (byte1 < 0) byte1 += 256;

        int byte2 = bytes[offset + 1];
        if (byte2 < 0) byte2 += 256;

        int byte3 = bytes[offset + 2];
        if (byte3 < 0) byte3 += 256;

        int byte4 = bytes[offset + 3];
        if (byte4 < 0) byte4 += 256;

        return (byte1 << 24) + (byte2 << 16) + (byte3 << 8) + byte4;
    }

    public static long solarisU64(byte[] bytes, int offset)
    {
        int byte1 = bytes[offset    ];
        if (byte1 < 0) byte1 += 256;

        int byte2 = bytes[offset + 1];
        if (byte2 < 0) byte2 += 256;

        int byte3 = bytes[offset + 2];
        if (byte3 < 0) byte3 += 256;

        int byte4 = bytes[offset + 3];
        if (byte4 < 0) byte4 += 256;

        int byte5 = bytes[offset + 4];
        if (byte5 < 0) byte5 += 256;

        int byte6 = bytes[offset + 5];
        if (byte6 < 0) byte6 += 256;

        int byte7 = bytes[offset + 6];
        if (byte7 < 0) byte7 += 256;

        int byte8 = bytes[offset + 7];
        if (byte8 < 0) byte8 += 256;

        return (byte1 << 56) + (byte2 << 48) + (byte3 << 40) + (byte4 << 32) + (byte5 << 24) + (byte6 << 16) + (byte7 << 8) + byte8;
    }
}
