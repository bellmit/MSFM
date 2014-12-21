package com.cboe.domain.util.remoteShell;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.cboe.domain.util.InputOutputStream;

public abstract class AbstractTransport implements Transport
{
    private String host;
    private int port;
    private Socket socket;
    protected String userName;

    public Socket getSocket()
    {
        return socket;
    }


    public AbstractTransport(String host, int port, String userName){
          this.host = host;
          this.port = port;
          this.userName = userName;
    }
    
    public AbstractTransport(String userName){
        this.userName = userName;
  }
    
    public void startNewSocket() throws IOException{
       
        socket = new Socket();
        socket.setTcpNoDelay(true);
        socket.connect(new InetSocketAddress(host, port)); 
        
    }

    
    public String getHostName()
    {
        return host;

    }

    public int getPort()
    {
        return port;
    }
    
    public String getUserName()
    {
        return userName;
    }
    
    protected void getData(StringBuffer sout, InputOutputStream is, InputStream chanIn)
    {
        byte[] buffer = new byte[512];
        while (true) {
            try {
                int n = chanIn.read(buffer);
                if (n < 0) {
                    break;
                }
                if (n > 0) {
                    if(is != null){
                    is.feed(buffer, n);
                    }
                    sout.append(new String(buffer,0,n));
                }
            } catch (IOException x) {
                break;
            }
        }
    }
}
