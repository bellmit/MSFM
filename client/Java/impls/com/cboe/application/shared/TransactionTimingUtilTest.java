package com.cboe.application.shared;

// THIS CLASS IS FOR UNIT TESTING ONLY

import java.util.Properties;


import junit.framework.TestCase;

public class TransactionTimingUtilTest extends TestCase {
	volatile Exception exception = null;
	volatile Error error = null;

	protected void setUp() throws Exception {
		super.setUp();
    }

    protected void tearDown() throws Exception
    {
    	TransactionTimingUtil.cleanup();
	if (error != null)
	{
		throw error;
	}
	if (exception != null)
	{
		throw exception;
	}
    }

    public void testDevCASSetEntity() throws Exception
    {
		Properties p = System.getProperties();
		p.setProperty(TransactionTimingUtil.SOURCETYPE_STR, "CAS");
		//p.setProperty(SOURCENUM_FIX_STR, "DFIX0201A");
		p.setProperty(TransactionTimingUtil.HOSTNAME_STR, "dev1cas");		
//		TransactionTimingUtil.initialize();
		long entityID = TransactionTimingUtil.setEntityID();
		assertEquals(entityID, TransactionTimingUtil.getEntityID());
    }

    public void testInvalidSourceType() throws Exception
    {
		Properties p = System.getProperties();
		p.setProperty(TransactionTimingUtil.SOURCETYPE_STR, "UNKNOWN");
		p.setProperty(TransactionTimingUtil.SOURCENUM_FIX_STR, "DFIX101A");
		p.setProperty(TransactionTimingUtil.HOSTNAME_STR, "dev1fix");
		try
		{
			TransactionTimingUtil.initialize();
			TransactionTimingUtil.setEntityID();
			fail();
		}
		catch (IllegalStateException ise)
		{			
		}
		catch (Exception e)
		{
			fail();
		}
    }
    
    public void testInvalidCASNumber() throws Exception
    {
		Properties p = System.getProperties();
		p.setProperty(TransactionTimingUtil.SOURCETYPE_STR, "CAS");
		p.setProperty(TransactionTimingUtil.HOSTNAME_STR, "devacas");
		try
		{
			TransactionTimingUtil.initialize();
			TransactionTimingUtil.setEntityID();
			fail();
		}
		catch (IllegalStateException ise)
		{			
		}
		catch (Exception e)
		{
			fail();
		}
    }
    
    public void testInvalidFIXCASNumber() throws Exception
    {
		Properties p = System.getProperties();
		p.setProperty(TransactionTimingUtil.SOURCETYPE_STR, "FIXCAS");
		p.setProperty(TransactionTimingUtil.HOSTNAME_STR, "dev1fix");
		p.setProperty(TransactionTimingUtil.SOURCENUM_FIX_STR, "DFIX1A1A");
		try
		{
			TransactionTimingUtil.initialize();
			TransactionTimingUtil.getEntityID();
			fail();
		}
		catch (IllegalStateException ise)
		{			
		}
		catch (Exception e)
		{
			fail();
		}
    }
    

    public void testDevFIXCASSetEntity() throws Exception
    {
		Properties p = System.getProperties();
		p.setProperty(TransactionTimingUtil.SOURCETYPE_STR, "FIXCAS");
		//p.setProperty(SOURCENUM_FIX_STR, "DFIX0201A");
		p.setProperty(TransactionTimingUtil.HOSTNAME_STR, "dev1fix");
		p.setProperty(TransactionTimingUtil.SOURCENUM_FIX_STR, "DFIX101A");
		TransactionTimingUtil.initialize();
		long entityID = TransactionTimingUtil.setEntityID();
		assertEquals(entityID, TransactionTimingUtil.getEntityID());
    }
    
    public void testProdCASSetEntity() throws Exception
    {
		Properties p = System.getProperties();
		p.setProperty(TransactionTimingUtil.SOURCETYPE_STR, "CAS");
		//p.setProperty(SOURCENUM_FIX_STR, "DFIX0201A");
		p.setProperty(TransactionTimingUtil.HOSTNAME_STR, "cas0001");
		TransactionTimingUtil.initialize();
		long entityID = TransactionTimingUtil.setEntityID();
		assertEquals(entityID, TransactionTimingUtil.getEntityID());
    }

    public void testProdFIXCASSetEntity() throws Exception
    {
		Properties p = System.getProperties();
		p.setProperty(TransactionTimingUtil.SOURCETYPE_STR, "FIXCAS");
		//p.setProperty(SOURCENUM_FIX_STR, "DFIX0201A");		
		p.setProperty(TransactionTimingUtil.SOURCENUM_FIX_STR, "PFIX504A");
		p.setProperty(TransactionTimingUtil.HOSTNAME_STR, "fix1a");
		TransactionTimingUtil.initialize();
		long entityID = TransactionTimingUtil.setEntityID();
		assertEquals(entityID, TransactionTimingUtil.getEntityID());
    }
    
    public void testMultipleThreads() throws Exception
    {
		Properties p = System.getProperties();
		p.setProperty(TransactionTimingUtil.SOURCETYPE_STR, "CAS");
		//p.setProperty(SOURCENUM_FIX_STR, "DFIX0201A");
		p.setProperty(TransactionTimingUtil.HOSTNAME_STR, "dev1cas");		
		TransactionTimingUtil.initialize();
		long startTime = System.currentTimeMillis();
		final int MAX_CALLS = 1001;
		Thread t1 = new Thread() {
			public void run()
			{
				for (int i=0; i<MAX_CALLS; i++)
				{
					long entityID = TransactionTimingUtil.setEntityID();
					try {
						assertEquals(entityID, TransactionTimingUtil.getEntityID());
					}
					catch (Error e){
						error = e;
						break;
					}
					catch (Exception ex){
						exception = ex;
						break;
					}
//					try {
//						sleep (1);
//					}
//					catch (InterruptedException ie){}
				}				
			}
		};
		Thread t2 = new Thread() {
			public void run()
			{
				for (int i=0; i<MAX_CALLS; i++)
				{
					long entityID = TransactionTimingUtil.setEntityID();
					try {
						assertEquals(entityID, TransactionTimingUtil.getEntityID());
					}
					catch (Error e){
						error = e;
						break;
					}
					catch (Exception ex){
						exception = ex;
						break;
					}
//					try {
//						sleep (1);
//					}
//					catch (InterruptedException ie){}
				}				
			}
		};
		Thread t3 = new Thread() {
			public void run()
			{
				for (int i=0; i<MAX_CALLS; i++)
				{
					long entityID = TransactionTimingUtil.setEntityID();
					try {
						assertEquals(entityID, TransactionTimingUtil.getEntityID());
					}
					catch (Error e){
						error = e;
						break;
					}
					catch (Exception ex){
						exception = ex;
						break;
					}
//					try {
//						sleep (1);
//					}
//					catch (InterruptedException ie){}
				}				
			}
		};
		Thread t4 = new Thread() {
			public void run()
			{
				for (int i=0; i<MAX_CALLS; i++)
				{
					long entityID = TransactionTimingUtil.setEntityID();
					try {
						assertEquals(entityID, TransactionTimingUtil.getEntityID());
					}
					catch (Error e){
						error = e;
						break;
					}
					catch (Exception ex){
						exception = ex;
						break;
					}
//					try {
//						sleep (1);
//					}
//					catch (InterruptedException ie){}
				}				
			}
		};
		Thread t5 = new Thread() {
			public void run()
			{
				for (int i=0; i<MAX_CALLS; i++)
				{
					long entityID = TransactionTimingUtil.setEntityID();
					try {
						assertEquals(entityID, TransactionTimingUtil.getEntityID());
					}
					catch (Error e) {
						error = e;
						break;
					}
					catch (Exception ex){
						exception = ex;
						break;
					}
//					try {
//						sleep (1);
//					}
//					catch (InterruptedException ie){}
				}				
			}
		};
		Thread t6 = new Thread() {
			public void run()
			{
				for (int i=0; i<MAX_CALLS; i++)
				{
					long entityID = TransactionTimingUtil.setEntityID();
					try {
						assertEquals(entityID, TransactionTimingUtil.getEntityID());
					}
					catch (Error e){
						error = e;
						break;
					}
					catch (Exception ex){
						exception = ex;
						break;
					}
//					try {
//						sleep (1);
//					}
//					catch (InterruptedException ie){}
				}				
			}
		};
		Thread t7 = new Thread() {
			public void run()
			{
				for (int i=0; i<MAX_CALLS; i++)
				{
					long entityID = TransactionTimingUtil.setEntityID();
					try {
						assertEquals(entityID, TransactionTimingUtil.getEntityID());
					}
					catch (Error e){
						error = e;
						break;
					}
					catch (Exception ex){
						exception = ex;
						break;
					}
//					try {
//						sleep (1);
//					}
//					catch (InterruptedException ie){}
				}				
			}
		};
		Thread t8 = new Thread() {
			public void run()
			{
				for (int i=0; i<MAX_CALLS; i++)
				{
					long entityID = TransactionTimingUtil.setEntityID();
					try {
						assertEquals(entityID, TransactionTimingUtil.getEntityID());
					}
					catch (Error e){
						error = e;
						break;
					}
					catch (Exception ex){
						exception = ex;
						break;
					}
//					try {
//						sleep (1);
//					}
//					catch (InterruptedException ie){}
				}				
			}
		};
		Thread t9 = new Thread() {
			public void run()
			{
				for (int i=0; i<MAX_CALLS; i++)
				{
					long entityID = TransactionTimingUtil.setEntityID();
					try {
						assertEquals(entityID, TransactionTimingUtil.getEntityID());
					}
					catch (Error e){
						error = e;
						break;
					}
					catch (Exception ex){
						exception = ex;
						break;
					}
//					try {
//						sleep (1);
//					}
//					catch (InterruptedException ie){}
				}				
			}
		};
		Thread t10 = new Thread() {
			public void run()
			{
				for (int i=0; i<MAX_CALLS; i++)
				{
					long entityID = TransactionTimingUtil.setEntityID();
					try {
						assertEquals(entityID, TransactionTimingUtil.getEntityID());
					}
					catch (Error e) {
						error = e;
						break;
					}
					catch (Exception ex){
						exception = ex;
						break;
					}
//					try {
//						sleep (1);
//					}
//					catch (InterruptedException ie){}
				}				
			}
		};
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
		t7.start();
		t8.start();
		t9.start();
		t10.start();
		try 		{
			t1.join();
		}
		catch (InterruptedException ie){}
		try 		{
			t2.join();
		}
		catch (InterruptedException ie){}
		try 		{
			t3.join();
		}
		catch (InterruptedException ie){}
		try		{
			t4.join();
		}
		catch (InterruptedException ie){}
		try 		{
			t5.join();
		}
		catch (InterruptedException ie){}
		try 		{
			t6.join();
		}
		catch (InterruptedException ie){}
		try 		{
			t7.join();
		}
		catch (InterruptedException ie){}
		try 		{
			t8.join();
		}
		catch (InterruptedException ie){}
		try		{
			t9.join();
		}
		catch (InterruptedException ie){}
		try 		{
			t10.join();
		}
		catch (InterruptedException ie){}
		long endTime = System.currentTimeMillis();
		System.out.println("Time elapsed for " + (10 * MAX_CALLS) + " calls is " + (endTime-startTime) + " milliseconds");
    }
}
