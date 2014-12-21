package com.cboe.domain.util.remoteShell;

import java.util.concurrent.Callable;


/**
 * @author baranski
 *
 */
public class TransportCommand implements Callable<Transport>{

    String host;
    String userId;
    String password;
    String pkFile;
    TransportFactory tf;
   
    public TransportCommand(TransportFactory p_tf, String p_host, String p_userId, String p_password, String p_pkFile )
    {
        super();
        host = p_host;
        password = p_password;
        pkFile = p_pkFile;
        userId = p_userId;
        tf = p_tf;
        
    }
    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    public Transport call() throws Exception
    {
        return tf.getTransport(host, userId, password, pkFile);
    }
}