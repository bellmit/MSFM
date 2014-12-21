package com.cboe.interfaces.domain;

import java.io.Serializable;
import com.cboe.infrastructureServices.foundationFramework.BObject;

/**
 * A command that will be scheduled by a trading class.  All command
 * implementations must be Serializable, because the commands are scheduled
 * through a potentially persistent queue.
 *
 * @author John Wickberg
 */

public interface TradingClassCommand extends Serializable {

    
    Object getInfo();
    
    void setInfo(Object o);
        
    /**
     * Return true if the command should acquire Class level lock with priority. Default is 'false'
     */
    boolean isPriorityCommand();
    
	/**
	 * Completes usage of this command.  This method can be used to return
	 * a completed instance to a pool.
	 */
	 void complete();

	/**
	 * Executes processing of this command.
	 *
	 * @exception Exception allows any exception to be thrown during execution
	 */
	void execute() throws Exception;

    /**
     * Create a log message for this command.  This method will be called by the command processor when
     * this command is taken from the queue.  High usage commands should be careful about how much they
     * log.  Creating any log entry is optional.
     *
     * @param commandProcessor the processor executing this command.  Passed as a BObject so it can be
     *                         used for context for the log message.
     */
     void logCommand(BObject commandProcessor);
     
     /**
      * The following 4 methods for getting and setting results were added in IndexHybrid project for
      * manual quote processing. The requirement was to hold on to 'results' during processing and send
      * them back to the caller after broker processing via a transfer from trading class to trading class
      * command.
      */
     void setResult (int code, String msg);

     String getResultMsg ();

     int getResultCode();

     void clearResults();
     
     void emitExecuteStart();
     
     void emitExecuteEnd();
     
     void setTradingClass(TradingClass tradingClass);
     
     TradingClass getTradingClass();
     
     
}
