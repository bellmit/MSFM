package com.cboe.presentation.manualReporting;

import com.cboe.idl.cmiMarketData.ProductClassVolumeStruct;
import com.cboe.interfaces.presentation.manualReporting.ProductClassVolume;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
/**
 * Quote implementation for a QuoteStruct from the API.
 */
class ProductClassVolumeImpl extends AbstractBusinessModel implements ProductClassVolume {



    protected ProductClassVolumeStruct productClassVolumeStruct = null;
    private String sessionName = null;
    private int classKey = -1;
    private String classSymbol = null;
    private String requestTime = null;
    private String requestDate = null;

    public ProductClassVolumeImpl()
    {}
    
    public int getClassKey() {
        return this.classKey;
    }
    // helper methods to struct attributes
    public int getClassCallsVolume() {
        return getStruct().classVolume.classCallsVolume;
    }

    public int getClassPutsVolume() {
       return getStruct().classVolume.classPutsVolume;
    }

    public int getClassTotalVolume() {
       return getStruct().classVolume.classTotalVolume;
    }

    public int getTradesForCallsCnt() {
        return getStruct().classVolume.numberOfTradesForCalls;
    }

    public int getTradesForPutsCnt() {
       return getStruct().classVolume.numberOfTradesForPuts;
    }

    public int getTotalTrades() {
        return getStruct().classVolume.numberOfTotalTrades;
    }

    public int getAvgCallsContractSize() {
        return getStruct().classVolume.averageCallsContractSize;
    }

    public int getAvgPutsContractSize() {
        return getStruct().classVolume.averagePutsContractSize;
    }

    public int getAvgContractTotals() {
        return getStruct().classVolume.averageTotalContractSize;
    }
    public ProductClassVolumeStruct getStruct() {
        return this.productClassVolumeStruct;
    }
    public void setClassKey(int classKey) {
        this.classKey = classKey;
    }
    public String getClassSymbol() {
        return classSymbol;
    }

    public void setClassSymbol(String classSymbol) {
        this.classSymbol = classSymbol;
    }
       public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }
    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String name) {
        this.sessionName = name;
    }

    public void setStruct(ProductClassVolumeStruct productClassVolumeStruct) {
        this.productClassVolumeStruct = productClassVolumeStruct;
    }

}
