package com.cboe.domain.util.remoteShell;

import java.io.IOException;

import com.cboe.domain.util.StreamLogger;


public interface Transport
{
   public String getUserName();
   public String getHostName();
   public int getPort();
   public String executeCommand(String cmd, boolean checkExit, int expectedExitStatus, boolean ignoreOutput, StreamLogger sl) throws IOException, ExitStatusException, InterruptedException;
   public void close();
   public boolean isConnected();
   
}

