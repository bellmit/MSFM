package com.cboe.client.util.tourist;

/**
 * TouristSocketAdapter.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;
import java.net.*;

import com.cboe.client.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public class TouristSocketAdapter
{
    protected Socket         socket;
    protected BufferedReader istream;
    protected OutputStream   ostream;
    protected int            timeout;

    public void resetSocket(Socket socket)
    {
        close();

        this.socket = null;
        this.istream = null;
        this.ostream = null;

        try
        {
            this.socket = socket;

            this.istream = new BufferedReader(new InputStreamReader(new BufferedInputStream(socket.getInputStream())));
            this.ostream = new BufferedOutputStream(socket.getOutputStream());

            this.socket.setSoLinger(true, 1);
            this.socket.setSoTimeout(timeout);
        }
        catch (Exception ex)
        {
            Log.exception(ex);
        }
    }

    public Socket getSocket()
    {
        return socket;
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
            socket = null;
            istream = null;
            ostream = null;
        }
    }

    public boolean isConnected()
    {
        return socket != null;
    }

    public String read() throws Exception
    {
        return istream.readLine();
    }

    public void write(String str) throws Exception
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
        }
        catch (Exception ex)
        {
            Log.exception(ex);
            throw ex;
        }
    }

    public void write(byte[] bytes) throws Exception
    {
        if (!isConnected())
        {
            return;
        }

        try
        {
            ostream.write(bytes);
            ostream.flush();
        }
        catch (Exception ex)
        {
            Log.exception(ex);
            throw ex;
        }
    }
}

