//
//-----------------------------------------------------------------------------------
//Source file: TraderServiceProxyResultsWrapper.java
//
//PACKAGE: package com.cboe.infra.presentation.traderService;
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

import java.util.ArrayList;

public class TraderServiceProxyResultsWrapper
{
    // string buffer for report text 
    private StringBuffer reportTextStrBuf = new StringBuffer();

    // array list of offers
    private ArrayList queryOffersList;

    // indicates type of objs in offers array list
    private int queryResultType = TraderServiceConstants.QUERY_RESULTS_TYPE_UNKNOWN; 
    
    public TraderServiceProxyResultsWrapper()
    {
    }

    /**
     * @return Returns the queryOffersList.
     */
    public ArrayList getQueryOffersList()
    {
        return this.queryOffersList;
    }

    /**
     * @param queryOffersList The queryOffersList to set.
     */
    public void setQueryOffersList(ArrayList queryOffersList)
    {
        this.queryOffersList = queryOffersList;
    }

    /**
     * @return Returns the queryResultType.
     */
    public int getQueryResultType()
    {
        return this.queryResultType;
    }

    /**
     * @param queryResultType The queryResultType to set.
     */
    public void setQueryResultType(int queryResultType)
    {
        this.queryResultType = queryResultType;
    }

    /**
     * @return Returns the reportTextStrBuf.
     */
    public StringBuffer getReportTextStrBuf()
    {
        return this.reportTextStrBuf;
    }

    /**
     * @param reportTextStrBuf The reportTextStrBuf to set.
     */
    public void setReportTextStrBuf(StringBuffer reportTextStrBuf)
    {
        this.reportTextStrBuf = reportTextStrBuf;
    }
    
    

}
