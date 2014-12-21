/*
 * Created on Apr 8, 2004
 *
 */
package com.cboe.lwt.transaction;


/**
 * Observer pattern 
 */
public interface TransactionObserver
{
    void observeAborted( Transaction p_transaction );
    void observeCommitting( Transaction p_transaction ) throws CommitVetoedException;
    void observeCommitComplete( Transaction p_transaction );
}
