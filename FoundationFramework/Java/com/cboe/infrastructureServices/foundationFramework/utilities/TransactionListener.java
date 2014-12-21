package com.cboe.infrastructureServices.foundationFramework.utilities;

/**
 * A transaction listener can register with the Transaction class to be
 * called back when the current transaction is committed or rolled back.
 *
 * @author John Wickberg
 */
public interface TransactionListener {

    /**
     * Processes the "transaction committed" event.
     */
    void commitEvent();

    /**
     * Processes the "transaction rolled back" event.
     */
    void rollbackEvent();
}
