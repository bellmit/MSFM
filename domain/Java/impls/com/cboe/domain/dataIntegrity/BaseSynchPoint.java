package com.cboe.domain.dataIntegrity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.dataIntegrity.SynchPoint;
import com.cboe.util.ExceptionBuilder;

/**
 * A class to hold all the counts and a timestamp associated with business cluster entitie and event counts
 * 
 * @author dowat
 */
public abstract class BaseSynchPoint implements Serializable, SynchPoint
{
    private static final long serialVersionUID = 292987559063346532L;
    protected static final int COUNT_NOT_COMPARED = -3;
    public static final String AGREGATECOMPARISONRESULT_DISCREPANCY_MSG = "DISHI_SYNCHPOINTCOMPARE_DISCREPANCY - Master and Slave synchpoints have counts that are out of range";
    public static final String AGREGATECOMPARISONRESULT_MATCH_MSG = "DISHI_SYNCHPOINTCOMPARE_MATCH - Master and Slave synchpoints are in range of one another";
    private static final int MAX_COMPARISON_HISTORY_ENTRIES = 1;
    private static final String OUT_OF_SYNCH = "OUT_OF_SYNCH";
    private static final String IN_SYNCH = "IN_SYNCH";
    private static final String STATUS_MSG_IFS = "|";
    private static final String STATUS_MSG_DETAILS_IFS = STATUS_MSG_IFS.concat(STATUS_MSG_IFS); // equals "||"
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MMMMM.dd H:mm:ss:SSS");

    protected Map<String, String> thresholdParams;

    public long getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(long p_createTime)
    {
        createTime = p_createTime;
    }

    public String getSynchPointStatus()
    {
        return synchPointStatus;
    }

    public void setSynchPointStatus(String p_synchPointStatus)
    {
        synchPointStatus = p_synchPointStatus;
    }

    public Map<String, String> getThresholdParams()
    {
        return thresholdParams;
    }

    public void setLastCountAddedTime(long p_lastCountAddedTime)
    {
        lastCountAddedTime = p_lastCountAddedTime;
    }

    protected long createTime;
    protected long lastCountAddedTime;
    protected String serverType;
    protected int numCounts = 4;

    private CountAt orderCount;
    private CountAt orderEventCount;
    private CountAt userCount;
    private CountAt userEventCount;
    private long counter = 0;
    private int configuredPeriod;

    protected static boolean verboseLogging;
    
    protected String synchPointStatus = BaseSynchPoint.OUT_OF_SYNCH.concat(BaseSynchPoint.STATUS_MSG_IFS).concat("No synchpoint comparison done yet").concat(STATUS_MSG_DETAILS_IFS);
    
    @Override
    public abstract String toString();
  
    public BaseSynchPoint(String serverType)
    {
        this.serverType = serverType;
        this.createTime = System.currentTimeMillis();
        this.lastCountAddedTime = this.createTime;
    }
    /**
     * This method is responsible for creating all the counts applicable to the specific <code>SynchPoint</code> 
     * subtype with their default thresholds
     */
    abstract public void initializeCounts(Map<String, String> thresholds) throws DataValidationException;
    /**
     * This method is responsible for comaprison of this <code>SynchPoint</code> to the given SynchPoint instance. 
     */
    abstract public Map<String, String> compareWith(SynchPoint primaryCounts) throws DataValidationException;
   
    public void setThresholdParams(Map<String, String> thresholdParams)
    {
        this.thresholdParams = thresholdParams;
    }
    public int getConfiguredPeriod()
    {
        return configuredPeriod;
    }
    public void setConfiguredPeriod() throws DataValidationException
    {
        // Get the count time period
        String periodString = thresholdParams.get(SynchPoint.PERIOD_PROPERTYKEY);
        int period = 0;
        try
        {
            configuredPeriod = Integer.parseInt(periodString);
        }
        catch (NumberFormatException e)
        {
            throw ExceptionBuilder.dataValidationException("Caught NumberFormatException while converting[" + periodString + "] to an integer", -1);
        }
    }
    public int getNumCounts()
    {
        return numCounts;
    }

    public void setNumCounts(int p_numCounts)
    {
        numCounts = p_numCounts;
    }
    
    public long getCountCreateTime()
    {
        return createTime;
    }
    public long getLastCountAddedTime()
    {
        return lastCountAddedTime;
    }
    public boolean isInitializingSynchPoint()
    {
        return this.getCounter() == 0;
    }
    public void increment()
    {
        System.out.println("SYNCHPOINTCOUNTER[" + this.getCounter() + "] BEFORE INCREMENT" );
        this.setCounter(this.getCounter() + 1);
        System.out.println("SYNCHPOINTCOUNTER[" + this.getCounter() + "] AFTER INCREMENT" );
    }
    public void setEventCountOffsets(BaseSynchPoint sp)
    {
        this.getOrderEventCount().setOffset(sp.getOrderEventCount().getCurrentCount() - this.getOrderEventCount().getCurrentCount());
        this.getUserEventCount().setOffset(sp.getUserEventCount().getCurrentCount() - this.getUserEventCount().getCurrentCount());
    }
    public void resetEventCountOffsets()
    {
        this.getOrderEventCount().setOffset(0);
        this.getUserEventCount().setOffset(0);
    }
    public void resetCounter()
    {
        this.setCounter(0);
    }
    public long getSynchPointCounter()
    {
        return this.getCounter();
    }
    public void setSynchPointCounter(int p_synchPointCounter)
    {
        this.counter = p_synchPointCounter;
    }
    protected String getCountComparisonMsg(CountAt masterCount, CountAt slaveCount, int result)
    {
        String resultString = CountAt.ABOVERANGE == result ? CountAt.ABOVERANGESTRING : CountAt.BELOWRANGESTRING;
        return "Count[" + masterCount.getName() + 
               "] ComparisonResult=[" + resultString + 
               "] MasterCount=[" + masterCount.getCount() + 
               "] SlaveCount=[" + slaveCount.getCount() + "]";
    }
    protected void setHistoricalComparisonResult(CountAt masterCount, CountAt slaveCount, int result)
    {
        
        Date readTime = new Date(masterCount.currentTimestamp);
        String readTimeFormatted = formatter.format(readTime);

        String resultString = CountAt.getResultString(result);
        String historyEntry = readTimeFormatted + ", " +
                              masterCount.getCount() + ", " + 
                              slaveCount.getCount() + ", " +
                              resultString;
        slaveCount.setHistoryEntry( historyEntry );
    }

    public CountAt getOrderCount()
    {
        return orderCount;
    }

    public void setOrderCount(CountAt orderCount)
    {
        this.orderCount = orderCount;
    }

    public CountAt getOrderEventCount()
    {
        return orderEventCount;
    }

    public void setOrderEventCount(CountAt orderEventCount)
    {
        this.orderEventCount = orderEventCount;
    }

    public CountAt getUserCount()
    {
        return userCount;
    }

    public void setUserCount(CountAt userCount)
    {
        this.userCount = userCount;
    }

    public CountAt getUserEventCount()
    {
        return userEventCount;
    }
    
    public void setUserEventCount(CountAt userEventCount)
    {
        this.userEventCount = userEventCount;
    }

    public void setCounter(long counter)
    {
        this.counter = counter;
    }

    public long getCounter()
    {
        return counter;
    }
    
    public void addOrderUpdateCount(long count, long timestamp) throws DataValidationException
    {
        this.getOrderEventCount().addCount(count, timestamp);
        this.lastCountAddedTime = timestamp;
    }
    public void addOrderCount(long count, long timestamp) throws DataValidationException
    {
        this.getOrderCount().addCount(count, timestamp);
        this.lastCountAddedTime = timestamp;
    }
    public void addUserCount(long count, long timestamp) throws DataValidationException
    {
        this.getUserCount().addCount(count, timestamp);
        this.lastCountAddedTime = timestamp;
    }
    public void addUserUpdateCount(long count, long timestamp) throws DataValidationException
    {
        this.getUserEventCount().addCount(count, timestamp);
        this.lastCountAddedTime = timestamp;
    }
    public String getSynchPointStatus(boolean verbose)
    {
        long currentTimeMillis = System.currentTimeMillis();
        String timestamp = this.getDateFormatter().format(new Date(this.getLastCountAddedTime()));
        long timeSinceLastUpdate = currentTimeMillis - this.getLastCountAddedTime();
        int numSynchPointsMissed = (int) (timeSinceLastUpdate/30000);
        String status = "synchPointsMissed=".concat(String.valueOf(numSynchPointsMissed)).concat(BaseSynchPoint.STATUS_MSG_IFS);
        if(verbose)
        {
            status = status.concat(this.synchPointStatus);
        }
        else
        {
            status = status.concat(this.synchPointStatus.substring(0, this.synchPointStatus.indexOf(BaseSynchPoint.STATUS_MSG_DETAILS_IFS)));
        }
        return status;
    }
    public void setSynchPointStatus(long createdTimestamp, int result, Map<String, String> resultMsgs)
    {
        this.synchPointStatus = generateSynchPointStatus(createdTimestamp, result, this.configuredPeriod * 1000 ,resultMsgs);
    }
    private String generateSynchPointStatus(long p_createdTimestamp, int p_result, long configuredPeriod, Map<String, String> p_resultMsgs)
    {
        long currentTimeMillis = System.currentTimeMillis();
        String timestamp = this.getDateFormatter().format(new Date(p_createdTimestamp));
        String result = "timestamp=".concat(timestamp).concat(BaseSynchPoint.STATUS_MSG_IFS);
        result = result.concat("result=");
        if(p_result > 0) // we have some discrepancies
        {
            result = result.concat(OUT_OF_SYNCH).concat(BaseSynchPoint.STATUS_MSG_IFS).concat(BaseSynchPoint.STATUS_MSG_IFS);
            Collection<String> msgs = p_resultMsgs.values();
            for(String msg:msgs)
            {
                result = result.concat(msg).concat(BaseSynchPoint.STATUS_MSG_IFS);
            }
        }
        else
        {
            result = result.concat(IN_SYNCH).concat(BaseSynchPoint.STATUS_MSG_IFS).concat(BaseSynchPoint.STATUS_MSG_IFS);
        }
        return result;
    }
    protected void setConfiguredPeriod(int p_configuredPeriod)
    {
        this.configuredPeriod = p_configuredPeriod;
    }
    protected String getServerType()
    {
        return this.serverType;
    }
    protected void setServerType(String p_serverType)
    {
        this.serverType = p_serverType;
    }
    
    /**
     * Returns a copy of the object, or null if the object cannot
     * be serialized.
     * @throws IOException 
     * @throws ClassNotFoundException 
     */
    public static Object copy(Object orig) throws IOException, ClassNotFoundException {
        Object obj = null;
        // Write the object out to a byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(orig);
        out.flush();
        out.close();
        // Make an input stream from the byte array and read
        // a copy of the object back in.
        ObjectInputStream in = new ObjectInputStream(
            new ByteArrayInputStream(bos.toByteArray()));
        obj = in.readObject();
        System.out.println("Original[" + orig.hashCode()+ "] copy[" + obj.hashCode() + "] to_string[" + obj.toString() + "]");
        return obj;
    }
    public static SimpleDateFormat getDateFormatter()
    {
        return formatter;
    }
}
