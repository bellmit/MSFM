//
// ------------------------------------------------------------------------
// FILE: SunClientFtpAdapter.java
//
// PACKAGE: com.cboe.presentation.common.storage
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.storage.adapters.ftp;

import com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * @author torresl@cboe.com
 */
public class SunClientFtpAdapter implements FtpAdapter
{
    protected SunFtpClient ftpClient;

    public SunClientFtpAdapter()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        ftpClient = new SunFtpClient();
        ftpClient.setConnectTimeout(30000);
        ftpClient.setReadTimeout(30000);
    }

    public void ascii()
            throws IOException
    {
        ftpClient.ascii();
    }

    public void binary()
            throws IOException
    {
        ftpClient.binary();
    }

    public void cdUp()
            throws IOException
    {
        ftpClient.cdUp();
    }

    public void closeServer()
            throws IOException
    {
        ftpClient.closeServer();
    }

    public void noop()
            throws IOException
    {
        ftpClient.noop();
    }

    public String pwd()
            throws IOException
    {
        return ftpClient.pwd();
    }

    public int issueCommand(String command)
            throws IOException
    {
        return ftpClient.sendCommand(command);
    }

    public void cd(String path)
            throws IOException
    {
        ftpClient.cd(path);
    }

    public void openServer(String host, int port)
            throws IOException
    {
        ftpClient.openServer(host, port);
    }

    public void login(String user, String password)
            throws IOException
    {
        ftpClient.login(user, password);
    }

    public void rename(String oldName, String newName)
            throws IOException
    {
        ftpClient.rename(oldName, newName);
    }

    public String[] list()
            throws IOException
    {
        ArrayList list = new ArrayList(20);
        InputStream inputStream = ftpClient.list();
        BufferedReader bw = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = bw.readLine()) != null)
        {
            list.add(line);
        }
        inputStream.close();
        return (String[]) list.toArray(new String[list.size()]);
    }

    public String[] list(String pathName)
            throws IOException
    {
        ArrayList list = new ArrayList(20);
        InputStream inputStream = ftpClient.nameList(pathName);
        BufferedReader bw = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = bw.readLine()) != null)
        {
            list.add(line);
        }
        inputStream.close();
        return (String[]) list.toArray(new String[list.size()]);
    }

    public void mkdir(String pathName)
            throws IOException
    {
        ftpClient.mkdir(pathName);
    }

    public void mkdirs(String pathName)
            throws IOException
    {
        ftpClient.mkdirs(pathName);
    }

    public InputStream get(String fileName)
            throws IOException
    {
        return ftpClient.get(fileName);
    }

    public OutputStream put(String fileName)
            throws IOException
    {
        return ftpClient.put(fileName);
    }

    public void delete(String name)
            throws IOException
    {
        ftpClient.sendCommand("DELE " + name);
    }

    public boolean exists(String name)
            throws IOException
    {
        try
        {
            list(name);
            return true;
        }
        catch (IOException ioe)
        {
            // if the file does not exist, an exception will be thrown
        }
        return false;
    }

    public boolean isConnected()
            throws IOException
    {
        return ftpClient.isConnected();
    }
}
