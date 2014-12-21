package com.cboe.cfix.fix.net;

/**
 * FixSocketAdapter.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;
import java.net.*;

import com.cboe.cfix.interfaces.*;
import com.cboe.client.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

/**
 * Smart socket that understands a FIX packet
 *
 */

public class FixSocketAdapter
{
    protected Socket               socket;
    protected InputStream          istream;
    protected OutputStream         ostream;
    protected FixPacketParserIF    fixPacketParser;
    protected int                  timeout;
    protected long                 lastSentTime;
    protected long                 lastRecvTime;
    protected int                  debugFlags = FixSessionDebugIF.DEBUG_OFF;

    public FixSocketAdapter()
    {

    }

    public int setDebugFlags(int debugFlags)
    {
        int oldDebugFlags = this.debugFlags;

        this.debugFlags = debugFlags;

        return oldDebugFlags;
    }

    public long getLastSentTime()
    {
        return lastSentTime;
    }

    public long getLastRecvTime()
    {
        return lastRecvTime;
    }

    public void resetSocket(Socket socket)
    {
        close();

        this.socket  = null;
        this.istream = null;
        this.ostream = null;

        try
        {
            this.socket  = socket;

            this.istream = new BufferedInputStream(socket.getInputStream());
            this.ostream = new BufferedOutputStream(socket.getOutputStream());

            this.socket.setSoLinger(true, 5);
            this.socket.setSoTimeout(timeout);

            fixPacketParser.reset();
        }
        catch (Exception ex)
        {
            Log.exception(ex);
        }
    }

    public void setFixPacketParser(FixPacketParserIF fixPacketParser)
    {
        this.fixPacketParser = fixPacketParser;
    }

    public Socket getSocket()
    {
        return socket;
    }

    public InputStream getSocketInputStream()
    {
        return istream;
    }

    public OutputStream getSocketOutputStream()
    {
        return ostream;
    }

    public void setTimeout(int timeout)
    {
        if (!isConnected())
        {
            return;
        }

        this.timeout = timeout;

        try
        {
           socket.setSoTimeout(timeout);
        }
        catch (SocketException ex)
        {

        }
    }

    public void close()
    {
        if (!isConnected())
        {
            return;
        }

        try
        {
            socket.close();
            istream.close();
            ostream.close();
        }
        catch (Exception ex)
        {

        }
        finally
        {
            socket  = null;
            istream = null;
            ostream = null;
        }
    }

    public boolean isConnected()
    {
        return socket != null;
    }

    public FixPacketIF read()
    {
        FixPacketIF fixPacket = fixPacketParser.parse(istream, 0, debugFlags);

        lastRecvTime = System.currentTimeMillis();

        return fixPacket;
    }

    public void write(String str) throws Exception
    {
        if (!isConnected())
        {
            return;
        }

        Exception exception = null;

        try
        {
            int len = str.length();
            for (int i = 0; i < len; i++)
            {
                ostream.write((int) str.charAt(i));
            }

            ostream.flush();
            lastSentTime = System.currentTimeMillis();
        }
        catch (Exception ex)
        {
            exception = ex;
        }

        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.SESSION_SHOW_RAW_SENT_MESSAGES))
        {
            Log.information(Thread.currentThread().getName() + (exception != null ? " NOT" : " ") + "SENT [" + str + "]");
        }

        if (exception != null)
        {
            Log.exception(exception);
            throw exception;
        }
    }

    public void writeNoDebug(String str) throws Exception
    {
        if (!isConnected())
        {
            return;
        }

        try
        {
            int len = str.length();
            for (int i = 0; i < len; i++)
            {
                ostream.write((int) str.charAt(i));
            }

            ostream.flush();
            lastSentTime = System.currentTimeMillis();
        }
        catch (Exception ex)
        {
            Log.exception(ex);
            throw ex;
        }
    }

    public void write(FastCharacterWriter fastCharacterWriter) throws Exception
    {
        if (!isConnected())
        {
            return;
        }

        Exception exception = null;

        try
        {
             fastCharacterWriter.write(ostream);
             ostream.flush();
             lastSentTime = System.currentTimeMillis();
        }
        catch (Exception ex)
        {
            exception = ex;
        }

        if (BitHelper.isBitMaskSet(debugFlags, FixSessionDebugIF.SESSION_SHOW_RAW_SENT_MESSAGES))
        {
            Log.information(Thread.currentThread().getName() + (exception != null ? " NOT" : " ") + "SENT [" + fastCharacterWriter.toString() + "]");
        }

        if (exception != null)
        {
            Log.exception(exception);
            throw exception;
        }
    }

    public void writeNoDebug(FastCharacterWriter fastCharacterWriter) throws Exception
    {
        if (!isConnected())
        {
            return;
        }

        try
        {
            fastCharacterWriter.write(ostream);
            ostream.flush();
            lastSentTime = System.currentTimeMillis();
        }
        catch (Exception ex)
        {
            Log.exception(ex);
            throw ex;
        }
    }
}
