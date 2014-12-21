/*
 * Created on Mar 22, 2005
 */
package com.cboe.lwt.interProcess;

import com.cboe.lwt.interProcess.TcpIpc;
import com.cboe.lwt.thread.ThreadTask;

public interface ConnectionHandlerFactory
{
    ThreadTask getHandler( String p_name, 
                           int    p_connectionNumber,
                           TcpIpc p_ipc );
}
