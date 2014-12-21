package com.cboe.interfaces.domain.migratePersistence;

public interface PersistenceStrategyProperties
{
	String getStrategyName();
	
	String getImplClassName();
	
	int getNumberQueues();
	
	int getNumberEventChannels();
	
	int getBlockingFactor();

	int getReadTimeoutInMillis();
}
