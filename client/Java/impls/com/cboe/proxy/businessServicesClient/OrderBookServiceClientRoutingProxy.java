package com.cboe.proxy.businessServicesClient;

import com.cboe.infrastructureServices.foundationFramework.utilities.RouteNameHelper;
import com.cboe.infrastructureServices.instrumentationService.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.loggingService.*;
import com.cboe.infrastructureServices.traderService.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.exceptions.*;

import com.cboe.idl.businessServices.OrderBookService;
import com.cboe.idl.marketData.BookDepthDetailStruct;
import com.cboe.idl.orderBook.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiUtil.*;

import com.cboe.idl.cmiIntermarketMessages.BookDepthDetailedStruct;


/**
 * This class is a routing proxy that delgates the incoming requests
 * to the appropriate order book service. The class maintains a table which
 * maps every service to its respective process ( route ).
 *
 * @date December 18, 2008
 * 
**/

public 	class OrderBookServiceClientRoutingProxy 
              extends NonGlobalServiceClientRoutingProxy 
              implements com.cboe.interfaces.businessServices.OrderBookService
{
        /**
         * Default constructor
         */
        public	OrderBookServiceClientRoutingProxy()
        {
        }

        /**
         * Forwards request to delegate based on classKey and sessionName
         *
         * @param String sessionName,
         * @param int classKey
         * @return BestBookStruct[]
         */
        public BestBookStruct [] getBestBookForClass(String sessionName, int classKey)
	        throws	SystemException,
                        CommunicationException,
                        DataValidationException,
                        AuthorizationException
        {            
            OrderBookService service = (OrderBookService) getServiceByClass(sessionName,classKey);
	        return service.getBestBookForClass(sessionName, classKey );
        }

        /**
         * Forwards request to delegate based on productKey and sessionName
         *
         * @param String sessionName,
         * @param int productKey,
         * @param PriceStruct[] priceSequence,
         * @return TradableStruct[]
         */
        public TradableStruct[] getBookDetails(String sessionName, int productKey, PriceStruct[] priceSequence)
	        throws	SystemException,
                        CommunicationException,
                        DataValidationException,
                        AuthorizationException,
                        TransactionFailedException
        {
            OrderBookService service = (OrderBookService) getServiceByProduct(sessionName, productKey);
	        return service.getBookDetails(sessionName, productKey, priceSequence );
        }

      /**
         * Forwards request to delegate based on productKey and sessionName
         *
         * @param String sessionName,
         * @param int productKey
         * @return BestBookStruct[]
         */
        public BookDepthStruct getBookDepth(String sessionName, int productKey, boolean includeTopOnly)
	        throws  SystemException,
                        CommunicationException,
                        DataValidationException,
                        NotFoundException,
                        AuthorizationException
        {
            OrderBookService service = (OrderBookService) getServiceByProduct(sessionName, productKey);
	        return service.getBookDepth(sessionName, productKey, includeTopOnly);
        }

        public com.cboe.idl.cmiMarketData.BookDepthStructV2 getBookDepthV2(String sessionName, int productKey, boolean includeTopOnly)
	        throws  SystemException,
                        CommunicationException,
                        DataValidationException,
                        NotFoundException,
                        AuthorizationException
        {
            OrderBookService service = (OrderBookService) getServiceByProduct(sessionName, productKey);
	        return service.getBookDepthV2(sessionName, productKey, includeTopOnly);
        }

      /**
         * Forwards request to delegate based on classKey and sessionName
         *
         * @param String sessionName,
         * @param int classKey
         * @return BookDepthStruct[]
         */
        public BookDepthStruct[] getBookDepthByClass(String sessionName, int classKey, boolean includeTopOnly)
	        throws  SystemException,
                        CommunicationException,
                        DataValidationException,
                        NotFoundException,
                        AuthorizationException
        {
            OrderBookService service = (OrderBookService) getServiceByClass(sessionName, classKey);
	        return service.getBookDepthByClass(sessionName, classKey, includeTopOnly);
        }

      /**
         * Forwards request to delegate based on productKey and sessionName
         *
         * @param String sessionName,
         * @param int productKey
         * @return BestBookStruct[]
         */
        public BookDepthDetailStruct getBookDepthDetails(String sessionName, int productKey)
	        throws  SystemException,
                        CommunicationException,
                        DataValidationException,
                        NotFoundException,
                        AuthorizationException
        {
            OrderBookService service = (OrderBookService) getServiceByProduct(sessionName, productKey);
	        return service.getBookDepthDetails(sessionName, productKey);
        }

   /* Operation Definition */
   public short getOpeningRequirementCode(java.lang.String sessionName, int classKey)
   throws com.cboe.exceptions.SystemException,
        com.cboe.exceptions.CommunicationException,
        com.cboe.exceptions.DataValidationException,
        com.cboe.exceptions.NotFoundException,
        com.cboe.exceptions.AuthorizationException
    {       
        OrderBookService service = (OrderBookService) getServiceByClass(sessionName, classKey);
        return service.getOpeningRequirementCode(sessionName, classKey);
    }

    /* Operation Definition */
    public void setOpeningRequirementCode(java.lang.String sessionName, int classKey, short anOpeningRequirementCode)
    throws com.cboe.exceptions.SystemException,
            com.cboe.exceptions.CommunicationException,
            com.cboe.exceptions.DataValidationException,
            com.cboe.exceptions.AuthorizationException
    {        
        OrderBookService service = (OrderBookService) getServiceByClass(sessionName, classKey);
        service.setOpeningRequirementCode(sessionName, classKey, anOpeningRequirementCode);
    }

        /**
         * Return the Service Helper class name
         *
         * @return String, the service helper class name related to this proxy
         */
        protected String getHelperClassName()
        {
            return "com.cboe.idl.businessServices.OrderBookServiceHelper";
        }
        
    /* Get all the orders from the book corresponding to the product key.
     *
     * @param sessionName The session name where the method has been invoked.
     * @param productKey Information on the product that is being moved
     *
     * @return boolean All the orders in a struct.
     *
     * @throw SystemException
     * @throw CommunicationException
     * @throw AuthorizationException
     * @throw DataValidationException
     * @throw TransactionFailedException
     * @throw NotAccetedException
     */   
    public BookDepthDetailedStruct getDetailedOrderBook(String sessionName,int productKey)
        throws SystemException,
               CommunicationException,
               AuthorizationException,
               DataValidationException,
               NotFoundException,
               NotAcceptedException
    {
        OrderBookService service = (OrderBookService) getServiceByProduct(sessionName, productKey);
        return service.getDetailedOrderBook(sessionName, productKey);
    }
    
    /* Get current state of the OrderBook
     *
     * @param sessionName The session name where the method has been invoked.
     * @param productKey ProductKey whose orderBook state will be queried.
     *
     * @return boolean State of the OrderBook.
     *
     * @throw SystemException
     * @throw CommunicationException
     * @throw AuthorizationException
     * @throw DataValidationException
     * @throw TransactionFailedException
     * @throw NotAccetedException
     *
     * @author Sandip Chatterjee
     */
    public short getOrderBookStatus(String sessionName,int productKey)
        throws SystemException,
               CommunicationException,
               AuthorizationException,
               DataValidationException,
               NotFoundException,
               NotAcceptedException
    {
        OrderBookService service = (OrderBookService) getServiceByProduct(sessionName, productKey);
        return service.getOrderBookStatus(sessionName, productKey);
    }
}
