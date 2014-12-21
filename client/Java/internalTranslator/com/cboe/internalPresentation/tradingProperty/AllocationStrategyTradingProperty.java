//
// -----------------------------------------------------------------------------------
// Source file: AllocationStrategyTradingProperty.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.util.*;

import com.cboe.idl.tradingProperty.AllocationStrategyStructV2;

import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.domain.tradingProperty.AbstractTradingProperty;
import com.cboe.domain.tradingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.util.StructBuilder;

/**
 * Represents a TradingProperty for a single AllocationStrategyStructV2
 */
public class AllocationStrategyTradingProperty extends AbstractTradingProperty implements Comparable
{
    public static final String STRUCT_CHANGE_EVENT = "StructChange";
    public static final String ALLOCATION_TRADE_TYPE_CHANGE_EVENT = "AllocationTradeType";
    public static final String DEFAULT_STRATEGY_CODE_CHANGE_EVENT = "DefaultStrategyCode";
    public static final String PRIORITIZED_STRATEGY_CODES_CHANGE_EVENT = "PrioritizedStrategyCodes";

    private AllocationStrategyStructV2 allocationStrategyStruct;

//    private Map propertyDefinitionMap = new HashMap(3);
//
//    public PropertyDefinition getPropertyDefinition(String fieldName)
//            throws NotFoundException
//    {
//        if("allocationTradeType".equals(fieldName) || "defaultStrategyCode".equals(fieldName) ||
//           "prioritizedStrategyCodes".equals(fieldName))
//        {
//            Object[] nameElements = {getTradingPropertyType().getName(), fieldName};
//            String name = BasicPropertyParser.buildCompoundString(nameElements);
//
//            PropertyDefinition definition = (PropertyDefinition) propertyDefinitionMap.get(name);
//            if(definition == null)
//            {
//                if("allocationTradeType".equals(fieldName))
//                {
//                    List possibleValues = new ArrayList(7);
//                    possibleValues.add(new Short(AllocationTradeTypes.REGULAR));
//                    possibleValues.add(new Short(AllocationTradeTypes.INTERNALIZATION_AUCTION_TRADE));
//                    possibleValues.add(new Short(AllocationTradeTypes.INTERNALIZATION_NO_AUCTION_TRADE));
//                    possibleValues.add(new Short(AllocationTradeTypes.STRATEGY_AUCTION_TRADE));
//                    possibleValues.add(new Short(AllocationTradeTypes.OPENING));
//                    possibleValues.add(new Short(AllocationTradeTypes.QUOTE_LOCK_MIN_TRADE));
//                    possibleValues.add(new Short(AllocationTradeTypes.QUOTE_TRIGGER));
//
//                    List displayValues = new ArrayList(7);
//                    displayValues.add("Regular");
//                    displayValues.add("Auction Default Trade");
//                    displayValues.add("Auction Strategy Trade");
//                    displayValues.add("Internalization Trade");
//                    displayValues.add("Opening");
//                    displayValues.add("Quote Lock Min Trade");
//                    displayValues.add("Quote Trigger");
//
//                    definition = PropertyFactory.createPropertyDefinition(Short.toString(AllocationTradeTypes.OPENING),
//                                                                          Short.class,
//                                                                          possibleValues, displayValues,
//                                                                          "Allocation Trade Type PD", name);
//                }
//                else
//                {
//                    List possibleValues = new ArrayList(19);
//                    possibleValues.add(new Short(AllocationStrategyCodes.BEST_DPMCOMPLEX_UMA));
//                    possibleValues.add(new Short(AllocationStrategyCodes.BEST_DPMCOMPLEXREVISED_CAPPEDUMAWITHDPM));
//                    possibleValues.add(new Short(AllocationStrategyCodes.BEST_OF_DPM_UMA));
//                    possibleValues.add(new Short(AllocationStrategyCodes.CAPPEDUMA));
//                    possibleValues.add(new Short(AllocationStrategyCodes.CAPPEDUMA_WITH_DPM));
//                    possibleValues.add(new Short(AllocationStrategyCodes.CUSTOMER));
//                    possibleValues.add(new Short(AllocationStrategyCodes.DPM_COMPLEX));
//                    possibleValues.add(new Short(AllocationStrategyCodes.DPM_COMPLEX_REVISED));
//                    possibleValues.add(new Short(AllocationStrategyCodes.DPM_FIXED_PCT));
//                    possibleValues.add(new Short(AllocationStrategyCodes.DPM_SCALED_PCT));
//                    possibleValues.add(new Short(AllocationStrategyCodes.DPM_VAR_PCT));
//                    possibleValues.add(new Short(AllocationStrategyCodes.LOCK_MINIMUM_TRADE));
//                    possibleValues.add(new Short(AllocationStrategyCodes.MARKET_TURNER));
//                    possibleValues.add(new Short(AllocationStrategyCodes.PREF_DPM_UMA));
//                    possibleValues.add(new Short(AllocationStrategyCodes.PRICE_TIME));
//                    possibleValues.add(new Short(AllocationStrategyCodes.PRO_RATA));
//                    possibleValues.add(new Short(AllocationStrategyCodes.UMA_Q_NONQ));
//                    possibleValues.add(new Short(AllocationStrategyCodes.UMA_VAR_PCT));
//                    possibleValues.add(new Short(AllocationStrategyCodes.UMA_WITH_DPM));
//
//                    List displayValues = new ArrayList(19);
//                    displayValues.add("Best DPM Complex UMA");
//                    displayValues.add("Best DPM Complex Revised Capped UMA with DPM");
//                    displayValues.add("Best of DPM UMA");
//                    displayValues.add("Capped UMA");
//                    displayValues.add("Capped UMA with DPM");
//                    displayValues.add("Customer");
//                    displayValues.add("DPM Complex");
//                    displayValues.add("DPM Complex Revised");
//                    displayValues.add("DPM Fixed Percentange");
//                    displayValues.add("DPM Scaled Percentange");
//                    displayValues.add("DPM Variable Percentange");
//                    displayValues.add("Lock Minimum Trade");
//                    displayValues.add("Market Turner");
//                    displayValues.add("Preferred DPM UMA");
//                    displayValues.add("Price Time");
//                    displayValues.add("Pro Rata");
//                    displayValues.add("UMA Q Non Q");
//                    displayValues.add("UMA Variable Percentage");
//                    displayValues.add("UMA with DPM");
//
//                    String displayName;
//                    if("defaultStrategyCode".equals(fieldName))
//                    {
//                        displayName = "Default Strategy Code PD";
//                    }
//                    else
//                    {
//                        displayName = "Prioritized Strategy Codes PD";
//                    }
//
//                    definition = PropertyFactory.createPropertyDefinition(Short.toString(AllocationStrategyCodes.PRICE_TIME),
//                                                                          Short.class,
//                                                                          possibleValues, displayValues,
//                                                                          displayName, name);
//                }
//
//                propertyDefinitionMap.put(name, definition);
//            }
//            return definition;
//        }
//        else
//        {
//            return super.getPropertyDefinition(fieldName);
//        }
//    }

    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AllocationStrategyTradingProperty(String sessionName, int classKey)
    {
        super(AllocationStrategyTradingPropertyGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param allocationStrategyStruct value for the trading property
     */
    public AllocationStrategyTradingProperty(String sessionName, int classKey,
                                             AllocationStrategyStructV2 allocationStrategyStruct)
    {
        this(sessionName, classKey);
        setAllocationStrategyStruct(allocationStrategyStruct);
    }

    public int hashCode()
    {
        return getAllocationTradeType();
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        AllocationStrategyTradingProperty clonedTradingProperty = (AllocationStrategyTradingProperty) super.clone();
        AllocationStrategyStructV2 clonedStruct =
                StructBuilder.cloneAllocationStrategyStructV2(getAllocationStrategyStruct());
        clonedTradingProperty.allocationStrategyStruct = clonedStruct;

        return clonedTradingProperty;
    }

    public int compareTo(Object object)
    {
        int result;
        short myValue = getAllocationTradeType();
        short theirValue = ((AllocationStrategyTradingProperty) object).getAllocationTradeType();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    public String getPropertyName()
    {
        return Short.toString(getAllocationTradeType());
    }

    public AllocationStrategyStructV2 getAllocationStrategyStruct()
    {
        return allocationStrategyStruct;
    }

    public void setAllocationStrategyStruct(AllocationStrategyStructV2 allocationStrategyStruct)
    {
        AllocationStrategyStructV2 oldValue = this.allocationStrategyStruct;
        this.allocationStrategyStruct = allocationStrategyStruct;
        firePropertyChange(STRUCT_CHANGE_EVENT, oldValue, allocationStrategyStruct);
    }

    public short getAllocationTradeType()
    {
        short result = 0;
        if(getAllocationStrategyStruct() != null)
        {
            result = getAllocationStrategyStruct().allocationTradeType;
        }
        return result;
    }

    public void setAllocationTradeType(short allocationTradeType)
    {
        if(getAllocationStrategyStruct() == null)
        {
            setAllocationStrategyStruct(new AllocationStrategyStructV2());
        }
        short oldValue = getAllocationStrategyStruct().allocationTradeType;
        getAllocationStrategyStruct().allocationTradeType = allocationTradeType;
        firePropertyChange(ALLOCATION_TRADE_TYPE_CHANGE_EVENT, oldValue, allocationTradeType);
    }

    public short getDefaultStrategyCode()
    {
        short result = 0;
        if(getAllocationStrategyStruct() != null)
        {
            result = getAllocationStrategyStruct().defaultStrategyCode;
        }
        return result;
    }

    public void setDefaultStrategyCode(short defaultStrategyCode)
    {
        if(getAllocationStrategyStruct() == null)
        {
            setAllocationStrategyStruct(new AllocationStrategyStructV2());
        }
        short oldValue = getAllocationStrategyStruct().defaultStrategyCode;
        getAllocationStrategyStruct().defaultStrategyCode = defaultStrategyCode;
        firePropertyChange(DEFAULT_STRATEGY_CODE_CHANGE_EVENT, oldValue, defaultStrategyCode);
    }

    public short getPrioritizedStrategyCodes(int index)
    {
        return getPrioritizedStrategyCodes()[index];
    }

    public void setPrioritizedStrategyCodes(int index, short prioritizedStrategyCode)
    {
        if(getAllocationStrategyStruct() == null)
        {
            setPrioritizedStrategyCodes(new short[index + 1]);
        }
        short[] oldValue = getAllocationStrategyStruct().prioritizedStrategyCodes;
        getAllocationStrategyStruct().prioritizedStrategyCodes[index] = prioritizedStrategyCode;
        firePropertyChange(PRIORITIZED_STRATEGY_CODES_CHANGE_EVENT, oldValue,
                           getAllocationStrategyStruct().prioritizedStrategyCodes);
    }

    public short[] getPrioritizedStrategyCodes()
    {
        short[] result = new short[0];
        if(getAllocationStrategyStruct() != null)
        {
            result = getAllocationStrategyStruct().prioritizedStrategyCodes;
        }
        return result;
    }

    public void setPrioritizedStrategyCodes(short[] prioritizedStrategyCodes)
    {
        if(getAllocationStrategyStruct() == null)
        {
            setAllocationStrategyStruct(new AllocationStrategyStructV2());
        }
        short[] newArray = new short[prioritizedStrategyCodes.length];
        System.arraycopy(prioritizedStrategyCodes, 0, newArray, 0, prioritizedStrategyCodes.length);

        short[] oldValue = getAllocationStrategyStruct().prioritizedStrategyCodes;
        getAllocationStrategyStruct().prioritizedStrategyCodes = newArray;
        firePropertyChange(PRIORITIZED_STRATEGY_CODES_CHANGE_EVENT, oldValue,
                           getAllocationStrategyStruct().prioritizedStrategyCodes);
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return AllocationStrategyTradingPropertyGroup.TRADING_PROPERTY_TYPE;
    }

    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"allocationTradeType", "defaultStrategyCode", "prioritizedStrategyCodes"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
