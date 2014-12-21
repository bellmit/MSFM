package com.cboe.instrumentationService.transactionTimingCommon;

public interface TransactionTimerFactory
{
   public TransactionTimer getTransactionTimer();
	public TransactionTimer getLocalTransactionTimer();
	public TransactionTimer getLocalTransactionTimer( String fileName );
	public TransactionTimer getRemoteTransactionTimer();
	public TransactionTimer getRemoteTransactionTimer( String channelName );
	public TransactionTimer getServiceContextTransactionTimer();
}
