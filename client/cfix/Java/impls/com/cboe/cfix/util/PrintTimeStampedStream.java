package com.cboe.cfix.util;

/**
 * PrintTimeStampedStream
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Prints a string with a timestamp before it
 *
 */

import java.io.*;
import java.util.*;

import com.cboe.client.util.*;

public class PrintTimeStampedStream extends PrintStream
{
   protected char[]  dateStamp = new char[5];
   protected long    midnight  = 0L;
   protected boolean printThreadName = false;

   public PrintTimeStampedStream(OutputStream out)
   {
       super(out, true);
   }

   public PrintTimeStampedStream(OutputStream out, boolean autoFlush)
   {
       super(out, autoFlush);
   }

    public PrintTimeStampedStream(OutputStream out, boolean autoFlush, boolean printThreadName)
    {
        super(out, autoFlush);
        this.printThreadName = printThreadName;
    }

   public void setPrintThreadName(boolean tf)
   {
       printThreadName = tf;
   }

   public boolean getPrintThreadName()
   {
       return printThreadName;
   }

   public void update()
   {
       int      field;
       Calendar cal = GregorianCalendar.getInstance();

       cal.set(Calendar.HOUR_OF_DAY, 0);
       cal.set(Calendar.MINUTE,      0);
       cal.set(Calendar.SECOND,      0);
       cal.set(Calendar.MILLISECOND, 0);

       midnight = cal.getTime().getTime();

       field = cal.get(Calendar.MONTH);
       dateStamp[0] = (char) ('0' + ((field + 1) / 10));
       dateStamp[1] = (char) ('0' + ((field + 1) % 10));

       field = cal.get(Calendar.DATE);
       dateStamp[2] = (char) ('0' + (field / 10));
       dateStamp[3] = (char) ('0' + (field % 10));
       dateStamp[4] = '_';
   }

   public void println(Object obj)
   {
       if (obj != null)
       {
           println(obj.toString());
       }
       else
       {
           println((String) null);
       }
   }

   public void println()
   {
       println((String) null);
   }

   /**
    * Override to insert a TimeStamp before the beginning of the line
    */
   public void println(String str)
   {
       long now = System.currentTimeMillis() - midnight;

       if (now > DateHelper.MILLISECONDS_PER_DAY)
       {
           update();

           now = System.currentTimeMillis() - midnight;
       }

       char[] timeStamp = new char[13];

       int j;

       j = (int) ((now / (DateHelper.MILLISECONDS_PER_HOUR))   % DateHelper.HOURS_PER_DAY);
       timeStamp[0]  = (char) ('0' + (j / 10));
       timeStamp[1]  = (char) ('0' + (j % 10));
       timeStamp[2]  = (char) (':');

       j = (int) ((now / (DateHelper.MILLISECONDS_PER_MINUTE)) % DateHelper.MINUTES_PER_HOUR);
       timeStamp[3]  = (char) ('0' + (j / 10));
       timeStamp[4]  = (char) ('0' + (j % 10));
       timeStamp[5]  = (char) (':');

       j = (int) ((now / (DateHelper.MILLISECONDS_PER_SECOND)) % DateHelper.SECONDS_PER_MINUTE);
       timeStamp[6]  = (char) ('0' + (j / 10));
       timeStamp[7]  = (char) ('0' + (j % 10));
       timeStamp[8]  = (char) ('.');

       j = (int) ((now % (DateHelper.MILLISECONDS_PER_SECOND)));
       timeStamp[9]  = (char) ('0' + (j / 100));
       timeStamp[10] = (char) ('0' + ((j / 10) % 10));
       timeStamp[11] = (char) ('0' + (j % 10));
       timeStamp[12] = (char) (' ');

       int    strLen;
       int    ch               = ' ';
       String threadName       = null;
       int    threadNameLen    = 0;

       if (str != null)
       {
           strLen = str.length();
       }
       else
       {
           strLen = 1;
       }

       if (printThreadName)
       {
           threadName = Thread.currentThread().getName();
           threadNameLen = threadName.length();
       }

       try
       {
           synchronized (out)
           {
               for (j = 0; j < strLen;)
               {
                   out.write((int) dateStamp[0]);
                   out.write((int) dateStamp[1]);
                   out.write((int) dateStamp[2]);
                   out.write((int) dateStamp[3]);
                   out.write((int) dateStamp[4]);

                   out.write((int) timeStamp[0]);
                   out.write((int) timeStamp[1]);
                   out.write((int) timeStamp[2]);
                   out.write((int) timeStamp[3]);
                   out.write((int) timeStamp[4]);
                   out.write((int) timeStamp[5]);
                   out.write((int) timeStamp[6]);
                   out.write((int) timeStamp[7]);
                   out.write((int) timeStamp[8]);
                   out.write((int) timeStamp[9]);
                   out.write((int) timeStamp[10]);
                   out.write((int) timeStamp[11]);
                   out.write((int) timeStamp[12]);

                   if (threadName != null)
                   {
                        if (threadName.charAt(0) != '[')
                        {
                            out.write('[');
                            for (int x = 0; x < threadNameLen; x++)
                            {
                                out.write((int) threadName.charAt(x));
                            }
                            out.write(']');
                        }
                        else
                        {
                            for (int x = 0; x < threadNameLen; x++)
                            {
                                out.write((int) threadName.charAt(x));
                            }
                        }

                        out.write(' ');
                   }

                   if (str != null)
                   {
                       for (; j < strLen;)
                       {
                          ch = str.charAt(j);
                          out.write(ch);
                          j++;

                          if (ch == '\n')
                          {
                              break;
                          }
                       }
                   }
                   else
                   {
                       j++;
                   }

                   if (ch != '\n')
                   {
                       out.write((int) ('\n'));
                   }
               }
           }
       }
       catch (Exception ex)
       {

       }
   }
}
