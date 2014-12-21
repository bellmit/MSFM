package com.cboe.domain.dataIntegrity;

import java.io.Serializable;

import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.dataIntegrity.SynchPoint;
import com.cboe.util.ExceptionBuilder;

public class CountAtDynamicThreshold extends CountAt implements Serializable
{
    private static final long serialVersionUID = 4279943193749588486L;
    
    private int measurementPeriodUnitMultiplier = 1;
    private int maxNumberOfTimeUnitsDelay = 1;
    
    public static CountAtDynamicThreshold getInstance(String name, String serverType, String unitMultiplierProperty, String maxUnitsDelayProperty) throws DataValidationException
    {
        int period = 0;
        int unitMultiplier = 0;
        int maxUnitsDelay = 0;

        unitMultiplier = unitMultiplierProperty.equals(SynchPoint.TIMEUNIT_SECONDS)? 1 : 1000;
        
        try
        {
            maxUnitsDelay = Integer.parseInt(maxUnitsDelayProperty);
        }
        catch (NumberFormatException e)
        {
            throw ExceptionBuilder.dataValidationException("Caught NumberFormatException while converting[" + maxUnitsDelayProperty + "] to an integer", -1);
        }
        CountAtDynamicThreshold count = new CountAtDynamicThreshold(name, serverType, period, unitMultiplier, maxUnitsDelay, CountAt.COUNT_TYPE_ABSOLUTE);
        return count;
    }
    
    private CountAtDynamicThreshold(String name, String serverType, int period, int unitMultiplier, int maxUnitsDelay, int countType)
    {
        super(name, serverType, 0, countType);
        this.measurementPeriodUnitMultiplier = unitMultiplier;
        this.maxNumberOfTimeUnitsDelay = maxUnitsDelay;
    }

    public int getMeasurementPeriodUnitMultiplier()
    {
        return measurementPeriodUnitMultiplier;
    }

    public void setMeasurementPeriodUnitMultiplier(int p_measurementPeriodUnitMultiplier)
    {
        measurementPeriodUnitMultiplier = p_measurementPeriodUnitMultiplier;
    }

    public int getMaxNumberOfTimeUnitsDelay()
    {
        return maxNumberOfTimeUnitsDelay;
    }

    public void setMaxNumberOfTimeUnitsDelay(int p_maxNumberOfTimeUnitsDelay)
    {
        maxNumberOfTimeUnitsDelay = p_maxNumberOfTimeUnitsDelay;
    }
    
    public long getMaxThreshold()
    {
        float totaltimeunits = ((this.currentTimestamp - this.lastTimestamp)/1000) * this.measurementPeriodUnitMultiplier * 1f;
        float objects_per_timeunit = getPeriodCount() / totaltimeunits;
        float objects = objects_per_timeunit * this.maxNumberOfTimeUnitsDelay;
        this.maxThreshold = (long)Math.round(objects);
        return maxThreshold;
    }
}
