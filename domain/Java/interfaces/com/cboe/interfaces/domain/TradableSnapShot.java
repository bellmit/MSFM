package com.cboe.interfaces.domain;


import java.util.ArrayList;
import java.util.HashMap;


import com.cboe.idl.order.MarketDetailStruct;
import com.cboe.idl.trade.TradeReportStructV2;
import com.cboe.util.Copyable;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ObjectPool;
import com.cboe.util.ObjectPoolHome;
import java.util.Iterator;
/**
 * snapshot of tradable required for fill generation and publishing during the async trade 
 *
 * @author Harinath Manchana
 *
 * @version 1.0
 */

public final class TradableSnapShot implements Copyable 
{
	// following are captured after the tradable and book updates
	private int tradedTotalVolume;	
	private int tradedSessionVolume;
	private double averagePrice;
	private double sessionAveragePrice;
	private int canceledTotalVolume;
	private int canceledSessionVolume;
	private int remainingVoume;
	private int remainingFillVoume;
	private short tradableState;
	private long tid;
	private long tradedTime = 0l;
	private int updateTradableCounter = 0;
	private String userAssignedId = null;
	
	// following are captured prior to trade for Billing
	private short internalOrderState;
	private long bookedTime;
	private boolean isBooked;
	private int bookedStatus;
	private Price bestOrderBookPrice;
	private Price tradablePrice;
	private short productState;	
	private HashMap<Integer, QuoteSnapShot[]> legQuotes = new HashMap<Integer, QuoteSnapShot[]>();
	private int quoteKey;
	
	MarketDetailStruct[] fillReprotMarketDetailStruct;  // Used for publishing the Async. Fill Report to OHS.
	
    // These following arrayLists are only used for CPS trading to hold the generated leg reports artifact.
    ArrayList<TradeReportStructV2> generatedLegTrades = null;
    ArrayList generatedLegAtomicBuyer = null;
    ArrayList generatedLegAtomicSeller = null;
    ArrayList generatedLegSides = null;
	
	private static ObjectPool<TradableSnapShot> myPool 
	= ObjectPoolHome.getHome().create(TradableSnapShot.class.getName(), new TradableSnapShot());

	
	public TradableSnapShot()
	{
	
	}
	
	public static void returnInstance(TradableSnapShot p_TradeCommand) 
	{
		//myPool.checkIn(p_TradeCommand);
	}

	public static TradableSnapShot getInstance() 
	{
		return new TradableSnapShot();
		//return myPool.checkOut();
	}

	public Object copy()
	{
		return new TradableSnapShot();
	}

	public long getAcquiringThreadId()
	{
		return tid;
	}

	public void setAcquiringThreadId(long acquiringThreadId)
	{
		tid = acquiringThreadId;
	}

	public void clear()
	{
		this.tradedTotalVolume = 0;
		this.tradedSessionVolume = 0;
		this.averagePrice = 0;
		this.sessionAveragePrice = 0;
		this.canceledTotalVolume = 0;
		this.canceledSessionVolume = 0;
		this.remainingVoume = 0;
		this.remainingFillVoume = 0;
		this.tradableState =0;
		this.bookedTime = 0;
		this.isBooked = false;
		this.bestOrderBookPrice = null;
		this.bookedStatus = 0;
		this.tradedTime = 0l;
		this.internalOrderState = 0;
		this.tradablePrice= null;
		this.productState = 0;
		this.userAssignedId = null;
		this.updateTradableCounter = 0;
		this.quoteKey = 0;
		
		fillReprotMarketDetailStruct = null;
    
	    generatedLegTrades = null;
	    generatedLegAtomicBuyer = null;
	    generatedLegAtomicSeller = null;
	    generatedLegSides = null;
	}

	public void set(int tradedTotalVolume, int tradedSessionVolume, int canceledTotalVolume, int canceledSessionVolume, int remainingVoume,
			int remainingFillVoume, short tradableState, double averagePrice, double sessionAveragePrice, long tradedTime)
	{
		this.tradedTotalVolume = tradedTotalVolume;
		this.tradedSessionVolume = tradedSessionVolume;
		this.averagePrice = averagePrice;
		this.sessionAveragePrice = sessionAveragePrice;
		this.canceledTotalVolume = canceledTotalVolume;
		this.canceledSessionVolume = canceledSessionVolume;
		this.remainingVoume = remainingVoume;
		this.remainingFillVoume = remainingFillVoume;
		this.tradableState = tradableState;
		this.tradedTime = tradedTime;
	}
	
	public void setPriorTradeData(long bookedTime, boolean isBooked, 
			Price bestPrice, Price price,  int bookedStatus,  short priorInternalOrderState, HashMap<Integer, Quote[]> legQuotes)
	{
		this.bookedTime = bookedTime;
		this.isBooked = isBooked;
		this.bookedStatus = bookedStatus;
		this.internalOrderState = priorInternalOrderState;
		this.bestOrderBookPrice = bestPrice;
		this.tradablePrice = price;
		setQuoteSnapShots(legQuotes);
	}
	
	private void setQuoteSnapShots(HashMap<Integer, Quote[]> legQts)
	{
		Iterator itr = legQts.keySet().iterator();
		while ( itr.hasNext())
		{
			Integer pk = (Integer)itr.next();
			Quote[] quotes = (Quote[]) legQts.get(pk);
			QuoteSnapShot[] result = new QuoteSnapShot[quotes.length];
			for(int i = 0; i<quotes.length ; ++i)
			{
				result[i] = new QuoteSnapShot();
				result[i].quote = quotes[i];
				result[i].bidBookedTime = quotes[i].getBid().getBookedTime();
				result[i].askBookedTime = quotes[i].getAsk().getBookedTime();
			}
			legQuotes.put(pk, result);
		}
		
	}
	
	public class QuoteSnapShot
	{
		public Quote quote;
		public long bidBookedTime;
		public long askBookedTime;
	}
	
	public long getTradedTime()
	{
	    return this.tradedTime;
	}
	
	public long getBookedTime()
	{
	    return this.bookedTime;
	}

	public boolean isBooked()
	{
	    return this.isBooked;
	}

	public Price getBestPrice()
	{
	    return this.bestOrderBookPrice;
	}

	public int getBookedStatus()
	{
	    return this.bookedStatus;
	}
	   
	public short getProductState()
	{
	    return this.productState;
	}
	
	public void setProductState(short productState)
	{
	   this.productState = productState;
	}
	
	
	public int getTradedTotalVolume()
	{
		return tradedTotalVolume;
	}
	
	public int getTradedSessionVolume()
	{
		return tradedSessionVolume;
	}
	
	public double getAveragePrice()
	{
		return averagePrice;
	}
	
	public double getsessionAveragePrice()
	{
		return sessionAveragePrice;
	}
	
	public int getCanceledTotalVolume()
	{
		return canceledTotalVolume;
	}
	
	public int getCanceledSessionVolume()
	{
		return canceledSessionVolume;
	}
	
	public int getRemainingVoume()
	{
		return remainingVoume;
	}
	
	public short getTradableState()
	{
		return tradableState;
	}
	public Price getTradablePrice()
	{
		return tradablePrice;
	}
	
	public short getInternalState()
	{
		return internalOrderState;
	}
	
	public int getRemainingFillVoume()
	{
		return remainingFillVoume;
	}
	
	public String getUserAssignedId()
	{
		return userAssignedId;
	}
	
    public String toString()
    {
        StringBuffer sb = new StringBuffer("TradableSnapShot: ");
        sb.append("tradedTotalVolume = " + tradedTotalVolume);
        sb.append(", tradedSessionVolume = " + tradedSessionVolume);
        sb.append(", averagePrice = " + averagePrice);
        sb.append(", sessionAveragePrice = " + sessionAveragePrice);
        sb.append(", canceledTotalVolume = " + canceledTotalVolume);
        sb.append(", canceledSessionVolume = " + canceledSessionVolume);
        sb.append(", remainingVoume = " + remainingVoume);
        sb.append(", remainingFillVoume = " + remainingFillVoume);
        sb.append(", tradableState = " + tradableState);
        sb.append(", bookedTime = " + bookedTime);
        sb.append(", productState = " + productState);
        sb.append(", userAssignedId = " + userAssignedId);
        sb.append(", isBooked = " + isBooked);
        if(bestOrderBookPrice != null)
            sb.append(", bestOrderBookPrice = " + bestOrderBookPrice);
        else
            sb.append(", bestOrderBookPrice = null");
        if(tradablePrice != null)
            sb.append(", tradablePrice = " + tradablePrice );
        else
            sb.append(", tradablePrice  = null");
        sb.append(", bookedStatus = " + bookedStatus + "\n");
        sb.append(", tradedTime = " + tradedTime + "\n");
        sb.append(", updateTradableCounter = " + updateTradableCounter + "\n");
        sb.append(", priorInternalOrderState = " + internalOrderState + "\n");
        sb.append(", quoteKey = " + quoteKey + "\n");
        return sb.toString();
    }   

    
    public ArrayList<TradeReportStructV2> getGeneratedLegTrades()
    {
        return generatedLegTrades;
    }

    /**
     * Used for the CPS trade within Spread Trade Server
     * @param p_generatedLegTrades
     */
    public void setGeneratedLegTrades(ArrayList<TradeReportStructV2> p_generatedLegTrades)
    {
        generatedLegTrades = p_generatedLegTrades;
    }

    public ArrayList getGeneratedLegAtomicBuyer()
    {
        return generatedLegAtomicBuyer;
    }

    /**
     * Used for the CPS trade within Spread Trade Server
     * @param p_generatedLegAtomicBuyer
     */
    public void setGeneratedLegAtomicBuyer(ArrayList p_generatedLegAtomicBuyer)
    {
        generatedLegAtomicBuyer = p_generatedLegAtomicBuyer;
    }

    public ArrayList getGeneratedLegAtomicSeller()
    {
        return generatedLegAtomicSeller;
    }

    /**
     * Used for the CPS trade within Spread Trade Server
     * @param p_generatedLegAtomicSeller
     */
    public void setGeneratedLegAtomicSeller(ArrayList p_generatedLegAtomicSeller)
    {
        generatedLegAtomicSeller = p_generatedLegAtomicSeller;
    }

    /**
     * 
     * @return
     */
    public ArrayList getGeneratedLegSides()
    {
        return generatedLegSides;
    }

    /**
     * Used for the CPS trade within Spread Trade Server
     * @param p_generatedLegSides
     */
    public void setGeneratedLegSides(ArrayList p_generatedLegSides)
    {
        generatedLegSides = p_generatedLegSides;
    }

    public void setUserAssignedId(String uAssignedId)
    {
    	userAssignedId = uAssignedId;
    }
    
    /**
     * 
     * @return
     */
    public MarketDetailStruct[] getFillReprotMarketDetailStruct()
    {
        return fillReprotMarketDetailStruct;
    }

    /**
     * Used to for publishing the asynchronous fill report from TE ----> OHS. This captures the 
     * Market data snapshot at the time of trading.
     * @param p_fillReprotMarketDetailStruct
     */
    public void setFillReprotMarketDetailStruct(MarketDetailStruct[] p_fillReprotMarketDetailStruct)
    {
        fillReprotMarketDetailStruct = p_fillReprotMarketDetailStruct;
    }

	public void setUpdateTradableCounter(int updateTradableCounter) 
	{
		this.updateTradableCounter = updateTradableCounter;
        if (Log.isDebugOn())
        {
            Log.debug("TradableSnapShot::setUpdateTradableCounter >> for ASYNC_TRADE updateTradableCounter: " + updateTradableCounter );
        }  		
	}

	public int getUpdateTradableCounter() 
	{
		return updateTradableCounter;
	}

	/**public void setLegQuotes(HashMap<Integer, Quote[]> legQuotes) 
	{
		this.legQuotes = legQuotes;
	}**/

	public HashMap<Integer, QuoteSnapShot[]> getLegQuotes() 
	{
		return legQuotes;
	}

	public void setQuoteKey(int quoteKey) 
	{
		this.quoteKey = quoteKey;
	}

	public int getQuoteKey() 
	{
		return quoteKey;
	}

}
