package com.cboe.interfaces.presentation.manualReporting;

import com.cboe.idl.cmiMarketData.ClassVolumeStruct;
import com.cboe.idl.cmiMarketData.ProductClassVolumeStruct;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

/**
 * Defines the contract a ManualQuote wrapper for a ManualQuoteStruct
 */
public interface ProductClassVolume extends BusinessModel {
   // public static final String PROPERTY_SESSION = "PROPERTY_SESSION"; 
    public static final String CLASS_CALLS_VOLUME = "CLASS_CALLS_VOLUME";
    public static final String CLASS_PUTS_VOLUME = "CLASS_PUTS_VOLUME";
    public static final String CLASS_TOTAL_VOLUME = "CLASS_TOTAL_VOLUME";
    public static final String TRADES_CALLS_CNT = "TRADES_CALLS_CNT";
    public static final String TRADES_PUTS_CNT = "TRADES_PUTS_CNT";
    public static final String TRADES_CNT = "TRADES_CNT";
    public static final String AVG_CALLS_CONTRACT_SIZE = "AVG_CALLS_CONTRACT_SIZE";
    public static final String AVG_PUTS_CONTRACT_SIZE = "AVG_PUTS_CONTRACT_SIZE";

    // helper methods to struct attributes
    public int getClassCallsVolume();
    public int getClassPutsVolume();
    public int getClassTotalVolume();
    public int getTradesForCallsCnt();
    public int getTradesForPutsCnt();
    public int getTotalTrades();
    public int getAvgCallsContractSize();
    public int getAvgPutsContractSize();
    public int getAvgContractTotals();

    public ProductClassVolumeStruct getStruct();
    public int getClassKey();                          
    public String getClassSymbol();
    public String getRequestTime();
    public String getRequestDate();
    public String getSessionName();

    public void setStruct(ProductClassVolumeStruct struct);
    public void setClassKey(int key);
    public void setClassSymbol(String classSymbol);
    public void setRequestTime(String requestTime);
    public void setRequestDate(String requestDate);
    public void setSessionName(String name);

}
