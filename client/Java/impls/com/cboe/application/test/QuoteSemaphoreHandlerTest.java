package com.cboe.application.test;

import com.cboe.application.quote.common.QuoteSemaphoreHandler;
import com.cboe.exceptions.NotAcceptedException;

import junit.framework.TestCase;

public class QuoteSemaphoreHandlerTest extends TestCase 
{
	public QuoteSemaphoreHandlerTest(String args)
	{
		super(args);
	}
	
    protected void setUp() throws Exception
    {
        super.setUp();
        System.setProperty(QuoteSemaphoreHandler.MAX_CONCURRENT_QUOTES_PER_CLASS, "2");
    }
    
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testQuoteEntryAccess()
    {
		int completed = 0;
    	try
    	{
    		int nLocks = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X01", "W_MAIN", 10001);
    		assertTrue(nLocks == 1);
    		++completed;
	    	nLocks = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X01", "W_MAIN", 10001);
    		assertTrue(nLocks == 2);
    		++completed;
    		try
    		{
    	    	nLocks = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X01", "W_MAIN", 10001);
        		assertTrue(3 == 2);    			
        		++completed;
    		}
    		catch (NotAcceptedException nae)
    		{
    			assertTrue(2 == 2);
    		}
	    	int nLocks2 = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X01", "W_MAIN", 10002); // different class
    		assertTrue(nLocks2 == 1);
    		++completed;
	    	int nLocks3 = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X01", "W_STOCK", 10002); // different session
    		assertTrue(nLocks3 == 1);
    		++completed;
	    	int nLocks4 = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X02", "W_MAIN", 10001); // different user
    		assertTrue(nLocks4 == 1);
    		++completed;
	    	QuoteSemaphoreHandler.releaseQuoteEntryAccess("X01", "W_MAIN", 10001); // releasing one of the locks of 10001 
    		++completed;
	    	nLocks = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X01", "W_MAIN", 10001);
    		assertTrue(nLocks == 2);
    		++completed;
	    	QuoteSemaphoreHandler.releaseQuoteEntryAccess("X01", "W_MAIN", 10001);
    		++completed;
	    	QuoteSemaphoreHandler.releaseQuoteEntryAccess("X01", "W_MAIN", 10001);
    		++completed;
    		QuoteSemaphoreHandler.cleanupSemaphores("X02"); // cleaning up user X02; followed by a release
    		++completed;
	    	QuoteSemaphoreHandler.releaseQuoteEntryAccess("X02", "W_MAIN", 10001);
    		++completed;
	    	nLocks4 = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X02", "W_MAIN", 10001); 
    		assertTrue(nLocks4 == 1);
    		++completed;
	    	QuoteSemaphoreHandler.releaseQuoteEntryAccess("X02", "W_MAIN", 10001);
    		++completed;
    		QuoteSemaphoreHandler.cleanupSemaphores("X01"); 
    		QuoteSemaphoreHandler.cleanupSemaphores("X02"); 
    	}
    	catch (Exception e)
    	{
    		assertTrue(0 == completed);
    	}
    }

    public void testQuoteCancelByClassAccess()
    {
		int completed = 0;
    	try
    	{
    		QuoteSemaphoreHandler.acquireQuoteCancelByClassAccess("X01", "W_MAIN", 10001);
    		++completed;
    		int nLocks = 0;
    		try
    		{
    	    	nLocks = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X01", "W_MAIN", 10001);
        		assertTrue(1 == 0);
    		}
    		catch (NotAcceptedException nae)
    		{
    			assertTrue(0 == 0);
    		}
    		++completed;
    		try
    		{
    	    	QuoteSemaphoreHandler.acquireSingleQuoteCancelAccess("X01", "W_MAIN", 10001);
        		assertTrue(1 == 0);
    		}
    		catch (NotAcceptedException nae)
    		{
    			assertTrue(0 == 0);
    		}
    		++completed;
    		QuoteSemaphoreHandler.acquireQuoteCancelByClassAccess("X01", "W_MAIN", 10001); // calling again
    		++completed;
	    	int nLocks2 = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X01", "W_MAIN", 10002); // different class
    		assertTrue(nLocks2 == 1);
	    	int nLocks3 = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X01", "W_STOCK", 10002); // different session
    		assertTrue(nLocks3 == 1);
    		++completed;
	    	int nLocks4 = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X02", "W_MAIN", 10001); // different user
    		assertTrue(nLocks4 == 1);
    		++completed;
	    	QuoteSemaphoreHandler.releaseQuoteCancelByClassAccess("X01", "W_MAIN", 10001); // still one lock left
    		++completed;
    		try
    		{
    	    	nLocks = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X01", "W_MAIN", 10001);
        		assertTrue(1 == 0);
    		}
    		catch (NotAcceptedException nae)
    		{
    			assertTrue(0 == 0);
    		}
    		++completed;
    		QuoteSemaphoreHandler.cleanupSemaphores("X02"); 
	    	QuoteSemaphoreHandler.releaseQuoteCancelByClassAccess("X01", "W_MAIN", 10001); // no more lock left
    		++completed;   		
	    	nLocks = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X01", "W_MAIN", 10001);
    		assertTrue(nLocks == 1);
	    	QuoteSemaphoreHandler.acquireQuoteCancelByClassAccess("X01", "W_MAIN", 10001); // one lock left
    		++completed;
    		try
    		{
    	    	nLocks = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X01", "W_MAIN", 10001);
        		assertTrue(1 == 0);
    		}
    		catch (NotAcceptedException nae)
    		{
    			assertTrue(1 == 1);
    		}
    		++completed;
    		QuoteSemaphoreHandler.cleanupSemaphores("X01");
	    	QuoteSemaphoreHandler.releaseQuoteEntryAccess("X01", "W_MAIN", 10001);
    		++completed;
	    	QuoteSemaphoreHandler.releaseQuoteCancelByClassAccess("X01", "W_MAIN", 10001); // no more lock left
    		++completed;   		
	    	nLocks = QuoteSemaphoreHandler.acquireQuoteEntryAccess("X01", "W_MAIN", 10001);
    		assertTrue(nLocks == 1);
	    	QuoteSemaphoreHandler.releaseQuoteEntryAccess("X01", "W_MAIN", 10001);
    		++completed;   		
    	}
    	catch (Exception e)
    	{
    		assertTrue(0 == completed);
    	}
    }

}
