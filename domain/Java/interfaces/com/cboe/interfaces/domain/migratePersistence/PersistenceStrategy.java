package com.cboe.interfaces.domain.migratePersistence;

import java.util.AbstractCollection;

import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;

public interface PersistenceStrategy
{
	void acceptObjects (AbstractCollection<PersistentBObject> list) throws SystemException;
	
	void setBOHome (BOHome home);
	
	void goSlave();
	
	void goMaster();
	
	void init(PersistenceStrategyProperties properties);
	
	PersistenceStrategyProperties getProperties();
}
