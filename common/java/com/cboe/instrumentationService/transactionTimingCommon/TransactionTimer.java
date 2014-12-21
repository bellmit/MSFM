package com.cboe.instrumentationService.transactionTimingCommon;

public interface TransactionTimer
{
    public static int Enter = 0;
    public static int Leave = 1;
    public static int LeaveWithException = 2;


    /**
     * sendTransactionMethodEvent -- record this point in a transaction
     * @param long methodID -- which method is the transaction in
     * @param long entityID -- which is the main object involved in the transaction
     * @param int direction -- is the entity entering or leaving the method
     * @author CGM
     */
    public void sendTransactionMethodEvent( long methodID, long entityID, int direction );


    /**
     * sendTransactionQueueID -- record the movement of the entity across a queue
     * @param long queueID -- which queue is being traversed
     * @param long entityID -- which object is traversing the queue
     * @param int queueSize -- how large is the queue after the addition or removal of the object
     * @param int direction -- is the object entering or leaving the queue
     * @author CGM
     */
    public void sendTransactionQueueEvent( long queueID, long entityID, int queueSize, int direction );



    /**
     * long registerTransactionIdentifier -- get a unique identifier for a string
     * @param String identifier -- the identifier to register. This identifier should completely identify the
     *         associated method or queue. Example for a method:
     *           com.cboe.businessServices.brokerService.BrokerServiceImpl.acceptQuote(Lcom.cboe.interfaces.domain.Quote;)V
     * @return a number which corresponds to the identifier 
     * @author CGM
     */
    public long registerTransactionIdentifier( String identifier );

	 public void setThreadIdentifier( int threadID );
	 public long getEntityID();
	 public long getStartingTimeMillis();
	 public void setEntityID( long entityID );
	 public void setTTContext( long entityID, long startingTimeMillis );


}
