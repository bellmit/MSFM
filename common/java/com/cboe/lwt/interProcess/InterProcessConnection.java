/*
 * InterProcessConnection.java
 *
 * Created on May 3, 2002, 3:57 PM
 */

package com.cboe.lwt.interProcess;

import com.cboe.lwt.byteUtil.ByteReader;
import com.cboe.lwt.byteUtil.ByteWriter;
import com.cboe.lwt.transactionLog.LogDestination;

/**
 * Represents an Inter-process connection without respect to transport protocol
 * or whether the communication is inter or intra machine
 *   
 * @author  dotyl
 */
public interface InterProcessConnection extends ByteWriter, ByteReader
{
    /**
     * Connects to the remote process over any of the connection's URLs
     *
     * @param p_connectRetryTimeout number of milliseconds to wait after all
     * addresses fail before starting again at the top of the list
     * @throws IpcException on failure
     */
    void connect( int p_connectRetryTimeout ) throws IpcException;

    
    /**
     * Connects to the remote process over any of the connection's URLs
     *
     * @param p_connectRetryTimeout number of milliseconds to wait after all
     * addresses fail before starting again at the top of the list
     * @throws IpcException on failure
     */
    void connect( int p_connectRetryTimeout,
                  int p_retryLoopsBeforeFailure ) throws IpcException;

    
    /**
     * Connects to the remote process over only the connection specified by p_index 
     * 
     * @param p_lowIndex   the first index to try to connect to
     * @param p_highIndex  the last index to try to connect to
     * @param p_connectRetryTimeout number of milliseconds to wait after all
     * addresses fail before starting again at the top of the list
     * @throws IpcException on failure
     */
    void connectSpecific( int p_lowIndex, 
                          int p_highIndex, 
                          int p_connectRetryTimeout ) throws IpcException;

    
    /**
     * Connects to the remote process over only the connection specified by p_index 
     * 
     * @param p_lowIndex   the first index to try to connect to
     * @param p_highIndex  the last index to try to connect to
     * @param p_connectRetryTimeout number of milliseconds to wait after all
     * addresses fail before starting again at the top of the list
     * @throws IpcException on failure
     */
    void connectSpecific( int p_lowIndex, 
                          int p_highIndex, 
                          int p_connectRetryTimeout,
                          int p_retryLoopsBeforeFailure ) throws IpcException;


    /**
     * Disconnects from the remote process
     *
     * @throws IpcException on failure
     */
    void disconnect() throws IpcException;    
    
        
    /**
     * @returns true if the output is hung (blocked on a send and with the
     * same block since the last time isHung() is called) and false otherwise
     */
    boolean isHung();
    
    
    /**
     * @return true if this IPC is connected to the remote socket, false otherwise
     */
    boolean isConnected();
    
    
    /**
     * enables transaction logging to the specified log destination for blocks
     * written to the IPC 
     * 
     * @param p_logDest the destination of the log
     * @return the new transaction log
     */
    void enableInboundTransactionLogging( LogDestination p_logDest, int p_logFlushInterval_MS );
    
    
    /**
     * disables transaction logging for blocks written to the IPC
     *
     */
    void disableOutboundTransactionLogging();
    
    /**
         * returns true if transaction logging for blocks written to the IPC
         * is enabled, false - otherwise
         */
    boolean isOutboundTransactionLoggingEnabled(); 
    
    
    /**
     * enables transaction logging to the specified log destination for blocks
     * received by the IPC 
     * 
     * @param p_logDest the destination of the log
     * @return the new transaction log
     */
    void enableOutboundTransactionLogging( LogDestination p_logDest, int p_logFlushInterval_MS );
    
    /**
     * disables transaction logging for blocks received by the IPC
     *
     */
    void disableInboundTransactionLogging();
    
    /**
         * returns true if transaction logging for blocks written to the IPC
         * is enabled, false - otherwise
         */
    boolean isInboundTransactionLoggingEnabled();
    
    
    String getConnectionString();
    
    
    int getConnectionNumber();
    
    /**
     * Writes bytes to the destination
     *
     * @param p_block the storage to write from
     * @param p_startOfBlock the offset in p_block at which to start copying
     * @param p_blockLength the number of bytes to copy
     *
     * @throws IpcException on failure
     */
    void write( byte[] p_block, 
                int p_startOfBlock, 
                int p_blockLength ) throws IpcException;
                
    /**
     * Writes a byte to the destination
     *
     * @param p_byte the byte to write
     *
     * @throws IpcException on failure
     */
    void write( byte p_byte ) throws IpcException;
    
    
    /**
     * Flushes all bytes to the IPC (blocks until all bytes are processed)
     *
     * @throws IpcException on failure
     */
     void flush() throws IpcException;
    
    /**
     * Reads a byte from the source
     *
     * @return the byte read from the stream
     *
     * @throws IpcException on failure
     */
    byte read() throws IpcException;
    
    /**
     * Reads bytes from the source
     *
     * @param p_dest the destination buffer that will receive the read
     * @param p_destOffset the offset within the p_dest buffer to take the first byte
     * @param p_maxLength the number of bytes to attempt to copy 
     *
     * @return the number of bytes read
     *
     * @throws IpcException on failure, including if there are no further bytes available
     */
    int read( byte[] p_dest, 
              int    p_destOffset,
              int    p_maxLength ) throws IpcException;
    
    /**
     * Reads bytes from the source and block until a minimum number of bytes are read
     *
     * @param p_dest the destination buffer that will receive the read
     * @param p_destOffset the offset within the p_dest buffer to take the first byte
     * @param p_maxLength the number of bytes to attempt to copy 
     * @param p_minLength the minimum number of bytes to copy (will block until available)
     *
     * @return the number of bytes read
     *
     * @throws IpcException on failure, including if there are no further bytes available
     */
    int read( byte[] p_dest, 
              int    p_destOffset,
              int    p_maxLength,
              int    p_minLength ) throws IpcException;
    
}
