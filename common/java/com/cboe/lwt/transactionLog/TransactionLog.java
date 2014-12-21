/*
 * Created on Jul 25, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cboe.lwt.transactionLog;

import java.io.IOException;

import com.cboe.lwt.byteUtil.ByteIterator;


/**
 * Interface for writing bytes to a log 
 */
public interface TransactionLog
{
    void write( final ByteIterator p_data, int p_length ) throws IOException;
    
    void flush();
    void dispose();
}