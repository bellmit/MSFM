/*
 * Created on Apr 13, 2004
 *
 */

package com.cboe.lwt.transaction;

/**
 * @author dotyl
 *
 */
public interface Transaction
{
    void abort() throws InterruptedException;
    void commit() throws InterruptedException, CommitVetoedException;
    
    void addObserver( TransactionObserver p_observer );
    void removeObserver( TransactionObserver p_observer );
}