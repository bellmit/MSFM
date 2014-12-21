package com.cboe.test.application.cmiScript;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.ExceptionDetails;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotSupportedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Log
{
    // Destination for output, can be changed by setOutputFile
    private static PrintStream outStream = System.out;

    private static StringBuilder zeropad(int num, int digits)
    {
        StringBuilder s = new StringBuilder().append(num);
        while (s.length() < digits)
        {
            s.insert(0, '0');
        }
        return s;
    }

    private static StringBuilder now()
    {
        Calendar c = new GregorianCalendar();
        return new StringBuilder()
            .append(c.get(Calendar.YEAR)).append('-')
            .append(zeropad(c.get(Calendar.MONTH)+1, 2)).append('-')
            .append(zeropad(c.get(Calendar.DAY_OF_MONTH), 2)).append(' ')
            .append(zeropad(c.get(Calendar.HOUR_OF_DAY),2)).append(':')
            .append(zeropad(c.get(Calendar.MINUTE),2)).append(':')
            .append(zeropad(c.get(Calendar.SECOND),2)).append('.')
            .append(zeropad(c.get(Calendar.MILLISECOND),3));
    }

    /** Format the details field of a CMi exception.
     * @param sb Destination for formatted text.
     * @param d Details field.
     **/
    private static void appendDetails(StringBuilder sb, ExceptionDetails d)
    {
        sb.append("details { ").append(d.dateTime)
          .append(" error:").append(d.error)
          .append(" severity:").append(d.severity)
          .append(" message:").append(d.message)
          .append(" }\n");
    }

    public static void setOutputFile(String names[])
    {
        PrintStream out;
        int i = 0;
        while (i < names.length)
        {
            try
            {
                out = new PrintStream(names[i]);
                outStream = out;
        /*while*/ break;
            }
            catch (FileNotFoundException fnf)
            {
                // Can't create this file
            }
            ++i; // Try using the next name in the list
        }
        Log.message("Output file is " +
                (outStream == System.out ? "standard output" : names[i]));
    }

    private static void writeFiles(StringBuilder message)
    {
        String m = message.toString();
        outStream.println(m);
        if (outStream != System.out)
        {
            System.out.println(m);
        }
    }

    /** Print an exception to outStream.
     * @param t Exception to print.
     **/
    public static void throwable(Throwable t)
    {
        CharArrayWriter stackChars = new CharArrayWriter();
        PrintWriter stackWriter = new PrintWriter(stackChars);
        t.printStackTrace(stackWriter);

        StringBuilder message = new StringBuilder(now());
        message.append(' ').append(t.toString()).append('\n');

        if (t instanceof AlreadyExistsException)
        {
            AlreadyExistsException e = (AlreadyExistsException) t;
            appendDetails(message, e.details);
        }
        else if (t instanceof AuthorizationException)
        {
            AuthorizationException e = (AuthorizationException) t;
            appendDetails(message, e.details);
        }
        else if (t instanceof AuthenticationException)
        {
            AuthenticationException e = (AuthenticationException) t;
            appendDetails(message, e.details);
        }
        else if (t instanceof CommunicationException)
        {
            CommunicationException e = (CommunicationException) t;
            appendDetails(message, e.details);
        }
        else if (t instanceof DataValidationException)
        {
            DataValidationException e = (DataValidationException) t;
            appendDetails(message, e.details);
        }
        else if (t instanceof NotFoundException)
        {
            NotFoundException e = (NotFoundException) t;
            appendDetails(message, e.details);
        }
        else if (t instanceof NotAcceptedException)
        {
            NotAcceptedException e = (NotAcceptedException) t;
            appendDetails(message, e.details);
        }
        else if (t instanceof NotSupportedException)
        {
            NotSupportedException e = (NotSupportedException) t;
            appendDetails(message, e.details);
        }
        else if (t instanceof SystemException)
        {
            SystemException e = (SystemException) t;
            appendDetails(message, e.details);
        }
        else if (t instanceof TransactionFailedException)
        {
            TransactionFailedException e = (TransactionFailedException) t;
            appendDetails(message, e.details);
        }

        message.append(stackChars.toCharArray());
        writeFiles(message);
    }

    /** Print a string to outStream. Output date and time first.
     * @param s Text to print.
     **/
    public static void message(String s)
    {
        StringBuilder message = new StringBuilder(now());
        message.append(' ') .append(s);
        writeFiles(message);
    }

    /** Print a StringBuilder to outStream. Output date and time first.
     * @param s Text to print.
     **/
    public static void message(StringBuilder s)
    {
        StringBuilder message = new StringBuilder(now());
        message.append(' ') .append(s);
        writeFiles(message);
    }
}
