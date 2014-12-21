package com.cboe.client.util.tourist;

/**
 * TouristSession.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;
import java.net.*;
import java.util.*;

import com.cboe.client.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public final class TouristSession implements Runnable
{
    protected TouristSocketAdapter touristSocketAdapter;
    protected boolean              logOutput;

    protected final static String SEPARATORS = "?&\n\r";

    public TouristSession()
    {

    }

    public TouristSession(Socket s, boolean logOutput)
    {
        touristSocketAdapter = new TouristSocketAdapter();

        this.logOutput = logOutput;

        try
        {
            touristSocketAdapter.resetSocket(s);
        }
        catch (Exception ex)
        {

        }
    }

    public static String createAndExecuteTourist(String string, boolean logOutput)
    {
        Date startTime = new Date();

        try
        {
            return executeTourist(createTourist(string, logOutput));
        }
        catch (Exception ex)
        {
            Log.exception("TOURIST STRING(" + string + ")", ex);

            StringBuffer buffer = new StringBuffer(256);

            buffer.append("<touristResult startTime=\"");
            buffer.append(DateHelper.stringizeDateInLogFormat(startTime));
            buffer.append("\" endTime=\"");
            buffer.append(DateHelper.stringizeDateInLogFormat());
            buffer.append("\" error=\"Unexpected Exception\"/>");

            return buffer.toString();
        }
        catch (Throwable th)
        {
            Log.information("TOURIST STRING(" + string + ") EXCEPTION" + ExceptionHelper.getStackTrace(th));

            StringBuffer buffer = new StringBuffer(256);

            buffer.append("<touristResult startTime=\"");
            buffer.append(DateHelper.stringizeDateInLogFormat(startTime));
            buffer.append("\" endTime=\"");
            buffer.append(DateHelper.stringizeDateInLogFormat());
            buffer.append("\" error=\"Unexpected Exception\"/>");

            return buffer.toString();
        }
    }

    public static TouristIF createTourist(String string, boolean logOutput) throws Exception
    {
        TouristIF       tourist;
        StringTokenizer tokenizer;
        int             pos;
        boolean         isHttp = false;

        if (logOutput)
        {
            String thname = Thread.currentThread().getName();
            StringBuilder sb = new StringBuilder(thname.length()+string.length()+20);
            sb.append(thname).append(" TOURIST Request: [").append(string).append("]");
            Log.information(sb.toString());
        }

        string = URLDecoder.decode(string);

        if (string.startsWith("GET ") || string.startsWith("POST "))
        {
            int httpIndex = string.indexOf(" HTTP");
            if (httpIndex == -1)
            {
                throw new Exception("TOURIST CREATION ERROR: Not valid HTTP request(" + string + ")");
            }

            string = string.substring(string.indexOf(' ') + 1, httpIndex);
            if (string.startsWith("/"))
            {
                string = string.substring(1);
            }

            isHttp = true;
        }

        if (!string.startsWith("com.cboe."))
        {
            throw new Exception("TOURIST CREATION ERROR: Doesn't start with com.cboe. (" + string + ")");
        }

        tokenizer = new StringTokenizer(string, SEPARATORS);

        tourist = (TouristIF) ClassHelper.loadClassWithExceptions(tokenizer.nextToken());

        tourist.setIsHttp(isHttp);

        while (tokenizer.hasMoreTokens())
        {
            string = tokenizer.nextToken();
            pos    = string.indexOf("=");
            if (pos >= 0)
            {
                tourist.addParameter(string.substring(0, pos), string.substring(pos + 1));
            }
        }

        return tourist;
    }

    public static String executeTourist(TouristIF tourist)
    {
        Date startTime = new Date();

        StringWriter writer = new StringWriter(1024);
        StringBuilder buffer = null;

        try
        {
            tourist.visit(writer);

            String writerString = writer.toString();

            buffer = new StringBuilder(writerString.length() + 256);

            if (tourist.isHttp() && tourist.getProperties().getProperty("tourist_showheader") == null)
            {
                buffer.append("HTTP/1.1 200 OK\nContent-Type: text/xml\n\n<?xml version=\"1.0\" standalone=\"yes\"?>\n");

                String stylesheet = tourist.getProperties().getProperty("tourist_xslstylesheet");

                if (stylesheet != null && stylesheet.trim().length() > 0)
                {
                    buffer.append("<?xml-stylesheet type=\"text/xsl\" href=\"").append(stylesheet).append("\"?>\n");
                }
            }

            buffer.append("<touristResult startTime=\"");
            buffer.append(DateHelper.stringizeDateInLogFormat(startTime));
            buffer.append("\" endTime=\"");
            buffer.append(DateHelper.stringizeDateInLogFormat());
            buffer.append("\" class=\"");
            buffer.append(tourist.getClass().getName());
            buffer.append("\">");

            buffer.append(writerString);

            buffer.append("</touristResult>");
        }
        catch (Exception ex)
        {
            buffer = new StringBuilder(512);

            buffer.append("<touristResult startTime=\"");
            buffer.append(DateHelper.stringizeDateInLogFormat(startTime));
            buffer.append("\" endTime=\"");
            buffer.append(DateHelper.stringizeDateInLogFormat());
            buffer.append("\" class=\"");
            buffer.append(tourist.getClass().getName());
            buffer.append("\">");

            buffer.append("<Exception>");
            if (tourist.isHttp())
            {
                buffer.append(tourist.getClass().getName()).append(" Exception: ").append(URLEncoder.encode(ExceptionHelper.getStackTrace(ex, "<br/>")));
            }
            else
            {
                buffer.append(tourist.getClass().getName()).append(" Exception: ").append(URLEncoder.encode(ExceptionHelper.getStackTrace(ex)));
            }
            buffer.append("</Exception>");
            buffer.append("</touristResult>");

            Log.exception(tourist.getClass().getName(), ex);
        }
        catch (Throwable ex)
        {
            buffer = new StringBuilder(512);

            buffer.append("<touristResult startTime=\"");
            buffer.append(DateHelper.stringizeDateInLogFormat(startTime));
            buffer.append("\" endTime=\"");
            buffer.append(DateHelper.stringizeDateInLogFormat());
            buffer.append("\" class=\"");
            buffer.append(tourist.getClass().getName());
            buffer.append("\">");

            buffer.append("<Exception>");
            if (tourist.isHttp())
            {
                buffer.append(tourist.getClass().getName()).append(" Exception: ").append(URLEncoder.encode(ExceptionHelper.getStackTrace(ex, "<br/>")));
            }
            else
            {
                buffer.append(tourist.getClass().getName()).append(" Exception: ").append(URLEncoder.encode(ExceptionHelper.getStackTrace(ex)));
            }
            buffer.append("</Exception>");
            buffer.append("</touristResult>");

            Log.information(tourist.getClass().getName() + " Exception: " + ExceptionHelper.getStackTrace(ex));
        }

        writer  = null;
        tourist = null;

        return buffer.toString();
    }

    public void run()
    {
        String       s;
        TouristIF    tourist;
        StringWriter writer;
        Date         startTime;

        do
        {
            writer  = null;
            tourist = null;
            s       = null;

            try
            {
                s = touristSocketAdapter.read();
            }
            catch (Exception ex)
            {
                Log.exception(ex);
                break;
            }

            if (s == null)
            {
                break;
            }

            s = s.trim();

            if (s.length() < 10 || !(s.startsWith("GET ") || s.startsWith("POST ")) || s.indexOf("com.cboe.") == -1)
            {
                break;
            }

            try
            {
                tourist = createTourist(s, logOutput);
            }
            catch (Exception ex)
            {
                Log.exception("TOURIST CREATION ERROR: String(" + s + ")",  ex);

                try
                {
                    touristSocketAdapter.write("Exception in creating tourist: " + ExceptionHelper.getStackTrace(ex));
                }
                catch (Exception ex2)
                {

                }

                break;
            }

            if (tourist == null)
            {
                Log.information("TOURIST CREATION ERROR: String(" + s + ")");

                try
                {
                    touristSocketAdapter.write("Couldn't create tourist");
                }
                catch (Exception ex)
                {

                }

                break;
            }

            try
            {
                touristSocketAdapter.write(executeTourist(tourist));
            }
            catch (Exception ex)
            {

            }
        }
        while (false);

        try
        {
            touristSocketAdapter.close();
        }
        catch (Exception ex)
        {

        }

        writer               = null;
        s                    = null;
        tourist              = null;
        touristSocketAdapter = null;
    }
}
