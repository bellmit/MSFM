package com.cboe.domain.util;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

public class StreamLogger {

    Calendar cal = Calendar.getInstance();
    private InputOutputStream inputStream;

    public StreamLogger(InputOutputStream p_inputStream)
    {
        super();
        inputStream = p_inputStream;
    }

    public InputOutputStream getInputStream()
    {
        return inputStream;
    }

    public void setInputStream(InputOutputStream p_inputStream)
    {
        inputStream = p_inputStream;
    }

    public void println(String output){
        StringBuffer sb = new StringBuffer();
        cal.setTimeInMillis(System.currentTimeMillis());
        sb.append(String.format("%1$tH:%1$tM:%1$tS.%1$tL", cal));
        sb.append("  ");
        sb.append(output);
        sb.append("\n");
        String line = sb.toString();
  
        inputStream.feed(line.getBytes(), line.length());
    }
    
    public void printlnNoTimestamp(String output){
       
        output += "\n";
        inputStream.feed(output.getBytes(), output.length());
    }

    public void exception(String p_string, Throwable p_e)
    {
        println(p_string);
        println(p_e.toString());
        StackTraceElement[] ste = p_e.getStackTrace();
        for(StackTraceElement st:ste){
          String element = "at " + st.toString() + "\n";
          inputStream.feed(element.getBytes(), element.length());
        }
        Throwable th = p_e.getCause();
        if(th != null){
         exception("The causing exception:", th);   
        }
    }
    
    public void print(PrintStream ps){
        

        byte[] buffer = new byte[512];
        while (true) {
            try {
                int n = inputStream.read(buffer);
                if (n < 0) {
                    break;
                }
                if (n > 0) {
                    ps.print(new String(buffer, 0, n));
                }
            } catch (IOException x) {
                break;
            }
        }   
    
        
    }
}
