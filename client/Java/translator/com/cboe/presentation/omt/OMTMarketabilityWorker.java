//
// -----------------------------------------------------------------------------------
// Source file: OMTMarketabilityWorker.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiConstants.ContingencyTypes;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiOrder.OrderStruct;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.marketData.StrategyImpliedMarketWrapper;
import com.cboe.interfaces.presentation.marketData.DsmBidAskStruct;
import com.cboe.interfaces.presentation.omt.MarketabilityCheckedListener;
import com.cboe.interfaces.presentation.omt.MarketabilityIndicator;
import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.omt.OrderMessageElement;
import com.cboe.interfaces.presentation.omt.MessageElement.MessageType;
import com.cboe.interfaces.presentation.order.LegOrderDetail;
import com.cboe.interfaces.presentation.order.StrategyOrder;
import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.presentation.product.Product;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.api.StrategyUtility;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.formatters.ProductTypes;
import com.cboe.presentation.common.formatters.RoutingReasonsFormatter;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.marketData.displayrules.StrategyBidAskFlipper;

import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.SidesSpecifier;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.lwt.queue.QueueInterruptedException;
import com.cboe.lwt.thread.ThreadTask;


/**
 * OMTMarketabilityWorker is a worker thread in charge of queuing OrderMessageElement object to
 * assess their marketability by fetching their CurrentMarketStruct.
 * <p/>
 * The simple order and the spread order marketability checks are supported.
 * <p/>
 * The current implementation uses a queue that is a FIFO type and all call to server are
 * synchronous requests.
 * <p/>
 * The OMTMarketabilityWorker is a singleton being initialized with {@code initialize()} method.
 *
 *
 * @author Eric Maheo
 * @version 7.1
 * @since 01/15/2009
 *
 * @see com.cboe.interfaces.presentation.api.OrderManagementTerminalAPI
 * 
 */
@SuppressWarnings({"FinalClass"})
public final class OMTMarketabilityWorker
{
    /**
     * Eager instanciation of this class.
     */
    public static final OMTMarketabilityWorker INSTANCE = new OMTMarketabilityWorker();

    /**
     * Contingency types that prevent any marketability assessment.
     */
    private static final Set<Short> EXCLUDED_CONTINGENCIES;

    static
    {
        EXCLUDED_CONTINGENCIES = new HashSet<Short>(16);
        EXCLUDED_CONTINGENCIES.add(ContingencyTypes.OPG);
        EXCLUDED_CONTINGENCIES.add(ContingencyTypes.MIT);
        EXCLUDED_CONTINGENCIES.add(ContingencyTypes.STP);
        EXCLUDED_CONTINGENCIES.add(ContingencyTypes.STP_LOSS);
        EXCLUDED_CONTINGENCIES.add(ContingencyTypes.CLOSE);
        EXCLUDED_CONTINGENCIES.add(ContingencyTypes.STP_LIMIT);
    }

    /**
     * Queue holding the OrderMessageElement waiting for marketability check.
     */
    private final ConcurrentLinkedQueue<MessageElement> marketabilityQueue = new ConcurrentLinkedQueue<MessageElement>();
    /**
     * Refer to the SbtTraderGui.properties property file section {@value
     * #PROPERTIES_SECTION_NAME}.
     */
    private static final String PROPERTIES_SECTION_NAME = "Timers";
    /**
     * Property of the section {@value #PROPERTIES_SECTION_NAME} that set the delay between each
     * batch check marketability request to server.
     */
    private static final String MARKETABILITY_CHECK_DELAY = "MarketabilityCheckDelay";
    /**
     * Specify the number of requests per batch by the property {@value #MARKETABILITY_CHECK_DELAY}
     * in the section {@value #PROPERTIES_SECTION_NAME}.
     */
    private static final String MARKETABILITY_CHECK_BLOCK_SIZE = "MarketabilityCheckBlockSize";
    /**
     * Delay between batch requests defined by the property {@value #MARKETABILITY_CHECK_BLOCK_SIZE}
     * in the section {@value #PROPERTIES_SECTION_NAME}.<br> The default value is 50.
     */
    private volatile long delay;
    /**
     * BlockSize value defined by the property {@value #MARKETABILITY_CHECK_BLOCK_SIZE}.<br> The
     * default value is 1.
     */
    private volatile int blockSize;
    /**
     * Hold all listener {@link MarketabilityCheckedListener}.
     */
    private final List<MarketabilityCheckedListener> listeners = new ArrayList<MarketabilityCheckedListener>(5);
    /**
     * Worker thread that process the marketability of the order element stored in the queue.
     */
    private final MarketabilityThread marketThread;
    /**
     * Monitor for the worker thread.
     */
    private final Object workerThreadMonitor = new Object();

    /**
     * Create an OMTMarketabilityWorker thread.<BR> Call the getInstance() method to get an instance
     * of this object.
     */
    private OMTMarketabilityWorker()
    {
        initializeValues();
        marketThread = new MarketabilityThread();
    }

    /**
     * Get the instance of this class.
     * @return the instance.
     */
    public static OMTMarketabilityWorker getInstance()
    {
        return INSTANCE;
    }

    /**
     * Method that merely initialized the static fields if they aren't already initialized.<BR>
     * This method will intialized the field INSTANCE which will call the private contructor of this
     * class.
     */
    public static synchronized void initialize(){}

    /**
     * Add to the queue an OrderMessageElement to check its marketability.
     * @param element to check marketability.
     */
    public void queueMessageElement(MessageElement element)
    {
        synchronized(workerThreadMonitor){
            marketabilityQueue.add(element);
            workerThreadMonitor.notifyAll();
        }
    }

    /**
     * Start processing the queue of MessageElement to assess their marketability.
     */
    public void startMarketabilityCheck(){
        marketThread.go();
    }

    /**
     * Add a listener to be notified when a marketability has been checked.
     * @param listener to add.
     */
    public void addListener(MarketabilityCheckedListener listener)
    {
        synchronized(listeners)
        {
            if(listener != null && !listeners.contains(listener))
            {
                listeners.add(listener);
            }
        }
    }

    /**
     * Unsubscribe a listener to stop being notified for marketability checked.
     * @param listener to remove.
     */
    public void removeListener(MarketabilityCheckedListener listener)
    {
        synchronized(listeners)
        {
            if(listener != null && !listeners.contains(listener))
            {
                listeners.remove(listener);
            }
        }
    }
    
    /**
     * Empty the queue.
     */
    public void emptyQueue(){
    	synchronized(workerThreadMonitor){
    		marketabilityQueue.clear();
    		workerThreadMonitor.notifyAll();
    	}
    }

    /**
     * Read property file SbtTraderGui.properties and initialize the {@code delay} and {@code blockSize}
     * value. If not present it will call setDefault() method.
     */
    @SuppressWarnings({"UnusedCatchParameter"})
    private void initializeValues()
    {
        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String delayString = AppPropertiesFileFactory.find()
                    .getValue(PROPERTIES_SECTION_NAME, MARKETABILITY_CHECK_DELAY);
            String blockString = AppPropertiesFileFactory.find()
                    .getValue(PROPERTIES_SECTION_NAME, MARKETABILITY_CHECK_BLOCK_SIZE);

            GUILoggerHome.find().debug(getClass().getName() + ".initializeValues()",
                                       GUILoggerBusinessProperty.OMT, MARKETABILITY_CHECK_DELAY +
                                                                      '=' + delayString + " : " +
                                                                      MARKETABILITY_CHECK_BLOCK_SIZE +
                                                                      '=' + blockString);
            if(delayString != null && delayString.trim().length() != 0 && blockString != null &&
               blockString.trim().length() != 0)
            {
                try
                {
                    delay = Long.parseLong(delayString);
                    blockSize = Integer.parseInt(blockString);
                }
                catch(NumberFormatException e)
                {
                    setDefaults();
                }
            }
            else
            {
                setDefaults();
            }
        }
        else
        {
            setDefaults();
        }
    }

    /**
     * Default values assigned when the property file doesn't specify them.
     */
    private void setDefaults()
    {
        delay = 50;
        blockSize = 1;
        Log.information(
                "Could not get properties for MarketabilityWorker - defaults will be used: delay=" +
                delay + " blockSize=" + blockSize);
    }

    /**
     * Notify all clients that registered a MarketabilityCheckedListener listener to this class.
     * @param element that had a marketability checked done.
     */
    private void fireMarketabilityElementUpdated(MessageElement element)
    {
        MarketabilityCheckedListener[] localListeners;
        synchronized(listeners)
        {
            localListeners = listeners.toArray(new MarketabilityCheckedListener[listeners.size()]);
        }
        for(MarketabilityCheckedListener listener : localListeners)
        {
            listener.messageElementMarketabilityUpdated(element);
        }
    }

    /**
     * MarketabilityThread class is in charge to fetching the CurrentMarketStruct for the order.
     * If the order is a spread order multiple requests will be done to the server.
     * All request are synchronous call to server.
     */
    private class MarketabilityThread extends ThreadTask
    {

        MarketabilityThread()
        {
            super("OMTMarketabilityWorker");
        }

        /**
         * The task assesses the orderMessageElement by fetching their currentMarket.
         * It notifies the registered MarketabilityCheckedListener clients that
         * an OrderMessageElement was updated with the result of the marketability.
         * @see com.cboe.lwt.thread.ThreadTask#doTask()
         */
        protected void doTask() throws InterruptedException, IOException, QueueInterruptedException
        {
            //noinspection CatchGenericClass
            try
            {
                for(int i = 0; i < blockSize; i++)
                {
                    MessageElement elementpop;
                    synchronized(workerThreadMonitor){
                        while(marketabilityQueue.peek()==null && !Thread.currentThread().isInterrupted()){
                            workerThreadMonitor.wait();
                        }
                        if (Thread.currentThread().isInterrupted()){
                            GUILoggerHome.find().audit("OMTMarketabilityWorker is interrupted.");
                            break;// no need to continue in this case.
                        }
                        elementpop = marketabilityQueue.poll();
                        workerThreadMonitor.notifyAll();
                    }

                    OrderMessageElement element = null;
                    if(elementpop instanceof OrderMessageElement)
                    {
                        element = (OrderMessageElement) elementpop;
                    }
                    else
                    {
                        if (elementpop != null){
                            //noinspection ThrowCaughtLocally
                            throw new IllegalArgumentException(
                                    "Cannot assess marketability for non OrderMessageElement. Found a :" +
                                    elementpop.getClass().getName());
                        } //if elementpop is null just let it hit the NPE below.
                    }
                    if(element != null)
                    {
                            StrategyImpliedMarketWrapper strategyImpledMarketWrapper = null;
                            // fetch our marketability indicator
                            //noinspection NonPrivateFieldAccessedInSynchronizedContext
                            CurrentMarketStruct currentMarketStruct =
                                    APIHome.findOrderManagementTerminalAPI()
                                            .getCurrentMarketQuoteForProducts(
                                                    element.getSessionName(),
                                                    element.getProductKey()).bestMarket;
                            if(element.getOrder().getProductType() == ProductTypes.STRATEGY)
                            {
                                //strategyImpledMarketWrapper = getDSM(element);
                                strategyImpledMarketWrapper = getDSM(((StrategyOrder) element.getOrder()).getSessionStrategy());

                            }
                            element.setCurrentMarket(currentMarketStruct);
                            element.setMarketabilityIndicator(getOrderMarketability(element,
                                                                                    currentMarketStruct,
                                                                                    strategyImpledMarketWrapper));
                            //notify the listeners.
                            fireMarketabilityElementUpdated(element);
                    }
                    else
                    {
                        //throws NPE for a case that shouldn't occur.
                        //noinspection ThrowCaughtLocally,ProhibitedExceptionThrown
                        throw new NullPointerException("OrderMessageElement is null.");
                    }
                }
                //Delay each request sent to the server.
                Thread.sleep(delay);
            }
            catch(UserException e)
            {
                GUILoggerHome.find()
                        .exception("Error trying to check marketability", e);
            }
            catch(RuntimeException re)
            {
                GUILoggerHome.find()
                        .exception("Runtime Exception thrown in MarketabilityWorker.", re);
            }
        }
    }

    /**
     * Calculate the DSM (same/define and opposite) for a strategy order.
     * @param sessionStrategy strategy from with the dsm will be calculated.
     * @return a StrategyImpliedMarketWrapper object.
     * @throws org.omg.CORBA.UserException exception thrown by server.
     */
    public static synchronized StrategyImpliedMarketWrapper getDSM(SessionStrategy sessionStrategy)
            throws UserException
    {
        StrategyImpliedMarketWrapper retVal = null;

        if(!StrategyUtility.containsUnderlyingLeg(sessionStrategy))
        {
            final String sessionName = sessionStrategy.getTradingSessionName();
            StrategyUtility.CurrentMarketGetter cmGetter = new StrategyUtility.CurrentMarketGetter()
            {
                public CurrentMarketStruct getCurrentMarket(Product product) throws UserException
                {
                    return APIHome.findOrderManagementTerminalAPI().getCurrentMarketQuoteForProducts(
                                                                      sessionName, product.getProductKey()).bestMarket;
                }
                protected CurrentMarketStruct getUnderlyingCM(Product product) throws UserException
                {
                    return null; // TODO ??
                }
            };

            DsmBidAskStruct dsmBidAsk = StrategyUtility.getStrategyDSM(sessionStrategy, cmGetter);
            if (dsmBidAsk != null)
            {
                dsmBidAsk.setFlipped(StrategyBidAskFlipper.isDsmBidAskFlipped(sessionStrategy, dsmBidAsk));
            }
            retVal = StrategyUtility.convertToImpliedMarket(dsmBidAsk);
        }
        return retVal;
    }


    /**
     * Assess the marketability indicator for a complex order.
     * @param element strategy order to assess.
     * @param strategyImpliedMarketWrapper that contains the opposite and same implied information.
     * @return the market indicator.
     */
    private static MarketabilityIndicator getMarketabilityForStrategyOrder(
            OrderMessageElement element, StrategyImpliedMarketWrapper strategyImpliedMarketWrapper)
    {
        //noinspection UnusedAssignment
        MarketabilityIndicator indicator = MarketabilityIndicator.UNKOWN;
        double zero = 0.00;
        Price zeroPrice = null;
        Price orderPrice = null;
        
        //Added following block to fix PITS#62227	- NAS
        if(element != null)
        {
            orderPrice = element.getPrice();
            zeroPrice = DisplayPriceFactory.create(zero);
            
            LegOrderDetail[] orderDetails = element.getLegOrderDetails();
            //First check all legs are of same side, if "YES", then get side of the first leg and process
            if(isAllLegSidesSame(orderDetails))
            {
            	LegOrderDetail legOrderDetail = orderDetails[0];
            	
            	//If all legs side are 'B' i.e. Buy
            	if(Character.toUpperCase(legOrderDetail.getSide()) == 'B')
            	{
            		if(orderPrice.greaterThanOrEqual(zeroPrice))
            		{
            			indicator = MarketabilityIndicator.X_NOT_MARKETABLE;
            			return indicator;
            		}
            	}
            	//If all legs side are 'S' i.e. Sell
            	else if(Character.toUpperCase(legOrderDetail.getSide()) == 'S')
            	{
            		if(orderPrice.lessThanOrEqual(zeroPrice))
            		{
            			indicator = MarketabilityIndicator.X_NOT_MARKETABLE;
            			return indicator;
            		}
            	}
            }
        }
        //Added until here to address PITS#62227	- NAS

        if(strategyImpliedMarketWrapper == null)
        {
            indicator = MarketabilityIndicator.UNKOWN;
        }
        else
        {
            orderPrice = element.getPrice();
            double same = strategyImpliedMarketWrapper.getImpliedSame();
            Price samePrice = DisplayPriceFactory.create(same);
            double opposite = strategyImpliedMarketWrapper.getImpliedOpposite();
            Price oppPrice = DisplayPriceFactory.create(opposite);
            zeroPrice = DisplayPriceFactory.create(zero);
            
            //if same and opposite are both CR then it isn't marketable
            if(samePrice.greaterThanOrEqual(zeroPrice) && oppPrice.greaterThanOrEqual(zeroPrice))
            {
                indicator = MarketabilityIndicator.NOT_MARKETABLE;
            }
            //if same and opposite are both DB then it's always marketable
            else if(samePrice.lessThan(zeroPrice) && oppPrice.lessThan(zeroPrice))
            {
                indicator = MarketabilityIndicator.MARKETABLE;
            }
            //if same and opposite are CR and DB or DB CR then there is a check to do that the same doesn't touch opposite in order to be marketable.
            else if(Character.toUpperCase(element.getSide()) == 'O' ||
                    Character.toUpperCase(element.getSide()) == 'D')
            {
                //CR DB
                if(samePrice.greaterThanOrEqual(zeroPrice))
                {
                    Price positiveOppPrice = DisplayPriceFactory.create(-1 * oppPrice.toDouble());
                    indicator = orderPrice.lessThanOrEqual(positiveOppPrice) ?
                                MarketabilityIndicator.MARKETABLE :
                                MarketabilityIndicator.NOT_MARKETABLE;
                }
                //DB CR
                else if(oppPrice.greaterThanOrEqual(zeroPrice))
                {
                    Price negativeOppPrice = DisplayPriceFactory.create(-1 * oppPrice.toDouble());
                    indicator = orderPrice.lessThanOrEqual(negativeOppPrice) ?
                                MarketabilityIndicator.MARKETABLE :
                                MarketabilityIndicator.NOT_MARKETABLE;
                }
                else { //should never pass in this case.
                    throw new IllegalStateException("Illegal state for a price.");
                }
            }
            else
            {
                throw new IllegalArgumentException(
                        "Unsupported case -- OrderMessageElement found was:" + element.toString());
            }
        }
        return indicator;
    }

    /**
     * Assess the marketability indicator for a simple order.
     * @param element order to assess marketability.
     * @param currentMarketStruct for the order being assessed.
     * @return the marketability indicator for a simple order.
     */
    private static MarketabilityIndicator getMarketabilityForSimpleOrder(OrderMessageElement element,
                                                                    CurrentMarketStruct currentMarketStruct)
    {
        MarketabilityIndicator indicator = MarketabilityIndicator.UNKOWN;
        Price orderPrice = element.getPrice();
        if(Character.toUpperCase(element.getSide()) == 'B')
        {
            Price askPrice = PriceFactory.create(currentMarketStruct.askPrice);
            if(askPrice != null && (askPrice.isValuedPrice() || askPrice.isMarketPrice()))
            {
                if(orderPrice.greaterThanOrEqual(askPrice))
                {
                    indicator = MarketabilityIndicator.MARKETABLE;
                }
                else
                {
                    indicator = MarketabilityIndicator.NOT_MARKETABLE;
                }
            }
        }
        else if(Character.toUpperCase(element.getSide()) == 'S')
        {
            Price bidPrice = PriceFactory.create(currentMarketStruct.bidPrice);
            if(bidPrice != null && (bidPrice.isValuedPrice() || bidPrice.isMarketPrice()))
            {
                if(orderPrice.lessThanOrEqual(bidPrice))
                {
                    indicator = MarketabilityIndicator.MARKETABLE;
                }
                else
                {
                    indicator = MarketabilityIndicator.NOT_MARKETABLE;
                }
            }
        }
        else {
            throw new IllegalArgumentException("Unexpected side for a simple order. Side found was:" + element.getSide());
        }
        return indicator;
    }

    /**
     * Get the order marketability.
     * Based on the type of order the getMarketabilityForSimpleOrder or getMarketabilityStrategyOrder
     * will be called. Unless the order price is marketable or "premium exceeds reasonability".
     *
     * @param element Order to assess marketability.
     * @param currentMarket the non-null current market.
     * @param strategyImpliedMarketWrapper used in case of strategy order.
     * @return the marketability indicator
     * @throws org.omg.CORBA.UserException exception thrown by server.
     */
    private static MarketabilityIndicator getOrderMarketability(OrderMessageElement element,
                                                         CurrentMarketStruct currentMarket,
                                                         StrategyImpliedMarketWrapper strategyImpliedMarketWrapper)
            throws UserException
    {
        MarketabilityIndicator indicator = MarketabilityIndicator.UNKOWN;

        if(element.getRouteReason() == RoutingReasonsFormatter.PREMIUM_EXCEEDS_REASONABILITY)
        {
            indicator = MarketabilityIndicator.SUPER_MARKETABLE;
        }
        else if(!(EXCLUDED_CONTINGENCIES.contains(element.getContingency().getType())))
        {
            if(element.getPrice().isMarketPrice()){
                indicator = MarketabilityIndicator.MARKETABLE;
            }
            else if(element.getProductType() == ProductTypes.STRATEGY)
            {
                indicator = getMarketabilityForStrategyOrder(element,
                                                                     strategyImpliedMarketWrapper);
            }
            else
            {
                if(element.getPrice().isValuedPrice())
                {
                    if(currentMarket.productKeys.productKey == element.getProductKey())
                    {
                        indicator = getMarketabilityForSimpleOrder(element, currentMarket);
                    }
                }
            }
        }
        return indicator;
    }
    
    /**
     * Checks whether all legs in the strategy orders are of same side i.e. Buy or Sell
     * @param orderDetails to check for the leg sides
     */
    private static boolean isAllLegSidesSame(LegOrderDetail[] orderDetails)
    {
        if(orderDetails != null)
        {
        	Character sideOfFirstLeg = null;
        	
        	if(orderDetails.length > 1)
        	{
	        	for(int i=0; i < orderDetails.length; i++)
	        	{
        			Character strategyLegSide = null;
	        		LegOrderDetail eachOrder = orderDetails[i];
	        		if(eachOrder != null)
	        		{
	        			if(SidesSpecifier.isBuyEquivalent(Character.toUpperCase(eachOrder.getSide()) ))
	        			{
	        				strategyLegSide = 'B';
	        			}
	        			else
	        			{
	        				strategyLegSide = 'S';
	        			}
	        		}
	        		if(sideOfFirstLeg == null)
	        		{
	        			sideOfFirstLeg = strategyLegSide;
	        		}//If any leg side is other than previous leg side, then return false
	        		else if(sideOfFirstLeg != null && sideOfFirstLeg != strategyLegSide)
	        		{
	        			return false;
	        		}
	        		else if(sideOfFirstLeg != null && sideOfFirstLeg == strategyLegSide)
	        		{
	        			continue;
	        		}
	        	}
	        	return true;
        	}
        }
		return false;
    }
}