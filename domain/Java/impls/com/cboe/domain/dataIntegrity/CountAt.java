package com.cboe.domain.dataIntegrity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cboe.exceptions.DataValidationException;
import com.cboe.util.ExceptionBuilder;

/**
 * A value object that represents a count of something at a specific time
 * 
 * @author dowat
 */
public class CountAt implements Serializable
{
    private static final long serialVersionUID = 4036989851950481737L;
    // Count types
    protected static final int COUNT_TYPE_ABSOLUTE = 1;
    private static final int COUNT_TYPE_DELTA = 2;
    
    private long currentCount;    
    private long lastCount;
    private long previousCount;
    private long offset = 0;
    private int countType;
    private String serverType;
    private String name;
    transient RollingHistory history;
    long maxThreshold = -1;
    double percentMaxThreshold;
    long currentTimestamp;
    long lastTimestamp;
    int minimumPoolSize;
    
    // Configurable constants
    // TODO Fill these from property values, default it to 90 minutes of history
    public int MAX_HISTORY_ENTRIES = 180;
    // Minimum boundary radius is 1/10 of 1% of the count
    public static final double MIN_PERCENT_MAX_VALUE = 0;
    // Maximum boundary radius is 100% of the count
    public static final double MAX_PERCENT_MAX_VALUE = 1.0;
    
    private static final int NEGATIVE_COUNT_VALUE = -1;
    private static final int BAD_TIMESTAMP_VALUE = -2;

    // Comaprison result constants
    public static final int INRANGE = 0;
    public static final int BELOWRANGE = -1;
    public static final int ABOVERANGE = 1;
    public static final String NO_HISTORY_YET = "No history yet";
    public static final String INRANGESTRING = "IN RANGE";
    public static final String BELOWRANGESTRING = "BELOW RANGE";
    public static final String ABOVERANGESTRING = "ABOVE RANGE";
    public static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MMMMM.dd H:mm:ss:SSS");
    
    public CountAt(String name, String serverType, double percentMaxThreshold, int countType)
    {
        this.name = name;
        this.serverType = serverType;
        this.percentMaxThreshold = percentMaxThreshold;
        this.countType = countType;
        this.currentCount = 0; 
        this.currentTimestamp = System.currentTimeMillis();
        this.lastTimestamp = this.currentTimestamp - 1000;
        this.history = new RollingHistory(MAX_HISTORY_ENTRIES);
    }

    public static CountAt getAbsoluteCount(String name, String serverType, String percentMaxThresholdString) throws DataValidationException
    {
        double percentMaxThreshold = 0;
        try
        {
            percentMaxThreshold = Double.parseDouble(percentMaxThresholdString);
        }
        catch (NumberFormatException e)
        {
            throw ExceptionBuilder.dataValidationException("Caught NumberFormatException while converting[" + percentMaxThresholdString + "] to a double", -1);
        }
        if(!(MIN_PERCENT_MAX_VALUE <= percentMaxThreshold && percentMaxThreshold <= MAX_PERCENT_MAX_VALUE))
        {
            throw ExceptionBuilder.dataValidationException("Invalid maximum threshold percentage, cannot construct CountAt instance", 1);
        }
        return new CountAt(name, serverType, percentMaxThreshold, CountAt.COUNT_TYPE_ABSOLUTE);
    }
    
    public long getCount()
    {
        return currentCount + offset;
    }
    
    public void addCount(long count, long timestamp) throws DataValidationException
    { 
        if(this.currentTimestamp > timestamp)
        {
            throw ExceptionBuilder.dataValidationException("Invalid timestamp[" + timestamp + 
                                                           "] occurred prior to current timestamp[" + this.currentTimestamp + "]", 
                                                           CountAt.BAD_TIMESTAMP_VALUE);
        } 
        if(count < 0)
        {
            throw ExceptionBuilder.dataValidationException("Invalid count[" + count + "] is less than 0", CountAt.NEGATIVE_COUNT_VALUE);
        }
        this.previousCount = this.currentCount;
        this.lastTimestamp = this.currentTimestamp; 
        this.setCount( count );
        this.currentTimestamp = timestamp;
    }

    private void setCount(long count)
    {
        this.currentCount = count;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public double getPercentMaxThreshold()
    {
        return percentMaxThreshold;
    }
    
    public void setPercentMaxThreshold(double d)
    {
        this.percentMaxThreshold = d;
    }
    
    public long getTimestamp()
    {
        return currentTimestamp;
    }
    
    public void setTimestamp(long timestamp)
    {
        this.currentTimestamp = timestamp;
    }
    
    public long getMaxThreshold()
    {
        this.maxThreshold = (long)Math.round(this.getCount() * this.percentMaxThreshold);
        return maxThreshold;
    }
    
    public int isInRange(CountAt otherCount)
    {
        // Assume count is in range
        int returnValue = CountAt.INRANGE;
        // check to see if it is below range
        if(otherCount.getCount() < this.getBottomOfRange())
        {
            returnValue = CountAt.BELOWRANGE;
        }
        else if (otherCount.getCount() > this.getTopOfRange())
        {
            returnValue = CountAt.ABOVERANGE;
        }
        return returnValue;
    }

    public long getTopOfRange()
    {
        return this.getCount() + this.getMaxThreshold();
    }
    
    public long getBottomOfRange()
    {
        return this.getCount() - this.getMaxThreshold();
    }
    
    public String getLastHistoryEntry()
    {
        return this.history.getHistoryEntry();
    }
    
    public int getHistoryIdx()
    {
        return this.history.getHistoryIndex();
    }
    
    public String getHistory()
    {
        return "\nHHHHHHHHHHHHHHHHHHHHH " + this.getName()+ " COUNT COMPARISON HISTORY HHHHHHHHHHHHHHHHHHHHH\n" +
               "Timestamp,MasterCount,SlaveCount,Result\n" +
               this.history.getHistory() + 
               "\nHHHHHHHHHHHHHHHHHHHHH " + this.getName() + " COUNT COMPARISON HISTORY HHHHHHHHHHHHHHHHHHHHH\n";
               
    }
    
    public void resizeHistory(int newSize) 
    {
        history = new RollingHistory(newSize);
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
    {
        inputStream.defaultReadObject();
        this.history = new RollingHistory(this.MAX_HISTORY_ENTRIES);
    }

    public class RollingHistory
    {
        private String[] entries;
        private int lastIdx;
        private int currentIdx;
        public RollingHistory(int maxSize)
        {
            entries = new String[maxSize];
            currentIdx = 0;
            lastIdx = 0;
        }
        public void addEntry(String entry)
        {
            this.lastIdx = this.currentIdx;
            // overwrite previous entries.
            this.entries[currentIdx] = entry; 
            // roll the index
            if(currentIdx + 1 > entries.length - 1)
            {
                currentIdx = 0;
            }
            else
            {
                currentIdx++;
            } 
        }
        public String getHistory()
        {
            StringBuffer history = new StringBuffer();
            int entryIdx;
            for(int i=0;i<this.entries.length;i++)
            {
                if(i + this.currentIdx < this.entries.length)
                    entryIdx = this.currentIdx + i;
                else
                    entryIdx = (this.currentIdx + i - this.entries.length);
                if(this.entries[entryIdx] != null && ! this.entries[entryIdx].equals(""))
                {
                    history.append(this.entries[entryIdx] + "\n");
                }
            }
            String historyString = null;
            if(history != null && history.length() > 1)
                historyString = history.substring(0, history.length() - 1);
            else
                historyString = CountAt.NO_HISTORY_YET;
            return historyString;
        }
        public int getHistoryIndex()
        {
            return this.currentIdx;
        }
        public int getLastHistoryIndex()
        {
            return this.lastIdx;
        }
        public String getHistoryEntry()
        {
            return this.entries[this.lastIdx];
        }
    }
    
    public String toString()
    {
        StringBuffer countAtBuffer = new StringBuffer();
        countAtBuffer.append("name=" + this.getName() + ",");
        countAtBuffer.append("count=" + this.getCount() + ",");
        countAtBuffer.append("previousCount=" + this.getPreviousCount() + ",");
        countAtBuffer.append("maxTheshold=" + this.getMaxThreshold() + ",");
        countAtBuffer.append("offset=" + this.getOffset() + ",");
        countAtBuffer.append("topOfRange=" + this.getTopOfRange() + ",");
        countAtBuffer.append("bottomOfRange=" + this.getBottomOfRange() + ",");
        countAtBuffer.append("currentTimestamp=" + this.formatter.format(new Date(this.currentTimestamp)) + ",");
        countAtBuffer.append("lastTimestamp=" + this.formatter.format(new Date(this.lastTimestamp)));
        return countAtBuffer.toString();
        
    }

    private long getPreviousCount()
    {
        return this.previousCount;
    }
    
    public long getOffset()
    {
        return offset;
    }

    public void setOffset(long offset)
    {
        this.offset = offset;
    }

    public static String getResultString(int result)
    {
        String resultString = "";
        if(CountAt.INRANGE == result)
            resultString = CountAt.INRANGESTRING;
        if(CountAt.ABOVERANGE == result)
            resultString = CountAt.ABOVERANGESTRING;
        if(CountAt.BELOWRANGE == result)
            resultString = CountAt.BELOWRANGESTRING;
        
        return resultString;
    }

    public long getPeriodCount()
    {
        return Math.abs(this.currentCount - this.previousCount);
    }

    public void setHistoryEntry(String entry)
    {
        this.history.addEntry(entry);
    }

    public long getCurrentCount()
    {
        return this.currentCount;
    }
}
