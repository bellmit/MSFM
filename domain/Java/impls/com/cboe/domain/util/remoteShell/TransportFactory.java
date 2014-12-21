package com.cboe.domain.util.remoteShell;

import java.io.IOException;


public class TransportFactory
{
  public static enum SSHLibrary{
      JSCH,
      JARAMIKO
  }
  
  public static final SSHLibrary Lib = SSHLibrary.JSCH;
  
  public Transport getTransport(String p_host, int p_port, String p_userName, String p_password, String p_KeyFilePath) throws IOException{
      
   
      switch(Lib){
          case JSCH: return new SSHTransportJSch(p_host, p_port, p_userName, p_password, p_KeyFilePath);
          case JARAMIKO: return new SSHTransportJaramiko(p_host, p_port, p_userName, p_password, p_KeyFilePath);
          default: throw new RuntimeException("Wrong SSH library specified!");
          
      }
  }
  
public Transport getTransport(String p_host, String p_userName, String p_password, String p_KeyFilePath) throws IOException{
      
    
    switch(Lib){
        case JSCH: return new SSHTransportJSch(p_host, p_userName, p_password, p_KeyFilePath);
        case JARAMIKO: return new SSHTransportJaramiko(p_host, p_userName, p_password, p_KeyFilePath);
        default: throw new RuntimeException("Wrong SSH library specified!");
        
    }
  }
}
