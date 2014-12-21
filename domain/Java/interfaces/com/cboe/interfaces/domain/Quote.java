package com.cboe.interfaces.domain;


import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.NBBOStruct;


public interface Quote
{
    // Define some constants to be used in cancelReplace
    static final int BOTH_SIDES = 0;
    static final int BUY_SIDE = 1;
    static final int SELL_SIDE = 2;

    public void bust( int quoteKey, BustReportStruct bustInfo ) throws DataValidationException;
    public void cancel(short cancelReason) throws DataValidationException;

	public void publishCancelReport(short cancelReason, QuoteStructV4 quote);

    public QuoteSide getAsk();
    public QuoteSide getBid();
    public String getUserId();
    public Integer getProductKey();
    public int getProductKeyLocal();
    public int getQuoteKey();
    public long getUniqueId();
    public QuoteStruct getStruct();
    public QuoteStructV3 getStructV3();
    public QuoteStructV4 getStructV4();

    public boolean isStandard();
    public boolean isStandardForOEPW();
    public boolean isStandardSize();
    public void markForCancelReport() ;

	public boolean isUpdateable(QuoteStructV3 aQuoteStructV3);
	public boolean isUpdateable(QuoteStructV4 aQuoteStructV4);
	
    public void update( QuoteStructV3 aQuoteStructV3 );
    public void update( QuoteStructV4 aQuoteStructV4 );

    public void setCancelScope(short cancelScope);
    public void setCancelReason(short cancelReason);
//    public short getCancelScope();
    public short getCancelReason();
    public short getCancelScope();
    
    public boolean allowCancelInQuoteTrigger();

    public void setCancelRequestPending();
    public boolean isCancelRequestPending();
    public void pendingCancelProcessed();

    public void setUserSessionId(int userSessionId);

    public int getUserSessionId();

    /**
     * This is to check to see if the user with input sessionId can update the quote
     * @param incomingQuoteUserSessionId
     * @return true if updatable, false if not
     */
    public boolean isQuoteUpdatableWithUserSessionId(int incomingQuoteUserSessionId);

    public String getSessionName();

    //public int getState();
    public char getState();
    
    public boolean isAsyncCancelPending();
    
   
    
    public void setAsyncCancelPending(boolean p_asyncCancelPending);
    
    public char getSellShortIndicator();
    public void setSellShortIndicator(char indicator);
    
    //  To temporarily store NBBO while processing a quote
    public void setTempNBBO(NBBOStruct nbbo);
    public NBBOStruct getTempNBBO();
    
    public boolean isAsyncTradeCreatePending();
    public int getAsyncTradeCreatePendingCount();
    public void publishBlockCancelForAsyncTrade(RoutingParameterStruct routing, String userId, short cancelReason);

}


