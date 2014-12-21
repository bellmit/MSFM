package com.cboe.infrastructureServices.persistenceService;

import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
/**
 * @author Dave Hoag
 * @version 2.0
 */
public interface PersistenceService
{
	/**
	 * Find the broker with the associated key value.
	 * @return String if a broker is registered, null if no value found
	 */
	String getBrokerName(String key);
	boolean initialize(ConfigurationService configService);
	void goMaster();
	void goSlave();
	
	public static final String NULL_BROKER="nullBroker";
	public static final String DEFAULT_BROKER="defaultBroker";
	public static final String TRANSIENT_BROKER="transient";
	public static final String POOLED_BROKER ="pooledBroker";
	public static final String MIGRATE_PERSISTENCE_BROKER="migratePersistence";
	public static final String BATCH_BROKER="batchBroker";
	
	public static final String CHANNEL_BROKER="channelBroker";
	public static final String POOLED_CHANNEL_BROKER="pooledChannelBroker";
	
}
