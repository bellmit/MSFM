package com.cboe.client.util;

/**
 * ThreadHelper.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * Helper for working with Java Threads
 *
 */

import java.io.*;

public class ThreadHelper
{
   /**
    * Same as Thread.sleep(), but without the exceptions
    *
    */
   public final static void sleep(long milliseconds)
   {
       try
       {
           Thread.sleep(milliseconds);
       }
       catch (Exception ex)
       {

       }
   }
   /**
    * Sleep for <i>seconds</i> seconds, without the exceptions
    *
    */
   public final static void sleepSeconds(int seconds)
   {
       sleep(seconds * 1000);
   }

   public static Writer dumpThreadGroup(Writer out, ThreadGroup threadGroup, int index, boolean recurse, boolean xml) throws Exception
   {
       int activeCount = threadGroup.activeCount();
       StringBuilder sb = new StringBuilder(100);
       if (xml)
       {
           String s = threadGroup.getName().replace('<', '{').replace('>', '}');
           sb.append("<ThreadGroup name=\"").append(s).append("\" count=\"").append(activeCount).append("\">");
           out.write(sb.toString());
       }
       else
       {
           sb.append("ThreadGroup(").append(index).append(")[");
           out.write(sb.toString());
           out.write(threadGroup.getName());
           out.write("]\n");
       }

       Thread[] threads = new Thread[activeCount];

       int num = threadGroup.enumerate(threads, false);

       for (int t = 0; t < num; t++)
       {
           if (threads[t] == null)
           {
               continue;
           }

           sb.setLength(0);
           if (xml)
           {
               String s = threads[t].getName().replace('<', '{').replace('>', '}');
               sb.append("<Thread name=\"").append(s).append("\"/>");
               out.write(sb.toString());
           }
           else
           {
               out.write(StringHelper.spaces(3));
               sb.append("Thread(").append(t).append(")[");
               out.write(sb.toString());
               out.write(threads[t].getName());
               out.write("]\n");
           }
       }

       if (recurse)
       {
           ThreadGroup[] groups = new ThreadGroup[threadGroup.activeGroupCount()];

           num = threadGroup.enumerate(groups, false);

           for (int g = 0; g < num; g++)
           {
               if (groups[g] != null)
               {
                   ThreadHelper.dumpThreadGroup(out, groups[g], g, recurse, xml);
               }
           }
       }

       if (xml)
       {
           out.write("</ThreadGroup>");
       }

       return out;
   }

   public static Writer dumpThreadGroups(Writer out, boolean xml) throws Exception
   {
       ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();

       // find top-level group
       while (threadGroup.getParent() != null)
       {
           threadGroup = threadGroup.getParent();
       }

       // descend from it
       return ThreadHelper.dumpThreadGroup(out, threadGroup, 0, true, xml);
   }
}

