package com.cboe.application.marketData;

import java.util.Date;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.marketData.ManualQuoteDetailInternalStruct;
import com.cboe.idl.quote.ManualQuoteDetailStruct;
import com.cboe.interfaces.domain.MarketUpdate;
import com.cboe.interfaces.domain.MarketUpdateSide;
import com.cboe.interfaces.domain.bestQuote.CurrentMarket;
//import com.cboe.server.logger.MsgBuilder;
import com.cboe.util.Copyable;
import com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateHelper;

public final class ClientMarketUpdateImpl implements Copyable, MarketUpdate
{
	private long tid;
	private int classKey; // (since this is pre-cached)
	private int productKey; // (since this is pre-cached)
	private short productType; // (since this is pre-cached)
	private int reportingClassKey; // (since this is pre-cached)
	private long sentTime;
	private boolean isLegalMarket;
	private boolean isProductStateChange;
	private short oldProductState;
	private short newProductState;
	private int prodStateSeqNum;
	private String sessionName;

	private ManualQuoteDetailInternalStruct manualQuoteDetails = null;

	private final ClientMarketUpdateSideImpl bid = new ClientMarketUpdateSideImpl(true);
	private final ClientMarketUpdateSideImpl ask = new ClientMarketUpdateSideImpl(false);
	private  ClientMarketUpdateSideImpl bidManual = null;
	private  ClientMarketUpdateSideImpl askManual = null;

	public ClientMarketUpdateImpl()
	{
	}

	public ClientMarketUpdateImpl(MarketUpdate p_other)
	{
		copyFrom(p_other);
	}

	public void copyFrom(MarketUpdate other)
	{
	    ClientMarketUpdateImpl p_other = (ClientMarketUpdateImpl)other;
		classKey = p_other.classKey;
		reportingClassKey = p_other.reportingClassKey;
		productKey = p_other.productKey;
		productType = p_other.productType;
		sentTime = p_other.sentTime;
		isLegalMarket = p_other.isLegalMarket;
		isProductStateChange = p_other.isProductStateChange;
		oldProductState = p_other.oldProductState;
		newProductState = p_other.newProductState;
		prodStateSeqNum = p_other.prodStateSeqNum;
		sessionName = p_other.sessionName;
		bid.copyFrom(p_other.bid);
		ask.copyFrom(p_other.ask);

		if(p_other.bidManual != null)
		{
		    if(bidManual == null) bidManual = new ClientMarketUpdateSideImpl(true);
		    bidManual.copyFrom(p_other.bidManual);
		}
		else
		{
		    bidManual = null;
		}

		if(p_other.askManual != null)
		{
		    if(askManual == null) askManual = new ClientMarketUpdateSideImpl(false);
		    askManual.copyFrom(p_other.askManual);
		}
		else
		{
		    askManual = null;
		}

		if(p_other.manualQuoteDetails != null)
		{
		    if(manualQuoteDetails == null)
		    {
		        manualQuoteDetails = new ManualQuoteDetailInternalStruct();
		        manualQuoteDetails.bidSide = new ManualQuoteDetailStruct();
		        manualQuoteDetails.askSide = new ManualQuoteDetailStruct();
		        copyManualQuoteDetails(p_other.manualQuoteDetails);
		    }
		}
		else
		{
		    manualQuoteDetails = null;
		}
	}

	public MarketUpdateSide getBid()
	{
	    return bid;
	}

	public MarketUpdateSide getAsk()
	{
	    return ask;
	}

	public MarketUpdateSide getBidManual()
	{
	    return bidManual;
	}

	public MarketUpdateSide getAskManual()
	{
	    return askManual;
	}


	private void copyManualQuoteDetails(ManualQuoteDetailInternalStruct p_otherManualQuoteDetails)
	{
	    manualQuoteDetails.bidSide.ipAddress = p_otherManualQuoteDetails.bidSide.ipAddress;
        manualQuoteDetails.bidSide.locationId =  p_otherManualQuoteDetails.bidSide.locationId;
        manualQuoteDetails.bidSide.parId = p_otherManualQuoteDetails.bidSide.parId;
        manualQuoteDetails.bidSideCancelReason = p_otherManualQuoteDetails.bidSideCancelReason;

        manualQuoteDetails.askSide.ipAddress = p_otherManualQuoteDetails.askSide.ipAddress;
        manualQuoteDetails.askSide.locationId = p_otherManualQuoteDetails.askSide.locationId;
        manualQuoteDetails.askSide.parId = p_otherManualQuoteDetails.askSide.parId;
        manualQuoteDetails.askSideCancelReason = p_otherManualQuoteDetails.askSideCancelReason;
	}

	public void copyIntoStruct(MarketUpdate p_update, CurrentMarket p_struct, short p_marketViewType)
	{
	    MarketUpdateHelper.copyIntoStruct(p_update, p_struct, p_marketViewType);
	}

	public void copyIntoStruct(CurrentMarketStruct market, short viewType)
	{
	    MarketUpdateHelper.copyIntoStruct(this, market, viewType);
	}

	public void setSentTime(long sentTime)
	{
		this.sentTime = sentTime;
	}

	public void setSessionName(String sessionName)
    {
        this.sessionName = sessionName;
    }

	public String getSessionName()
    {
       return  this.sessionName;
    }

	public void setLegalMarket(boolean p_isLegalMarket)
	{
		isLegalMarket = p_isLegalMarket;
	}

	public boolean isLegalMarket()
	{
		return isLegalMarket;
	}

	public void setProductKeys(ProductKeysStruct p_prodKeys)
	{
		classKey = p_prodKeys.classKey;
		reportingClassKey = p_prodKeys.reportingClass;
		productKey = p_prodKeys.productKey;
		productType = p_prodKeys.productType;
	}

	public void clear()
	{
		classKey = 0;
		reportingClassKey = 0;
		productKey = 0;
		productType = (short)0;
		isProductStateChange = false;
		isLegalMarket = false;
		sentTime = 0;
		oldProductState = 0;
		newProductState = 0;
		sessionName = null;
		bid.clear();
		ask.clear();
		if(bidManual != null) bidManual.clear();
		if(askManual != null) askManual.clear();
		if(manualQuoteDetails != null)
		{
		    manualQuoteDetails.bidSide = null;
		    manualQuoteDetails.askSide =null;
		    manualQuoteDetails = null;
		}
	}

	public ManualQuoteDetailInternalStruct getManualQuoteDetails()
	{
		return manualQuoteDetails;
	}

	public void createManualSides()
	{
	     bidManual = new ClientMarketUpdateSideImpl(true);
	     askManual = new ClientMarketUpdateSideImpl(false);
	}


	public ManualQuoteDetailInternalStruct getOrCreateManualQuoteDetails()
	{
		if (manualQuoteDetails==null)
		{
			ManualQuoteDetailInternalStruct newStruct = new ManualQuoteDetailInternalStruct();
			newStruct.bidSide = new ManualQuoteDetailStruct();
			newStruct.askSide = new ManualQuoteDetailStruct();
			setManualQuoteDetails(newStruct);
		}
		return getManualQuoteDetails();
	}

	public void setManualQuoteDetails(ManualQuoteDetailInternalStruct p_manualQuoteDetails)
	{
		manualQuoteDetails = p_manualQuoteDetails;
	}

	public Object copy()
	{
		return new ClientMarketUpdateImpl();
	}

	public long getAcquiringThreadId()
	{
		return tid;
	}

	public void setAcquiringThreadId(long acquiringThreadId)
	{
		tid = acquiringThreadId;
	}

	public int getProdClassKey()
	{
		return classKey;
	}

	public int getProdReportingClassKey()
	{
	    return reportingClassKey;
	}

	public int getProductKey()
	{
		return productKey;
	}

	public short getProdType()
	{
		return productType;
	}

	public void setProdClassKey(int p_val)
	{
		classKey = p_val;
	}

	public void setProdReportingClassKey(int p_val)
    {
	    reportingClassKey = p_val;
    }

	public void setProductKey(int p_val)
	{
		productKey = p_val;
	}

	public void setProdType(short p_val)
	{
		productType = p_val;
	}

	public void setOldProductState(short p_oldState)
	{
		oldProductState = p_oldState;
	}

	public boolean isProductStateChange()
	{
		return isProductStateChange;
	}

	public short getOldProductState()
	{
		return oldProductState;
	}

	public short getNewProductState ()
	{
		return newProductState;
	}

	public void setNewProductState(short p_newState)
	{
		newProductState = p_newState;
	}

	public boolean hasPublicPrice()
	{
		return bid.hasPublicPrice() || ask.hasPublicPrice();
	}

	public boolean hasManualQuote()
    {
        if (manualQuoteDetails != null) return true;

        return false;
    }

	public boolean isPublicPriceBest()
	{
	    return bid.isPublicPriceBest() || ask.isPublicPriceBest();
	}

	public boolean isPublicBidPriceBest()
	{
	    return bid.isPublicPriceBest();
	}

	public boolean isPublicAskPriceBest()
	{
	    return ask.isPublicPriceBest();
	}

	public boolean isLimitBidPriceBest()
	{
	    return bid.isLimitPriceBest();
	}

	public boolean isLimitAskPriceBest()
	{
	    return ask.isLimitPriceBest();
	}

	public int getProdStateSeqNum()
	{
		return prodStateSeqNum;
	}

	public void setProdStateSeqNum(int p_val)
	{
		this.prodStateSeqNum = p_val;
	}

	public long getSentTime()
	{
		return this.sentTime;
	}

	@Override
	public boolean equals(Object p_rhs)
	{
		if (p_rhs==null || p_rhs.getClass() != MarketUpdate.class)
			return false;

		final ClientMarketUpdateImpl rhs = (ClientMarketUpdateImpl)p_rhs;

		return classKey == rhs.classKey
		    && reportingClassKey == rhs.reportingClassKey
			&& productKey == rhs.productKey
			&& productType == rhs.productType
			&& sentTime == rhs.sentTime
			&& isLegalMarket == rhs.isLegalMarket
			&& isProductStateChange == rhs.isProductStateChange
			&& oldProductState == rhs.oldProductState
			&& bid.equals(rhs.bid)
			&& ask.equals(rhs.ask)
			&& stringsAreEqual(sessionName,rhs.sessionName)
			&& manualSidesAreEqual(rhs)
			&& manualQuotesAreEqual(rhs);
	}

	private boolean manualSidesAreEqual(ClientMarketUpdateImpl p_rhs)
	{
	    boolean bidSideEqual = false;
	    boolean askSideEqual = false;

	    if(bidManual != null)
	    {
	        bidSideEqual = bidManual.equals(p_rhs.bidManual);
	    }
	    else
	    {
	        if(p_rhs.bidManual == null) bidSideEqual = true;
	    }

	    if(askManual != null)
        {
	        askSideEqual = askManual.equals(p_rhs.askManual);
        }
        else
        {
            if(p_rhs.askManual == null) askSideEqual = true;
        }

	    if( bidSideEqual && askSideEqual) return true;

	    return false;
	}

	private boolean manualQuotesAreEqual(MarketUpdate p_rhs)
	{
		if (getManualQuoteDetails()==null)
			return p_rhs.getManualQuoteDetails()==null;
		if (p_rhs.getManualQuoteDetails()==null)
			return false;
		final ManualQuoteDetailInternalStruct lhs = getManualQuoteDetails();
		final ManualQuoteDetailInternalStruct rhs = p_rhs.getManualQuoteDetails();
		return lhs.bidSideCancelReason == rhs.bidSideCancelReason
			&& lhs.askSideCancelReason == rhs.askSideCancelReason
			&& manualQuoteSidesAreEqual(lhs.bidSide, rhs.bidSide)
			&& manualQuoteSidesAreEqual(lhs.askSide, rhs.askSide);

	}

	private boolean manualQuoteSidesAreEqual(ManualQuoteDetailStruct p_lhs, ManualQuoteDetailStruct p_rhs)
	{
		if (p_lhs==null || p_rhs==null)
			return p_lhs==null && p_rhs==null;
		return stringsAreEqual(p_lhs.locationId, p_rhs.locationId)
			&& stringsAreEqual(p_lhs.parId, p_rhs.parId)
			&& stringsAreEqual(p_lhs.ipAddress, p_rhs.ipAddress);
	}

	private boolean stringsAreEqual(String p_lhs, String p_rhs)
	{
		if (p_lhs==null || p_rhs==null)
			return p_lhs==null && p_rhs==null;
		return p_lhs.equals(p_rhs);
	}
/*
    @Override
	public String toString()
	{
		StringBuffer msg = new StringBuffer();
		msg.append("cKey", classKey);
		msg.add("rcKey", reportingClassKey);
		msg.add("pKey", productKey);
		msg.add("pType", productType);
		msg.add("session", sessionName);
		msg.addDateTime("sentTime", new Date(sentTime));
		msg.add("lglMkt", isLegalMarket);
		msg.add("stateChg", isProductStateChange);
		msg.add("oldState", oldProductState);
		msg.add("manQuote", toManQuoteString());
		msg.newLine().add("bid", bid);
		msg.newLine().add("ask", ask);
		if(bidManual != null)
		    msg.newLine().add("bid", bid);
		else
		    msg.newLine().add("bidManual", "none");

		if(askManual != null)
		    msg.newLine().add("ask", ask);
		else
            msg.newLine().add("askManual", "none");

		return msg.toString();
	}

	private String toManQuoteString()
	{
		final ManualQuoteDetailInternalStruct mq = getManualQuoteDetails();
		if (mq==null)
			return "none";
		MsgBuilder msg = MsgBuilder.get();
		msg.add("bid(");
		msg.add("cxlReason='").add(mq.bidSideCancelReason).add("'");
		msg.add(",loc=").addQuoted(mq.bidSide.locationId);
		msg.add(",par=").addQuoted(mq.bidSide.parId);
		msg.add(",ip=").addQuoted(mq.bidSide.ipAddress);
		msg.add("), ask(");
		msg.add("cxlReason='").add(mq.askSideCancelReason).add("'");
		msg.add(",loc=").addQuoted(mq.askSide.locationId);
		msg.add(",par=").addQuoted(mq.askSide.parId);
		msg.add(",ip=").addQuoted(mq.askSide.ipAddress);
		msg.add(")");
		return msg.toString();
	}
*/
	public void setIsProductStateChange(boolean p_b)
	{
		this.isProductStateChange = p_b;
	}


	public boolean isRoundLotMarketChange()
	{
		return false;
	}

	public void setRoundLotMarketChange(boolean p_roundLotMarketChange)
	{
		//do not set - Round lot Market is only applicable for StockCFN Adapter.
	}

	public boolean hasRoundLotLimitMarket()
	{
		return false;
	}

}
