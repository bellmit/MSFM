package com.cboe.domain.util;

import java.io.Serializable;
import java.util.Vector;

import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.FatalFoundationFrameworkException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.Transaction;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.interfaces.domain.OrderIdGeneratorHome;
import com.cboe.server.distributedCache.DistributedCacheListener;
import com.cboe.server.distributedCache.DistributedCacheValue;
import com.cboe.server.distributedCache.GenericDataSyncHome;
import com.cboe.server.util.FFStatusQuery;

/**
 * This is the implementation of OrderIdGenerator and extends
 * OrderIdGenerator default implementation.
 * This class provides functionality to retrieve and save OrderIdSeed to
 * database.   
 * @author Ravi Rade
 */

public class OrderIdGeneratorHomeImpl extends OrderIdGeneratorHomeDefaultImpl
    implements OrderIdGeneratorHome,DistributedCacheListener<Object, DistributedCacheValue<Serializable>> {
    private GenericDataSyncHome<Serializable>   genericDataSyncHome;

    //Default construtor
    public OrderIdGeneratorHomeImpl(){
        //call super() so that date format and is set up.
        super();
        setSmaType("GlobalOrderIdGenerator.OrderIdGeneratorHomeImpl");

    }

    protected void saveBranch(String branch){

        boolean success = false;
        try {
            Transaction.startTransaction();
            getOrderIdSeed().setUsedBranch(branch);
            success = Transaction.commit();
            Log.information("Saving branch to DB:"+branch);
            genericDataSyncHome.put(GenericDataSyncHome.BRANCH,branch);
        }catch (PersistenceException e) {
            Log.exception(this,"Persistence Exception occured while saving Order Id Seed", e);            
        }finally {
            if(!success){
                Transaction.rollback();
            }
        }
    }

 
    protected String getNextBranch() throws SystemException,PersistenceException   {

        String returnValue;
        OrderIdSeedImpl orderIdSeed= getOrderIdSeed();
        if(orderIdSeed == null) {
            boolean success = false;
            try {
                Transaction.startTransaction();
                orderIdSeed = new OrderIdSeedImpl();
                addToContainer(orderIdSeed);
                orderIdSeed.initializeObjectIdentifier();
                orderIdSeed.setUsedBranch(getStartingBranch());
                success = Transaction.commit();
            }finally {
                if(!success){
                    Transaction.rollback();
                }
            }
            returnValue = getStartingBranch();
        }else{
             String nextBranch = incrementBranch(orderIdSeed.getUsedBranch());
             saveBranch(nextBranch);
             returnValue = nextBranch;
        }
        Log.information(this, "OrderIdGenerator STARTING BRANCH: " + returnValue);
        return returnValue;
    }

    private OrderIdSeedImpl getOrderIdSeed() throws PersistenceException{

        OrderIdSeedImpl subject = new OrderIdSeedImpl();
        addToContainer(subject);

		ObjectQuery query = new ObjectQuery(subject);
        Vector results = query.find();

        OrderIdSeedImpl returnValue = null;

        if(results == null || results.size() == 0) {
            Log.information(this, "Query for OrderIdSeedImpl returned no rows.  Will create new seed.");
        } else{
            if(results.size() > 1){
                Log.alarm(this, "Query for OrderIdSeedImpl returned more than one row.  Using first row returned.");
            }
            returnValue = (OrderIdSeedImpl) results.firstElement();
            addToContainer(returnValue);
        }

        return returnValue;
    }


//For testing :
    public static void main(String[] args){

        OrderIdGeneratorHomeImpl gen = new OrderIdGeneratorHomeImpl();
//        gen.branch = "AAA";
//        gen.branchSequenceNumber = 1;
        gen.goMaster(true);
        gen.MAX_BRANCH_SEQUENCE_NUMBER = 3;

        for (int i = 0 ; i < 20;i++) {
            OrderIdStruct orderId = gen.generateOrderId(new OrderStruct());
            System.out.println("Branch" + orderId.branch + " - " + orderId.branchSequenceNumber);
        }

    }

	public void goSlave() {
		try {
			genericDataSyncHome = (GenericDataSyncHome) HomeFactory.getInstance().findHome(GenericDataSyncHome.HOME_NAME);
		} catch (CBOELoggableException e) {
			throw new FatalFoundationFrameworkException(e,GenericDataSyncHome.HOME_NAME+" Not Found");
		}

		if (!FFStatusQuery.getInstance().isStartedInMasterMode())
		{
			genericDataSyncHome.addListener(GenericDataSyncHome.BRANCH, this);
			DistributedCacheValue<Serializable> value = genericDataSyncHome.get(GenericDataSyncHome.BRANCH);
			if (value !=null)
			{
				this.branch = (String)value.getValue();
			}
		}
		else
		{
			try {
				this.branch = getNextBranch();
			}catch (SystemException e) {
				Log.exception(this,"System Exception thrown while reading DB for OrderIdSeed.", e);
			}catch(PersistenceException e) {
				Log.exception("Persistence Exception thrown while reading DB for OrderIdSeed.", e);
			}        		
		}
		
        Log.information(this, "OrderIdGenerator STARTING BRANCH: " + this.branch);
	}
    
	public synchronized void goMaster(boolean fastFailover) {
		//started in master mode or slow failover
		if (!fastFailover)
		{
			super.goMaster(fastFailover);
		}
		else
		{
			this.branch = incrementBranch(this.branch);
            Thread t = new Thread()
            {
            	public void run()
            	{
                    saveBranch(branch);
            		
            	}
            };
            t.start();
		}
		//failover
		if (!FFStatusQuery.getInstance().isStartedInMasterMode())
		{
			genericDataSyncHome.removeListener(GenericDataSyncHome.BRANCH, this);
		}
        Log.information(this, "OrderIdGenerator STARTING BRANCH: " + this.branch);
	}

	public void clearEvent() {
	}

	public void removeEvent(Object key, DistributedCacheValue<Serializable> oldValue) {
	}

	public void updateEvent(Object key,
			DistributedCacheValue<Serializable> updatedValue) 
	{
		
		if (key.equals(GenericDataSyncHome.BRANCH))
		{
			this.branch = (String)updatedValue.getValue();
		}
	}




}
