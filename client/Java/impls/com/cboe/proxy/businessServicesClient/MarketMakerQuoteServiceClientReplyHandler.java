package com.cboe.proxy.businessServicesClient;

import com.cboe.idl.businessServices.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.util.ServerResponseStruct;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.util.*;
import java.util.*;
import com.cboe.client.util.ClientObjectResolver;
import com.cboe.interfaces.businessServicesClient.*;

/**
 *      Implementation specific to MarketMakerQuoteServiceClient. Also implements the AMI interface.
 *
 *      @date January 07, 2009
 *
 */

public  class MarketMakerQuoteServiceClientReplyHandler extends POA_AMI_MarketMakerQuoteServiceHandler
        implements  ReplyHandlerClient

{

    private int     numberOfRequests;
    private int     numberOfResponses;
    private int     numberOfExceptions;
    private boolean handlerState;
    private RFQStruct[] rfqArray;
    private	Vector 	rfqStructVector;
    private MarketMakerQuoteServiceClientReplyHandlerManager replyHandlerManager;

    private AMI_MarketMakerQuoteServiceHandler narrowedReplyHandler;

    private static final RFQStruct[] EMPTY_RFQStruct_ARRAY = new RFQStruct[0];

    public static final String HANDLER_POA_NAME = "AMIHandlerPOA";

    /**
     * MarketMakerQuoteServiceReplyHandler constructor establishes a connection with the ORB
     */

    public MarketMakerQuoteServiceClientReplyHandler(MarketMakerQuoteServiceClientReplyHandlerManager rhm)
    {
        super();
        rfqStructVector = new Vector();
        setReplyHandlerManager(rhm);

    }

    public void initialize()
    {
        // Automatically activate the handler

        com.cboe.infrastructureServices.orbService.OrbService orb = FoundationFramework.getInstance().getOrbService();
        try
        {
            org.omg.CORBA.Object obj = orb.connect( HANDLER_POA_NAME, this );
            //org.omg.CORBA.Object obj = orb.connect( com.cboe.infrastructureServices.orbService.OrbServicePOAImpl.HANDLER_POA_NAME, this );

            narrowedReplyHandler = (AMI_MarketMakerQuoteServiceHandler) ClientObjectResolver.resolveObject(obj, AMI_MarketMakerQuoteServiceHandlerHelper.class.getName() );

        }
        catch( NoSuchPOAException e )
        {
            Log.information( "MarketMakerQuoteServiceReplyHandler: Error initializing reply handler" );
            Log.exception( e );
        }
    }

    public void setReplyHandlerManager(MarketMakerQuoteServiceClientReplyHandlerManager rhm)
    {
        try
        {
            if (rhm == null)
            {
                throw new Exception();
            }
            replyHandlerManager = rhm;
        }
        catch(Exception e)
        {
            Log.information("MarketMakerQuoteServiceClientReplyHandler: Null " +
                "MarketMakerQuoteServiceClientReplyHandlerManager passed to setReplyHandlerManager.");
            Log.exception(e);
        }
    }

    public MarketMakerQuoteServiceClientReplyHandlerManager getReplyHandlerManager()
    {
        return replyHandlerManager;
    }

    public 	void setNumberOfRequests( int value )
    {
        numberOfRequests = value;
    }

    public  AMI_MarketMakerQuoteServiceHandler getAMIHandler()
    {
        return narrowedReplyHandler;
    }

    /**
     * Returns the number of exceptions returned
    **/

    public  int getNumberOfExceptions()
    {
        return numberOfExceptions;
    }

    /**
     * Returns the number of responses pending
     */

    public  int getNumberOfPendingResponses()
    {
        return (  numberOfRequests - ( numberOfResponses + numberOfExceptions ) );
    }

    /**
     * Returns the number of requests to be forwarded
    **/

    public  int getNumberOfRequests()
    {
        return numberOfRequests;
    }


    public	void cancelQuotesByClass()
    {
    }

    public  void cancelQuotesByClass_excep(AMI_MarketMakerQuoteServiceExceptionHolder ami_marketMakerQuoteServiceExceptionHolder)
    {
    }

    public void systemCancelQuotesByClass()
    {
    }

    public void systemCancelQuotesByClass_excep(AMI_MarketMakerQuoteServiceExceptionHolder ami_marketMakerQuoteServiceExceptionHolder)
    {
    }


    public void acceptQuote()
    {
    }

    public void acceptQuote_excep(com.cboe.idl.businessServices.AMI_MarketMakerQuoteServiceExceptionHolder excep_holder)
    {
    }

    public void acceptQuoteV4()
    {
    }

    public void acceptQuoteV4_excep(com.cboe.idl.businessServices.AMI_MarketMakerQuoteServiceExceptionHolder excep_holder)
    {
    }

    public void acceptQuotesForClass(com.cboe.idl.cmiQuote.ClassQuoteResultStruct[] ami_return_val)
    {
    }

    public void acceptQuotesForClass_excep(com.cboe.idl.businessServices.AMI_MarketMakerQuoteServiceExceptionHolder excep_holder)
    {
    }

    public void acceptQuotesForClassV3(com.cboe.idl.cmiQuote.ClassQuoteResultStructV3[] ami_return_val)
    {
    }

    public void acceptQuotesForClassV3_excep(com.cboe.idl.businessServices.AMI_MarketMakerQuoteServiceExceptionHolder excep_holder)
    {
    }

    public void getQuoteForProduct(com.cboe.idl.quote.InternalQuoteStruct ami_return_val)
    {
    }

    public void acceptQuotesForClassV4(com.cboe.idl.cmiQuote.ClassQuoteResultStructV3[] ami_return_val)
    {
    }

    public void acceptQuotesForClassV4_excep(com.cboe.idl.businessServices.AMI_MarketMakerQuoteServiceExceptionHolder excep_holder)
    {
    }


    public void getQuoteForProduct_excep(com.cboe.idl.businessServices.AMI_MarketMakerQuoteServiceExceptionHolder excep_holder)
    {
    }


    public void cancelQuote()
    {
    }

    public void cancelQuote_excep(com.cboe.idl.businessServices.AMI_MarketMakerQuoteServiceExceptionHolder excep_holder)
    {
    }

    public void acceptManualQuote_excep(AMI_MarketMakerQuoteServiceExceptionHolder excep_holder)
    {
    }

    public void acceptManualQuote()
    {
    }

    public void cancelManualQuote_excep(AMI_MarketMakerQuoteServiceExceptionHolder excep_holder)
    {
    }

    public void cancelManualQuoteWithReason_excep(AMI_MarketMakerQuoteServiceExceptionHolder excep_holder)
    {
    }
    public void cancelManualQuote()
    {
    }

    public void cancelManualQuoteWithReason()
    {
    }

    public void cancelAllQuotes()
    {
        synchronized( this )
        {
            numberOfResponses++;
            if (( numberOfResponses + numberOfExceptions ) >= numberOfRequests ) // ">=" in case numberOfRequests isn't set...
            {
                getReplyHandlerManager().returnReplyHandler(this);
            }
        }
    }

    public void cancelAllQuotes_excep(com.cboe.idl.businessServices.AMI_MarketMakerQuoteServiceExceptionHolder excep_holder)
    {
        synchronized ( this )
        {
            numberOfExceptions++;
            if(numberOfExceptions >= numberOfRequests) // ">=" in case numberOfRequests isn't set...
            {
                getReplyHandlerManager().returnReplyHandler(this);
            }
        }
    }


    public void requestForQuote()
    {
    }

    public void requestForQuote_excep(com.cboe.idl.businessServices.AMI_MarketMakerQuoteServiceExceptionHolder excep_holder)
    {
    }

    /**
     * Appends the RFQStruct vector with the incoming array
     * Releases the object wait once all responses have been received
    **/

    public void getRFQ(com.cboe.idl.cmiQuote.RFQStruct[] ami_return_val)
    {
    	if ( ami_return_val.length != 0 )
    	{
    		for ( int i=0; i < ami_return_val.length ; i++ )
    		{
    			rfqStructVector.addElement( ami_return_val[i] );
    		}
    	}

    	synchronized( this )
    	{
    		numberOfResponses++;
    		if (( numberOfResponses + numberOfExceptions ) >= numberOfRequests ) // ">=" in case numberOfRequests isn't set...
    		{
    			rfqStructVector.copyInto( rfqArray );
    			handlerState = true;
    			rfqStructVector.removeAllElements();
    			notifyAll();
                getReplyHandlerManager().returnReplyHandler(this);
    		}
    	}
    }

    public void getRFQ_excep(com.cboe.idl.businessServices.AMI_MarketMakerQuoteServiceExceptionHolder excep_holder)
    {
        synchronized( this )
        {
            numberOfExceptions++;
            if (( numberOfResponses + numberOfExceptions ) >= numberOfRequests ) // ">=" in case numberOfRequests isn't set...
            {
                handlerState = true;
                notifyAll();
                getReplyHandlerManager().returnReplyHandler(this);
            }
        }
    }

    public int getNumberOfResponses()
    {
    	return numberOfResponses;
    }

    public RFQStruct[] getQuoteData()
    {
    	if ( rfqArray == null )
    	{
    		rfqArray = EMPTY_RFQStruct_ARRAY;
    	}

    	return rfqArray;
    }

    public 	boolean isReady()
    {
    	return handlerState;
    }

    public	void	reset()
    {
    	numberOfRequests = 0;
    	numberOfResponses = 0;
    	numberOfExceptions = 0;
    	handlerState = false;
    	rfqArray = null;
    }

    public void cancelQuotesForUsers(ServerResponseStruct[] serverResponseStructs)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void cancelQuotesForUsers_excep(
            AMI_MarketMakerQuoteServiceExceptionHolder ami_marketMakerQuoteServiceExceptionHolder)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void getQuoteCountForUsers(ServerResponseStruct[] serverResponseStructs)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void getQuoteCountForUsers_excep(
            AMI_MarketMakerQuoteServiceExceptionHolder ami_marketMakerQuoteServiceExceptionHolder)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}//EOF
