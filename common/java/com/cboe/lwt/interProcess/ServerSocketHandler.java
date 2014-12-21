/*
 * Created on Mar 21, 2005
 */
package com.cboe.lwt.interProcess;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.cboe.lwt.collection.IntHashMap;
import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.thread.ThreadTask;


public class ServerSocketHandler 
    extends ThreadTask
{
    private int                      port;
    private int                      sendBufferSize;
    private int                      receiveBufferSize;
    private ConnectionHandlerFactory handlerFactory;
    
    private int                      connectionNumber = 0;
    private IntHashMap               connectionHandlers;
    
    
    public ServerSocketHandler( String                   p_name,
                                int                      p_port,
                                int                      p_sendBufferSize,
                                int                      p_receiveBufferSize,
                                ConnectionHandlerFactory p_handlerFactory )
    {
        super( p_name );
        
        port               = p_port;
        sendBufferSize     = p_sendBufferSize;
        receiveBufferSize  = p_receiveBufferSize;
        handlerFactory     = p_handlerFactory;
        connectionHandlers = new IntHashMap( 32 );  // TODO make configurable?  nah.
    }
        
    
    public Object[] getActiveConnectionThreads()
    {
        synchronized ( connectionHandlers )
        {
            IntHashMap.Iter i = connectionHandlers.iterator();
            while ( i.hasNext() )
            {
                i.next();
                
                if ( ! ( (ThreadTask)i.getValue() ).isRunning() )
                {
                    i.remove();
                }
            }
        }
        return connectionHandlers.copyToArray();
    }
   
    
    protected void doTask()
        throws IOException
    {
        ServerSocketChannel serverSock;
        serverSock = ServerSocketChannel.open();
        TcpIpc ipc = null;
        
        serverSock.configureBlocking( true );
        InetSocketAddress address = new InetSocketAddress( port );
        serverSock.socket().setReceiveBufferSize( receiveBufferSize );
        serverSock.socket().bind( address );
        Logger.info( name + " : Port ready for connection : " + port );
        
        try
        {
            while ( true )
            {
                SocketChannel in = serverSock.accept();
                
                ++connectionNumber;
                
                String connectionName = name + "." + connectionNumber;
                
                Logger.info( connectionName + " : Client connection accepted on port : " + port );
                                    
                ipc = TcpIpc.wrapExistingChannel( in, sendBufferSize, receiveBufferSize );  
                
                ThreadTask handler = handlerFactory.getHandler( connectionName, 
                                                                connectionNumber,
                                                                ipc );
                synchronized ( connectionHandlers )
                {
                    connectionHandlers.put( connectionNumber, handler );
                }
                
                handler.go();
            }
        }
        catch( IOException ex )
        {
            Logger.error( name + " : Exception in Worker Thread for connection # " + connectionNumber, ex );
        }
        finally
        {
            serverSock.socket().close();
            Logger.critical( name + " : ServerSocket CLOSED" );  
        } 
    }

    
    
}
