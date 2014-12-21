package com.cboe.interfaces.domain.migratePersistence;

import java.util.AbstractCollection;

import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;

public interface MigratePersistenceHome
{
	public String HOME_NAME = "MigratePersistenceHome";
	
	public String LOCAL_DIRECT_PERSISTENCE_STRATEGY = "LocalDirect";
	
	public String BEHIND_QUEUE_PERSISTENCE_STRATEGY = "LocalBehindQueue";
	
	public String EVENT_CHANNEL_PERSISTENCE_STRATEGY = "EventChannel";
	
	PersistenceStrategy lookup (String className);
	
	void handleFailedPersistenceMigration(AbstractCollection<PersistentBObject> list) throws Exception;
	
	void persist (AbstractCollection<PersistentBObject> list);
	
	/*
	 * During persistence the PersistentBObject is added to MigratePersistenceHome which has the 
	 * default Oracle broker. So irrespective of the broker associated with the home to which the
	 * PBO belongs, the object will always get persisted.
	 * In such cases, as in SpreadTradeServer, we use LocalDirect mechanism to persist objects.
	 * The DirectPersistendce strategy calls the following method for persisting objects to the database.
	 */
	void persistWithRespectiveHome (AbstractCollection<PersistentBObject> list);
}
