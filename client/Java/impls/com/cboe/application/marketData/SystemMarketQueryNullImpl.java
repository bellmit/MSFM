package com.cboe.application.marketData;

/**
 * @author Tom Lynch
 */
import java.util.*;
import com.cboe.application.shared.*;
import com.cboe.application.supplier.*;
import com.cboe.application.supplier.proxy.*;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.businessServices.*;
import com.cboe.interfaces.events.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.product.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.application.shared.consumer.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.util.channel.*;

public class SystemMarketQueryNullImpl extends BObject implements MarketQuery
{

    // Initialize with the User's Session Manager
    private SessionManager sessionManager;

    /**
    * SystemMarketQueryImpl constructor comment.
    */
    public SystemMarketQueryNullImpl()
    {
        super();
    }


    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void subscribeRecapForClass(String sessionName, int classKey,CMIRecapConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
      if ( 1 == 0 ) throw new SystemException();
      if ( 1 == 0 ) throw new CommunicationException();
      if ( 1 == 0 ) throw new AuthorizationException();
      if ( 1 == 0 ) throw new DataValidationException();
   }

    public void subscribeRecapForProduct(String sessionName, int productKey,CMIRecapConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
      if ( 1 == 0 ) throw new SystemException();
      if ( 1 == 0 ) throw new CommunicationException();
      if ( 1 == 0 ) throw new AuthorizationException();
      if ( 1 == 0 ) throw new DataValidationException();
   }

    public void subscribeCurrentMarketForProduct(String sessionName, int productKey,CMICurrentMarketConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
      if ( 1 == 0 ) throw new SystemException();
      if ( 1 == 0 ) throw new CommunicationException();
      if ( 1 == 0 ) throw new AuthorizationException();
      if ( 1 == 0 ) throw new DataValidationException();
   }

   public void subscribeNBBOForProduct(String sessionName, int productKey,CMINBBOConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
      if ( 1 == 0 ) throw new SystemException();
      if ( 1 == 0 ) throw new CommunicationException();
      if ( 1 == 0 ) throw new AuthorizationException();
      if ( 1 == 0 ) throw new DataValidationException();
   }

    public BookDepthStruct getBookDepth(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException
   {
      ProductKeysStruct     productKeys  = new ProductKeysStruct(0, 0, (short)0, 0);
      PriceStruct           price        = new PriceStruct((short)0,0,0);

      OrderBookPriceStruct[]  orderBookPrice = new OrderBookPriceStruct[ 1 ];
      orderBookPrice[ 0 ]  = new OrderBookPriceStruct(price,0,0);

      BookDepthStruct      bookDepth         = new BookDepthStruct(  productKeys, sessionName
                                                                     ,orderBookPrice
                                                                     ,orderBookPrice
                                     ,true
                                     ,0
                                                                     );

      if ( 1 == 0 ) throw new SystemException();
      if ( 1 == 0 ) throw new CommunicationException();
      if ( 1 == 0 ) throw new AuthorizationException();
      if ( 1 == 0 ) throw new NotFoundException();

      return bookDepth;
   }

    public void subscribeTicker(String sessionName, int productKey,CMITickerConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
      if ( 1 == 0 ) throw new SystemException();
      if ( 1 == 0 ) throw new CommunicationException();
      if ( 1 == 0 ) throw new AuthorizationException();
      if ( 1 == 0 ) throw new DataValidationException();
   }

    public void unsubscribeCurrentMarket(String sessionName, int productKey,CMICurrentMarketConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
      if ( 1 == 0 ) throw new SystemException();
      if ( 1 == 0 ) throw new CommunicationException();
      if ( 1 == 0 ) throw new AuthorizationException();
      if ( 1 == 0 ) throw new DataValidationException();
   }

    public void unsubscribeRecapForProduct(String sessionName, int productKey,CMIRecapConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
      if ( 1 == 0 ) throw new SystemException();
      if ( 1 == 0 ) throw new CommunicationException();
      if ( 1 == 0 ) throw new AuthorizationException();
      if ( 1 == 0 ) throw new DataValidationException();
   }

    public void unsubscribeRecapForClass(String sessionName, int classKey,CMIRecapConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
      if ( 1 == 0 ) throw new SystemException();
      if ( 1 == 0 ) throw new CommunicationException();
      if ( 1 == 0 ) throw new AuthorizationException();
      if ( 1 == 0 ) throw new DataValidationException();
   }

    public void unsubscribeTicker(String sessionName, int productKey,CMITickerConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
      if ( 1 == 0 ) throw new SystemException();
      if ( 1 == 0 ) throw new CommunicationException();
      if ( 1 == 0 ) throw new AuthorizationException();
      if ( 1 == 0 ) throw new DataValidationException();
   }

    public MarketDataHistoryStruct getMarketDataHistoryByTime(String sessionName, int productKey,DateTimeStruct startTime, short direction)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
   {

         return ServicesHelper.getMarketDataService().getProductByTime("querySessionId", sessionName, productKey, startTime, direction);
   }

    public void subscribeExpectedOpeningPrice(String sessionName, int classKey,CMIExpectedOpeningPriceConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
      if ( 1 == 0 ) throw new SystemException();
      if ( 1 == 0 ) throw new CommunicationException();
      if ( 1 == 0 ) throw new AuthorizationException();
      if ( 1 == 0 ) throw new DataValidationException();
   }

    public void unsubscribeExpectedOpeningPrice(String sessionName, int classKey,CMIExpectedOpeningPriceConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
      if ( 1 == 0 ) throw new SystemException();
      if ( 1 == 0 ) throw new CommunicationException();
      if ( 1 == 0 ) throw new AuthorizationException();
      if ( 1 == 0 ) throw new DataValidationException();
   }

    public void subscribeCurrentMarketForClass(String sessionName, int classKey, CMICurrentMarketConsumer clientListener) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException {
        //Implement this com.cboe.idl.cmi._MarketQueryOperations method;
    }

    public void subscribeBookDepthUpdate(String sessionName, int classKey, CMIOrderBookUpdateConsumer clientListener) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException {
        //Implement this com.cboe.idl.cmi._MarketQueryOperations method;
    }

    public void subscribeBookDepth(String sessionName, int classKey, CMIOrderBookConsumer clientListener) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException {
        //Implement this com.cboe.idl.cmi._MarketQueryOperations method;
    }


    public void subscribeNBBOForClass(String sessionName, int classKey, CMINBBOConsumer clientListener) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException {
        //Implement this com.cboe.idl.cmi._MarketQueryOperations method;
    }

    public void unsubscribeCurrentMarketForProduct(String sessionName, int productKey, CMICurrentMarketConsumer clientListener) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException {
        //Implement this com.cboe.idl.cmi._MarketQueryOperations method;
    }

    public void unsubscribeNBBOForProduct(String sessionName, int productKey, CMINBBOConsumer clientListener) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException {
        //Implement this com.cboe.idl.cmi._MarketQueryOperations method;
    }

    public void unsubscribeCurrentMarketForClass(String sessionName, int classKey, CMICurrentMarketConsumer clientListener) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException {
        //Implement this com.cboe.idl.cmi._MarketQueryOperations method;
    }

    public void unsubscribeBookDepthUpdate(String sessionName, int classKey, CMIOrderBookUpdateConsumer clientListener) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException {
        //Implement this com.cboe.idl.cmi._MarketQueryOperations method;
    }

    public void unsubscribeBookDepth(String sessionName, int classKey, CMIOrderBookConsumer clientListener) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException {
        //Implement this com.cboe.idl.cmi._MarketQueryOperations method;
    }

    public void unsubscribeNBBOForClass(String sessionName, int classKey, CMINBBOConsumer clientListener) throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException {
        //Implement this com.cboe.idl.cmi._MarketQueryOperations method;
    }


}

