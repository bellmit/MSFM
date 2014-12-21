//
// ------------------------------------------------------------------------
// FILE: SunFtpClient.java
//
// PACKAGE: com.cboe.presentation.common.storage
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.storage.adapters.ftp;

import sun.net.ftp.FtpClient;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @author torresl@cboe.com
 */
public class SunFtpClient extends FtpClient
{
    public SunFtpClient()
    {
        super();
    }

    public int sendCommand(String command)
            throws IOException
    {
        return issueCommand(command);
    }

    public void mkdir(String pathName)
            throws IOException
    {
        sendCommand("MKD " + pathName);
    }

    public void mkdirs(String pathName)
            throws IOException
    {
        StringTokenizer tk = new StringTokenizer(pathName, "/", false);
        StringBuffer wholePath = new StringBuffer(pathName.length());
        String currentPwd = pwd();
        while (tk.hasMoreElements())
        {
            String partialPath = (String) tk.nextElement();
            wholePath.append(partialPath);
            try
            {
                cd(wholePath.toString());
                cd(currentPwd);
            }
            catch(IOException ioe)
            {
                // try to make the directory and then CD again
                mkdir(wholePath.toString());
            }
            wholePath.append("/");
        }
        cd(currentPwd);
    }

    public boolean isConnected()
    {
        return serverIsOpen();
    }
}
