package com.cboe.domain.dataIntegrity;

import java.util.HashMap;
import java.util.Map;

import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.dataIntegrity.SynchPoint;
import com.cboe.util.ExceptionBuilder;

public class OhsSynchPoint extends BaseSynchPoint implements SynchPoint
{
    private static final long serialVersionUID = -2182105645779009001L;
    protected CountAt productCount;
    protected CountAt productEventCount;
    protected CountAt orsidCount;
    protected CountAt orsidIndex;

    public void addOrsidCount(long count, long timestamp) throws DataValidationException
    {
        this.orsidCount.addCount(count, timestamp);
        this.lastCountAddedTime = timestamp;
    }
    public void addOrsidIndexCount(long count, long timestamp) throws DataValidationException
    {
        this.orsidIndex.addCount(count, timestamp);
        this.lastCountAddedTime = timestamp;
    }
    public void addProductCount(long count, long timestamp) throws DataValidationException
    {
        this.productCount.addCount(count, timestamp);
        this.lastCountAddedTime = timestamp;
    }
    public void addProductUpdateCount(long count, long timestamp) throws DataValidationException
    {
        this.productEventCount.addCount(count, timestamp);
        this.lastCountAddedTime = timestamp;
    }
    
    public OhsSynchPoint()
    {
        super(SynchPoint.SERVER_TYPE_OHS);
        this.numCounts = 6;
    }
    
    /**
     * Initialize object counts for an OHS server
     * @throws DataValidationException
     */
    public void initializeCounts(Map<String, String> thresholdParams) throws DataValidationException
    {
        this.setThresholdParams(thresholdParams);
        // Initialize period in base
        super.setConfiguredPeriod();
        // initialize object count for local Order cache counts
        String orderTimeUnitMultiplier = thresholdParams.get(SynchPoint.ORDER_PROPERTYKEY + SynchPoint.TIMEUNIT_PROPERTYKEYSUFFIX);
        String orderTimeUnitDelay = thresholdParams.get(SynchPoint.ORDER_PROPERTYKEY + SynchPoint.TIMEUNITDELAY_PROPERTYKEYSUFFIX);
        this.setOrderCount(CountAtDynamicThreshold.getInstance(SynchPoint.ORDER_PROPERTYKEY, SynchPoint.SERVER_TYPE_OHS, orderTimeUnitMultiplier, orderTimeUnitDelay));
        // initialize distributed cache order updates
        String orderUpdateTimeUnitMultiplier = thresholdParams.get(SynchPoint.ORDERUPDATE_PROPERTYKEY + SynchPoint.TIMEUNIT_PROPERTYKEYSUFFIX);
        String orderUpdateTimeUnitDelay = thresholdParams.get(SynchPoint.ORDERUPDATE_PROPERTYKEY + SynchPoint.TIMEUNITDELAY_PROPERTYKEYSUFFIX);
        this.setOrderEventCount(CountAtDynamicThreshold.getInstance(SynchPoint.ORDERUPDATE_PROPERTYKEY, SynchPoint.SERVER_TYPE_OHS, orderUpdateTimeUnitMultiplier, orderUpdateTimeUnitDelay));
        // initialize orsid count
        String orsidMaxThreshold = thresholdParams.get(SynchPoint.ORSID_PROPERTYKEY + SynchPoint.MAXTHRESHOLD_PROPERTY_SUFFIX);
        this.orsidCount = CountAt.getAbsoluteCount(SynchPoint.ORSID_PROPERTYKEY,SynchPoint.SERVER_TYPE_OHS, orsidMaxThreshold);
        // initialize orsid index count
        String orsidIndexMaxThreshold = thresholdParams.get(SynchPoint.ORSIDINDEX_PROPERTYKEY + SynchPoint.MAXTHRESHOLD_PROPERTY_SUFFIX);
        this.orsidIndex = CountAt.getAbsoluteCount(SynchPoint.ORSIDINDEX_PROPERTYKEY,SynchPoint.SERVER_TYPE_OHS, orsidIndexMaxThreshold);
        // initialize object count for local product data cache(object count)
        String productMaxThreshold = thresholdParams.get(SynchPoint.PRODUCT_PROPERTYKEY + SynchPoint.MAXTHRESHOLD_PROPERTY_SUFFIX);
        this.productCount = CountAt.getAbsoluteCount(SynchPoint.PRODUCT_PROPERTYKEY,SynchPoint.SERVER_TYPE_OHS, productMaxThreshold);
        // initialize count of product update counts
        String productUpdateMaxThreshold = thresholdParams.get(SynchPoint.PRODUCTUPDATE_PROPERTYKEY + SynchPoint.MAXTHRESHOLD_PROPERTY_SUFFIX);
        this.productEventCount = CountAt.getAbsoluteCount(SynchPoint.PRODUCTUPDATE_PROPERTYKEY,SynchPoint.SERVER_TYPE_OHS, productMaxThreshold);
        // initialize count of users
        String userMaxThreshold = thresholdParams.get(SynchPoint.USER_PROPERTYKEY + SynchPoint.MAXTHRESHOLD_PROPERTY_SUFFIX);
        this.setUserCount(CountAt.getAbsoluteCount(SynchPoint.USER_PROPERTYKEY,SynchPoint.SERVER_TYPE_OHS, userMaxThreshold));
        // initialize count of user updates
        String userUpdateMaxThreshold = thresholdParams.get(SynchPoint.USERUPDATE_PROPERTYKEY + SynchPoint.MAXTHRESHOLD_PROPERTY_SUFFIX);
        this.setUserEventCount(CountAt.getAbsoluteCount(SynchPoint.USERUPDATE_PROPERTYKEY,SynchPoint.SERVER_TYPE_OHS, userUpdateMaxThreshold));
    }

    @Override
    public Map<String, String> compareWith(SynchPoint secondaryCounts) throws DataValidationException
    { 
        int discrepancyCount = 0;
        OhsSynchPoint slaveSp = (OhsSynchPoint) secondaryCounts;
        OhsSynchPoint masterSp = this;
        Map<String, String> comparisonMessages = new HashMap<String, String>();
        int result = BaseSynchPoint.COUNT_NOT_COMPARED;
        // compare object count for local Order cache counts
        result = masterSp.getOrderCount().isInRange(slaveSp.getOrderCount());
        slaveSp.setHistoricalComparisonResult(masterSp.getOrderCount(),slaveSp.getOrderCount(),result);
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.getOrderCount().getName(), masterSp.getCountComparisonMsg(masterSp.getOrderCount(), slaveSp.getOrderCount(), result));
            discrepancyCount++;
        }
        // compare distributed cache order updates
        result = masterSp.getOrderEventCount().isInRange(slaveSp.getOrderEventCount());
        slaveSp.setHistoricalComparisonResult(masterSp.getOrderEventCount(),slaveSp.getOrderEventCount(),result);
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.getOrderEventCount().getName(), masterSp.getCountComparisonMsg(masterSp.getOrderEventCount(), slaveSp.getOrderEventCount(), result));
            discrepancyCount++;
        }
        // compare distributed cache orsid free pool counts
        result = masterSp.getOrsidCount().isInRange(slaveSp.getOrsidCount());
        slaveSp.setHistoricalComparisonResult(masterSp.getOrsidCount(),slaveSp.getOrsidCount(),result);
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.getOrsidCount().getName(), masterSp.getCountComparisonMsg(masterSp.getOrsidCount(), slaveSp.getOrsidCount(), result));
            discrepancyCount++;
        }
        // compare distributed cache orsid index values
        result = masterSp.getOrsidIndex().isInRange(slaveSp.getOrsidIndex());
        slaveSp.setHistoricalComparisonResult(masterSp.getOrsidIndex(),slaveSp.getOrsidIndex(),result);
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.getOrsidIndex().getName(), masterSp.getCountComparisonMsg(masterSp.getOrsidIndex(), slaveSp.getOrsidIndex(), result));
            discrepancyCount++;
        }
        // compare object count for local product data cache(object count)
        result = masterSp.productCount.isInRange(slaveSp.productCount);
        slaveSp.setHistoricalComparisonResult(masterSp.productCount,slaveSp.productCount,result);
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.productCount.getName(), masterSp.getCountComparisonMsg(masterSp.productCount, slaveSp.productCount, result));
            discrepancyCount++;
        }
        // compare count of product update counts
        result = masterSp.productEventCount.isInRange(slaveSp.productEventCount);
        slaveSp.setHistoricalComparisonResult(masterSp.productEventCount,slaveSp.productEventCount,result);
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.productEventCount.getName(), masterSp.getCountComparisonMsg(masterSp.productEventCount, slaveSp.productEventCount, result));
            discrepancyCount++;
        }
        // compare count of users
        result = masterSp.getUserCount().isInRange(slaveSp.getUserCount());
        slaveSp.setHistoricalComparisonResult(masterSp.getUserCount(),slaveSp.getUserCount(),result);
        //if(result != CountAt.INRANGE)
        //{
        //    comparisonMessages.put(slaveSp.getUserCount().getName(), masterSp.getCountComparisonMsg(masterSp.getUserCount(), slaveSp.getUserCount(), result));
        //    discrepancyCount++;
      // }
        // compare count of user updates
        result = masterSp.getUserEventCount().isInRange(slaveSp.getUserEventCount());
        slaveSp.setHistoricalComparisonResult(masterSp.getUserEventCount(),slaveSp.getUserEventCount(),result);
        if(result != CountAt.INRANGE)
        {
            comparisonMessages.put(slaveSp.getUserEventCount().getName(), masterSp.getCountComparisonMsg(masterSp.getUserEventCount(), slaveSp.getUserEventCount(), result));
            discrepancyCount++;
        }
        // set last comparison history for whole synch point
        slaveSp.setSynchPointStatus(slaveSp.lastCountAddedTime, discrepancyCount, comparisonMessages);
        return comparisonMessages;
    }
    public CountAt getProductCount()
    {
        return productCount;
    }
    public void setProductCount(CountAt p_productCount)
    {
        productCount = p_productCount;
    }
    public CountAt getProductEventCount()
    {
        return productEventCount;
    }
    public void setProductEventCount(CountAt p_productEventCount)
    {
        productEventCount = p_productEventCount;
    }
    public CountAt getOrsidCount()
    {
        return this.orsidCount;
    }
    public void setOrsidCount(CountAt orsidCount)
    {
        this.orsidCount = orsidCount;
    }
    public CountAt getOrsidIndex()
    {
        return this.orsidIndex;
    }
    public void setOrsidIndex(CountAt orsidIndex )
    {
        this.orsidIndex = orsidIndex;
    }

    @Override
    public String toString()
    {
        StringBuffer teSynchPointSb = new StringBuffer();
        teSynchPointSb.append("\n$$$$$$$$$$$$$$$$$$$$$$$ OHS SynchPoint $$$$$$$$$$$$$$$$$$$$$$$").append("\n");
        teSynchPointSb.append(this.getOrderCount().toString()).append("\n");
        teSynchPointSb.append(this.getOrderEventCount().toString()).append("\n");
        teSynchPointSb.append(this.getOrsidCount().toString()).append("\n");
        teSynchPointSb.append(this.getOrsidIndex().toString()).append("\n");
        teSynchPointSb.append(this.productCount.toString()).append("\n");
        teSynchPointSb.append(this.productEventCount.toString()).append("\n");
        teSynchPointSb.append(this.getUserCount().toString()).append("\n");
        teSynchPointSb.append(this.getUserEventCount().toString()).append("\n");
        teSynchPointSb.append("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$").append("\n");
        return teSynchPointSb.toString();
    }

    public void setMaxThresholdForCount(String countName, Double maxPercentThreshold) throws DataValidationException
    {
        validateMaxThreshold(maxPercentThreshold);
        
        if(SynchPoint.ORDER_PROPERTYKEY.equals(countName))
        {
            this.setOrderCountMaxthreshold(maxPercentThreshold);
        }
        else if(SynchPoint.ORDERUPDATE_PROPERTYKEY.equals(countName))
        {
            this.setOrderUpdateMaxthreshold(maxPercentThreshold);
        }
        else if(SynchPoint.PRODUCT_PROPERTYKEY.equals(countName))
        {
            this.setProductMaxthreshold(maxPercentThreshold);
        }
        else if(SynchPoint.PRODUCTUPDATE_PROPERTYKEY.equals(countName))
        {
            this.setProductUpdateMaxthreshold(maxPercentThreshold);
        }
        else if(SynchPoint.USER_PROPERTYKEY.equals(countName))
        {
            this.setUserMaxthreshold(maxPercentThreshold);
        }
        else if(SynchPoint.USERUPDATE_PROPERTYKEY.equals(countName))
        {
            this.setUserUpdateMaxthreshold(maxPercentThreshold);
        }
        else if(SynchPoint.ORSID_PROPERTYKEY.equals(countName))
        {
            this.setOrsidMaxthreshold(maxPercentThreshold);
        }
        else if(SynchPoint.ORSIDINDEX_PROPERTYKEY.equals(countName))
        {
            this.setOrsidIndexMaxthreshold(maxPercentThreshold);
        }
        else 
        {
            throw ExceptionBuilder.dataValidationException("Invalid count name[" + countName + "], unable to set max threshold", -2);
        }
    }
    public String getSynchPointComparisonHistory(String countName)
    {
        StringBuffer spHistory = new StringBuffer();
        if(SynchPoint.ORDER_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(getOrderCount().getHistory());
        }
        
        if(SynchPoint.ORDERUPDATE_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.getOrderEventCount().getHistory());
        }
        
        if(SynchPoint.PRODUCT_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.productCount.getHistory());
        }
        
        if(SynchPoint.ORSID_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.getOrsidCount().getHistory());
        }
        
        if(SynchPoint.ORSIDINDEX_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.getOrsidIndex().getHistory());
        }
        
        if(SynchPoint.PRODUCTUPDATE_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.productEventCount.getHistory());
        }
        if(SynchPoint.USER_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.getUserCount().getHistory());
        }
        if(SynchPoint.USERUPDATE_PROPERTYKEY.equals(countName) || "all".equalsIgnoreCase(countName))
        {
            spHistory.append(this.getUserEventCount().getHistory());
        }
        return spHistory.toString();
    }
    public void setEventCountOffsets(OhsSynchPoint dcSynchPoint)
    {
        super.setEventCountOffsets(dcSynchPoint);
        this.productEventCount.setOffset(dcSynchPoint.productEventCount.getCurrentCount() - this.productEventCount.getCurrentCount());
    }
    public void resetEventCountOffsets()
    {
        super.resetEventCountOffsets();
        this.productEventCount.setOffset(0);
    }

    private void setUserUpdateMaxthreshold(Double maxPercentThreshold)
    {
        this.getUserEventCount().setPercentMaxThreshold(maxPercentThreshold);
    }
    private void setUserMaxthreshold(Double maxPercentThreshold)
    {
        this.getUserCount().setPercentMaxThreshold(maxPercentThreshold);
    }
    private void setProductUpdateMaxthreshold(Double maxPercentThreshold)
    {
        this.productEventCount.setPercentMaxThreshold(maxPercentThreshold);
    }
    private void setProductMaxthreshold(Double maxPercentThreshold)
    {
        this.productCount.setPercentMaxThreshold(maxPercentThreshold);
    }
    private void setOrderUpdateMaxthreshold(Double maxPercentThreshold)
    {
        this.getOrderEventCount().setPercentMaxThreshold(maxPercentThreshold);
    }
    private void setOrderCountMaxthreshold(Double maxPercentThreshold)
    {
        this.getOrderCount().setPercentMaxThreshold(maxPercentThreshold);
    }
    private void setOrsidIndexMaxthreshold(Double maxPercentThreshold)
    {
        this.orsidCount.setPercentMaxThreshold(maxPercentThreshold);
    }
    private void setOrsidMaxthreshold(Double maxPercentThreshold)
    {
        this.orsidIndex.setPercentMaxThreshold(maxPercentThreshold);
    }
    private void validateMaxThreshold(Double maxPercentThreshold) throws DataValidationException
    {
        if( maxPercentThreshold < 0 )
        {
            throw ExceptionBuilder.dataValidationException("Invalid threhsold[" + maxPercentThreshold + "], must be >= 0", COUNT_NOT_COMPARED);
        }
    }
}
