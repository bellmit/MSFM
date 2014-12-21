package com.cboe.domain.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.foundationFramework.transactionManagement.BOSession;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.Transaction;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.interfaces.domain.migratePersistence.Migratable;
import com.objectwave.persist.Broker;
import com.objectwave.persist.BrokerFactory;
import com.objectwave.persist.ObjectPool;
import com.objectwave.persist.QueryException;
import com.objectwave.persist.RDBBroker;

public class BOObjectEditorInterceptor extends DBAdapter
{
	private static final long serialVersionUID = 1L;

	private AtomicReference<PersistentBObject> objectReference = new AtomicReference<PersistentBObject>();

	private String className;

	private AtomicBoolean status = new AtomicBoolean(true);

	private AtomicBoolean preTransactionRetrieveFromDatabaseFlag = new AtomicBoolean(false);

	public BOObjectEditorInterceptor(PersistentBObject object, String className)
	{
		super(object);

		this.objectReference.set(object);
		
		this.className = className;

		setStatus(true);
	}

	public boolean isOn()
	{
		return status.get();
	}

	// no point in turning interceptor on if the underlying object doesn't
	// implement Migratable interface.
	public void setStatus(boolean on)
	{
		status.set((getObject() instanceof Migratable) && on);
	}

	private void debug(String string)
	{
		if (Log.isDebugOn())
		{
			BOSession session = Transaction.getCallbackSession();

			Log.information(Thread.currentThread().getName() + "/" + this.getClass().getCanonicalName() + " " + string
					+ " session: " + session.hashCode() + ", class: " + className);
		}
	}

	public PersistentBObject getObject()
	{
		return objectReference.get();
	}

	private void preSave()
	{
		if (isOn())
		{
			// save isRetrievedFromDatabase flag.
			preTransactionRetrieveFromDatabaseFlag.set(this.isRetrievedFromDatabase());

			debug("Setting broker to nullBroker");
			// change broker.
			//
			this.getPersistentObject().setBrokerName("nullBroker");
		}
	}

	private void postSave()
	{
		if (isOn())
		{
			// since we set the broker to 'nullBroker' we have to mark the
			// object as retrieved from database
			this.getPersistentObject().setRetrievedFromDatabase(true);

			debug("Marked object as retrieved from database");
		}
	}

	public void save() throws QueryException
	{
		String brokerName = this.getPersistentObject().getBrokerName();
		boolean isDirty = this.getPersistentObject().isDirty();
		boolean isTransient = this.getPersistentObject().isTransient();

		try
		{
			preSave();

			super.save();

			postSave();

			if (isOn() && isDirty && !isTransient)
			{
			    if (getObject() != null)
			    {
			        BOObjectCollector.getInstance().add(getObject(), className);
			    }
			    else
			    {
			        Log.exception(Thread.currentThread().getName() + "/" + this.getClass().getName() + 
			                "Async persistence ERROR: got NULL object for class name: " + className, 
			                new Exception());
			    }
			}

			if (Log.isDebugOn() && isOn() && isDirty && isTransient)
			{
				Log.information(Thread.currentThread().getId() + "/" + Thread.currentThread().getName()
						+ " BOObjectEditorInterceptor.save(). Ignoring object as it's transient. " + this.hashCode() + "/"
						+ className);
			}
		}
		catch (Exception e)
		{
			Log.exception("Exception caught while saving object of class: " + className + " " + toStringDatabaseFields(), e);

			throw new RuntimeException(e);
		}
		finally
		{
			if (isOn())
			{
				this.getPersistentObject().setBrokerName(brokerName);

				doBrokerSpecificChange();
			}

			if (Log.isDebugOn())
			{
				Log.information(Thread.currentThread().getId() + "/" + Thread.currentThread().getName()
						+ " BOObjectEditorInterceptor.save(). Done saving object. " + this.hashCode() + "/" + className + "/"
						+ brokerName);
			}
		}
	}

	private String toStringDatabaseFields()
	{
		try
		{
			Migratable m_obj = (Migratable) this.getObject();
		
			if (m_obj != null)
			{
				return m_obj.toStringDatabaseFields();
			}
		}
		catch (Exception e)
		{
		}
		
		return "";
	}

	private void doBrokerSpecificChange()
	{
		try
		{
			String brokerName = this.getPersistentObject().getBrokerName();

			if ((brokerName != null) && (brokerName.compareTo("pooledBroker") == 0) && !getPreTransactionRetrievedFromDatabaseFlag()
					&& this.getPersistentObject().isRetrievedFromDatabase())
			{
				Broker broker = BrokerFactory.getBroker(brokerName);

				if ((broker != null) && (broker instanceof RDBBroker))
				{
					RDBBroker rdbBroker = (RDBBroker) broker;
					
					ObjectPool pool = rdbBroker.getObjectPool();
					
					if (pool != null)
					{
						pool.put(this.getPersistentObject());
					}
				}
			}
		}
		catch (Exception e)
		{
			Log.exception(e);
		}
	}

	public boolean getPreTransactionRetrievedFromDatabaseFlag()
	{
		return preTransactionRetrieveFromDatabaseFlag.get();
	}
}
