/**
 * 
 */
package com.cboe.domain.tradingProperty;

import java.util.Comparator;

import com.cboe.exceptions.DataValidationException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.tradingProperty.RolloutFlagByBC;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;


public class RolloutFlagByBCImpl extends AbstractTradingProperty
        implements RolloutFlagByBC
{
    private TradingPropertyType tradingPropertyType;
    private static final String prefix="BC00";
    private static final String suffix="x1";
    private static final String ALL="ALL";
    private static final String envPrefix= System.getProperty("prefix","prod");
    
    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public RolloutFlagByBCImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey)
    {
        super(tradingPropertyType.getName(), sessionName, classKey);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param property to initialize with
     */
    public RolloutFlagByBCImpl(TradingPropertyType tradingPropertyType, String sessionName, int classKey,
                                       Property property)
            throws DataValidationException
    {
        super(sessionName, classKey, property);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName
     * @param classKey
     * @param newAutoLinkDisqualifiedExchanges
     * @param newAutoLinkDisqualifiedExchangesFlag
     */
    public RolloutFlagByBCImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey,
                                       int newRolloutBC, int newRolloutBCFlag)
    {
        this(tradingPropertyType, sessionName, classKey);
        
        setBCId(newRolloutBC);
        setRolloutFlag(newRolloutBCFlag);
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public RolloutFlagByBCImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey, String value)
    {
        super(tradingPropertyType.getName(), sessionName, classKey, value);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * Compares based on getAutoLinkDisqualifiedExchanges()
     */
    public int compareTo(Object object)
    {
        int result;
        int myValue = getBCId();
        int theirValue = ((RolloutFlagByBC) object).getBCId();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Overriden to return the getAutoLinkDisqualifiedExchanges(), converted to a String, then the hashCode obtained of the
     * resulting String.
     */ 
    public int hashCode()
    {
        return Integer.toString(getBCId()).hashCode();
    }

    /**
     * @see com.cboe.interfaces.domain.tradingProperty.TradingProperty#getTradingPropertyType()
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    /**
     * Returns the getAutoLinkDisqualifiedExchanges() as a String.
     */
    public String getPropertyName()
    {
        return Integer.toString(getBCId());
    }

 
    /**
     * Allows the Trading Property to determine the order of the PropertyDescriptor's. This implementation will just
     * return a comparator that will sort by the desired property descriptor order from the BeanInfo.
     * @return comparator to use for sorting the returned PropertyDescriptor's from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"rolloutBC", "rolloutBCFlag"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }

	public String getBC() {
		if (getBCId()==0)
		{
			return ALL;
		}
		String id = getBCId()+"";
		int idLen =id.length();
		if (idLen <2)
		{
			return prefix.substring(0,4-idLen)+id;
		}
		else
		{
			return "BC"+id;
		}
	}

	public int getRolloutFlag() {
		return getInteger2();
	}

	public int getBCId() {
		return getInteger1();
	}

	public boolean isRollout() {
		return (getRolloutFlag() == 1 ? true : false);
	}

	public Boolean getRollout()
	{
		return (getRolloutFlag() == 1 ? true : false);
	}

	public void setBC(String rolloutBC) {

		if (rolloutBC.equals(ALL))
		{
			   setInteger1(0);
			   return;
		}
        String bc = rolloutBC;
     
		try
		{
			int id = Integer.parseInt(bc);
			if(Log.isDebugOn())
		   {
		       Log.debug("RolloutFlagByBCImpl>> Saving converted rollout bc flag value = " + rolloutBC);
		   }
		   setInteger1(id);
		}
		catch (Exception e)
		{
			Log.alarm("Invalid format for BC:"+rolloutBC+ "  Error:"+e.getMessage());
		}

	}

	public void setRolloutFlag(int rolloutFlag) {
	       if(Log.isDebugOn())
	        {
	            Log.debug("RolloutFlagByBCImpl>> Saving rollout bc flag value = " + rolloutFlag);
	        }

	        setInteger2(rolloutFlag);
	}

	
	public void setRollout(boolean rolloutFlag) {
	       if(Log.isDebugOn())
	        {
	            Log.debug("RolloutFlagByBCImpl>> Saving rollout bc flag value = " + rolloutFlag);
	        }
	        int enabledFlag = (rolloutFlag ? 1 : 0);
	        setRolloutFlag(enabledFlag);
	}

	
	public void setBCId(int rolloutBCId) {
	       if(Log.isDebugOn())
	        {
	            Log.debug("RolloutFlagByBCImpl>> Saving rollout bc flag value = " + rolloutBCId);
	        }

	        setInteger1(rolloutBCId);
	}

	public boolean isAllBC() {
		return getBCId()==0;
		
	}


	
	public String getGroupName() {
		return envPrefix+getBC()+suffix;
	}
}
