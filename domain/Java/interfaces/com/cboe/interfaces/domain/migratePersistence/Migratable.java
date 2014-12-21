package com.cboe.interfaces.domain.migratePersistence;

import com.cboe.util.Copyable;

public interface Migratable extends java.io.Externalizable, Copyable
{
	String DASH_D_PROPERTY = "turnOffMigratePersistenceFeature";
	
	String PROPERTY_YES = "yes";
	
	String PROPERTY_NO = "no";
	
	int getKey();
	
	void turnOffPersistentBObjectInterceptor(boolean turnOff);
	
	String toStringDatabaseFields ();
	
	void postCommit(boolean success);
}
