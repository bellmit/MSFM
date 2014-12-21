package com.cboe.domain.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.AbstractCollection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.foundationFramework.transactionManagement.BOSession;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.Transaction;
import com.cboe.infrastructureServices.foundationFramework.utilities.TransactionListener;
import com.cboe.interfaces.domain.migratePersistence.Migratable;
import com.cboe.interfaces.domain.migratePersistence.MigratePersistenceHome;
import com.cboe.interfaces.domain.migratePersistence.PersistenceStrategy;

public class BOObjectCollector implements TransactionListener
{
	private static class MapValue extends ConcurrentHashMap<PersistenceStrategy, ConcurrentLinkedQueue<PersistentBObject>>
	{
		public void add(PersistenceStrategy strategy, PersistentBObject object)
		{
		    AbstractCollection<PersistentBObject> entry = getContainer(strategy);

			if (entry.contains(object) == false)
			{
				entry.add(object);
			}
		}

		private AbstractCollection<PersistentBObject> getContainer(PersistenceStrategy strategy)
		{
		    AbstractCollection<PersistentBObject> rval = get(strategy);

			if (rval == null)
			{
				synchronized (this)
				{
					rval = get(strategy);

					if (rval == null)
					{
					    ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue<PersistentBObject>();
					    
					    rval = queue;

						put(strategy, queue);
					}
				}
			}

			return rval;
		}
	}

	private static class LocalMap extends ConcurrentHashMap<BOSession, MapValue>
	{
		public MapValue find(boolean create)
		{
			BOSession session = Transaction.getCallbackSession();

			MapValue rval = get(session);

			if ((rval == null) && create)
			{
				synchronized (this)
				{
					rval = get(session);

					if (rval == null)
					{
						rval = new MapValue();

						if (put(session, rval) != null)
						{
							Log.alarm (Thread.currentThread().getName() + "/" + Thread.currentThread().getId() + 
							        " ##### BOObjectCollector: Replaced session in BOObjectCollector map for BOSession: " + 
							        session);
						}

						Transaction.registerListenerWithPriority(BOObjectCollector.getInstance(), Transaction.LISTENER_ALWAYS_FIRST);
					}
				}
			}

			return rval;
		}
	}

	private LocalMap localMap = new LocalMap();

	private MigratePersistenceHome cachedMigratePersistenceHome;

	public void add(PersistentBObject object, String className)
	{
		MapValue value = localMap.find(true);
		
		PersistenceStrategy handler = lookup(className);

		// the caller of this method is in BOObjectEditorInterceptor which checks for null object.
		value.add(handler, object);
	}
	
	public void commitEvent()
	{
		try
		{
			Exception exception = null;
			try
			{
				process();
			}
			catch (Exception e)
			{
				exception = e;
			}
			
			sendCommitEvent(exception == null);		
			
			// The commitEvent method is called in the Transaction callbacks. 
			// This method should not throw an exception as the other callbacks will not
			// get invoked, which would cause a memory leak.
		}
		finally
		{
			cleanup();
		}
	}

	private void sendCommitEvent(boolean flag)
	{
		MapValue value = localMap.find(false);

		if (value != null)
		{
			Iterator<Entry<PersistenceStrategy, ConcurrentLinkedQueue<PersistentBObject>>> itor = value.entrySet().iterator();

			while (itor.hasNext())
			{
				AbstractCollection<PersistentBObject> entry = itor.next().getValue();
				
				for (PersistentBObject object : entry)
				{
					Migratable migratable = (Migratable) object;
					
					if (migratable != null)
					{
						migratable.postCommit(flag);
					}
				}
			}
		}
	}

	private void process()
	{
		MapValue value = localMap.find(false);

		if (value != null)
		{
			Iterator<Entry<PersistenceStrategy, ConcurrentLinkedQueue<PersistentBObject>>> itor = value.entrySet().iterator();

			while (itor.hasNext())
			{
				Entry<PersistenceStrategy, ConcurrentLinkedQueue<PersistentBObject>> entry = itor.next();
				
				PersistenceStrategy handler = entry.getKey();
				
				try
				{
					handler.acceptObjects(entry.getValue());
				}
				catch (Exception e)
				{
					Log.exception("Exception received on persisting objects asynchronously using " +
					        (handler != null ? handler.getClass().getName() : "null") 
					        + ". Persisting objects synchronously.", e);
					
					persistLocally (entry.getValue());
				}
			}
		}
	}

	private void persistLocally(AbstractCollection<PersistentBObject> list)
	{
		try
		{
			this.getMigratePersistenceHome().handleFailedPersistenceMigration(list);
		}
		catch (Exception e)
		{
			Log.exception ("Failed persisting objects synchronously. The objects and their fields are already logged in the log prior to this message.", e);
		}
	}

	public void rollbackEvent()
    {
        try
        {
            MapValue value = localMap.find(false);

            if (value != null)
            {
                Log.alarm("BOObjectCollector: processing rollback for session: "
                                + value.hashCode());
            }

            sendCommitEvent(false);
        }
        finally
        {
            cleanup();
        }
    }

	private void cleanup()
	{
	    BOSession session = Transaction.getCallbackSession();
	    
        localMap.remove(session);
	}

	private PersistenceStrategy lookup(String className)
	{
		return getMigratePersistenceHome().lookup(className);
	}

	private MigratePersistenceHome getMigratePersistenceHome()
	{
		if (cachedMigratePersistenceHome == null)
		{
			synchronized (this)
			{
				try
				{
					cachedMigratePersistenceHome = (MigratePersistenceHome) HomeFactory.getInstance().findHome(
							MigratePersistenceHome.HOME_NAME);
				}
				catch (Exception e)
				{
					Log.exception(e);
					
					throw new RuntimeException (e);
				}
			}
		}

		return cachedMigratePersistenceHome;
	}

	private static BOObjectCollector instance = new BOObjectCollector();

	public static BOObjectCollector getInstance()
	{
		return instance;
	}
}
