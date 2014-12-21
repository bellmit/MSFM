package com.cboe.domain.util;

import com.cboe.idl.alarm.AlarmActivationStruct;
import com.cboe.idl.alarm.AlarmConditionStruct;
import com.cboe.idl.alarm.AlarmDefinitionStruct;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.KeyDescriptionStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.constants.RemainderHandlingModes;
import com.cboe.idl.constants.TradingRestrictions;
import com.cboe.idl.instrumentationService.instrumentors.InstrumentorInfo;
import com.cboe.idl.instrumentationService.instrumentors.QueueInstrumentorStruct;
import com.cboe.idl.order.LinkageExtensionsStruct;
import com.cboe.idl.order.OrderHandlingInstructionStruct;
import com.cboe.idl.product.ErrorCodeResultStruct;
import com.cboe.idl.trade.TradeReportSettlementStruct;
import com.cboe.idl.tradingProperty.AllocationStrategyStructV2;
import com.cboe.idl.tradingProperty.AuctionBooleanStruct;
import com.cboe.idl.tradingProperty.AuctionLongStruct;
import com.cboe.idl.tradingProperty.AuctionOrderSizeTicksStruct;
import com.cboe.idl.tradingProperty.AuctionRangeStruct;
import com.cboe.idl.tradingProperty.DpmRightsScaleStruct;
import com.cboe.idl.tradingProperty.InternalizationPercentageStruct;
import com.cboe.idl.tradingProperty.TimeRangeStruct;
import com.cboe.idl.util.LocationStruct;
import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.interfaces.adminService.DataItem;
import com.cboe.interfaces.domain.Price;

/**
 * A helper that makes it easy to create valid CORBA structs.  The structs created
 * by the methods of this class have default values for all attributes.  There are
 * also some test methods that can be used to check if a struct is a default struct.
 *
 * @author John Wickberg
 */
public class StructBuilder
{
/**
 * All methods are static, no instance needs to be created.
 *
 * @author John Wickberg
 */
private StructBuilder()
{
    super();
}

/**
 * Creates a default date struct.
 *
 * @return a default struct
 *
 * @author John Wickberg
 */
public static DateStruct buildDateStruct()
{
    // the zero values of the default constructor will be fine
    return new DateStruct();
}
/**
 * Creates a default date/time struct.
 *
 * @return a default struct
 *
 * @author John Wickberg
 */
public static DateTimeStruct buildDateTimeStruct()
{
    return new DateTimeStruct(buildDateStruct(), buildTimeStruct());
}
/**
 * Creates a default price struct.
 *
 * @return a default struct
 *
 * @author John Wickberg
 */
public static PriceStruct buildPriceStruct()
{
    return new PriceStruct(PriceTypes.NO_PRICE, 0, 0);
}
/**
 * Creates a default time struct.
 *
 * @return a default struct
 *
 * @author John Wickberg
 */
public static TimeStruct buildTimeStruct()
{
    // hour of -1 will be used to mark time as a default value
    return new TimeStruct((byte) -1, (byte) 0, (byte) 0, (byte) 0);
}
/**
 * Creates a ExchangeAcronymStruct struct.
 *
 * @return a ExchangeAcronymStruct struct
 *
 * @author Emily Huang
 */
public static ExchangeAcronymStruct buildExchangeAcronymStruct(String exchange, String userAcronym)
{
    return new ExchangeAcronymStruct(exchange, userAcronym);
}
/**
 * Creates a ExchangeFirmStruct struct.
 *
 * @return a ExchangeFirmStruct struct
 *
 * @author Emily Huang
 */
public static ExchangeFirmStruct buildExchangeFirmStruct(String exchange, String firm)
{
    return new ExchangeFirmStruct(exchange, firm);
}
/**
 * Creates a default CboeIdStruct.
 *
 * @return a default CboeIdStruct
 *
 * @author Dan Mannisto
 */
public static CboeIdStruct buildCboeIdStruct( )
{
   return CboeId.createDefaultStruct( );
}

public static ErrorCodeResultStruct buildErrorCodeResultStruct()
{
    ErrorCodeResultStruct error = new ErrorCodeResultStruct();
    error.classKey = 0;
    error.errorCode = 0;
    error.exceptionDescription = "";
    error.failedProducts = buildKeyDescriptionStructSequence();

    return error;
}

public static LocationStruct buildLocationStruct(short locType, String loc)
{
    LocationStruct locationStruct = new LocationStruct();
    locationStruct.location = loc;
    locationStruct.locationType = locType;
    
    return locationStruct;
}

public static OrderHandlingInstructionStruct buildOrderHandlingInstructionStruct()
{
    OrderHandlingInstructionStruct ohiStruct = new OrderHandlingInstructionStruct();
    ohiStruct.executionPrice = StructBuilder.buildPriceStruct();
    ohiStruct.ignoreContingency = true;
    ohiStruct.maximumExecutionVolume = 0;
    ohiStruct.maximumExecutionVolumeReason = 0;
    ohiStruct.remainderHandlingMode = RemainderHandlingModes.NOT_SPECIFIED;
    ohiStruct.tradingRestriction =  TradingRestrictions.NONE;
    return ohiStruct;
}

public static KeyDescriptionStruct[] buildKeyDescriptionStructSequence()
{
    KeyDescriptionStruct rtnStruct = new KeyDescriptionStruct();
    rtnStruct.key = 0;
    rtnStruct.description = "";

    KeyDescriptionStruct[] rtnSeq = {rtnStruct};
    return rtnSeq;
}

/**
 * Clones date struct.
 *
 * @param date struct to be cloned
 * @return cloned struct
 */
public static DateStruct cloneDate(DateStruct date)
{
    DateStruct result = null;
    if (date != null)
    {
        result = new DateStruct();
        result.month = date.month;
        result.day = date.day;
        result.year = date.year;
    }
    return result;
}
/**
 * Clones date time struct.
 *
 * @param dateTime struct to be cloned
 * @return cloned struct
 */
public static DateTimeStruct cloneDateTime(DateTimeStruct dateTime)
{
    DateTimeStruct result = null;
    if (dateTime != null)
    {
        result = new DateTimeStruct();
        result.date = cloneDate(dateTime.date);
        result.time = cloneTime(dateTime.time);
    }
    return result;
}
/**
 * Clones price struct.
 *
 * @param price struct to be cloned
 * @return cloned struct
 */
public static PriceStruct clonePrice(PriceStruct price)
{
    PriceStruct result = null;
    if (price != null)
    {
        result = new PriceStruct();
        result.type = price.type;
        result.whole = price.whole;
        result.fraction = price.fraction;
    }
    return result;
}
/**
 * Clones time struct.
 *
 * @param time struct to be cloned
 * @return cloned struct
 */
public static TimeStruct cloneTime(TimeStruct time)
{
    TimeStruct result = null;
    if (time != null)
    {
        result = new TimeStruct();
        result.hour = time.hour;
        result.minute = time.minute;
        result.second = time.second;
        result.fraction = time.fraction;
    }
    return result;
}

/**
 * Clones a CboeIdStruct.
 *
 * @param cboeIdStruct struct to be cloned
 * @return cloned cboeIdStruct
 *
 * @author Dan Mannisto
 */
public static CboeIdStruct cloneCboeId( CboeIdStruct cboeIdStruct )
{
   return CboeId.clone( cboeIdStruct );
}

/**
 * Clones an ExchangeAcronymStruct struct.
 *
 * @param ExchangeAcronymStruct struct to clone
 * @return cloned ExchangeAcronymStruct struct
 * @author Alex Brazhnichenko
 */
public static ExchangeAcronymStruct cloneExchangeAcronymStruct(ExchangeAcronymStruct struct)
{
    ExchangeAcronymStruct result = null;
    if (struct != null)
    {
        result = new ExchangeAcronymStruct(struct.exchange, struct.acronym);
    }
    return result;
}

/**
 * Clones a ExchangeFirmStruct struct.
 *
 * @param ExchangeFirmStruct struct to clone
 * @return cloned ExchangeFirmStruct struct
 *
 * @author Emily Huang
 */
public static ExchangeFirmStruct cloneExchangeFirmStruct(ExchangeFirmStruct struct)
{
    ExchangeFirmStruct result = null;
    if (struct != null)
    {
        result = new ExchangeFirmStruct(struct.exchange, struct.firmNumber);
    }
    return result;
}

/**
 * Checks date struct for default values.
 *
 * @param date date to be checked
 * @return <code>true</code> if date has default values
 *
 * @author John Wickberg
 */
public static boolean isDefault(DateStruct date)
{
    // assume that dates with year of 0 are default dates
    return date.year == 0;
}
/**
 * Checks date/time struct for default values.
 *
 * @param dateTime date/time to be checked
 * @return <code>true</code> if date/time has default values
 *
 * @author John Wickberg
 */
public static boolean isDefault(DateTimeStruct dateTime)
{
    // assume that date/times are default if year is default
    return isDefault(dateTime.date);
}
/**
 * Checks price struct for default values.
 *
 * @param price price to be checked
 * @return <code>true</code> if price has default values
 *
 * @author John Wickberg
 */
public static boolean isDefault(PriceStruct price)
{
    return price.type == PriceTypes.NO_PRICE;
}
/**
 * Checks time struct for default values.
 *
 * @param time time to be checked
 * @return <code>true</code> if time has default values
 *
 * @author John Wickberg
 */
public static boolean isDefault(TimeStruct time)
{
    // assume that times an hour of -1 are default times
    return time.hour == -1;
}

/**
 * Checks CboeIdStruct for default values.
 *
 * @param cboeIdStruct CboeIdStruct to be checked
 * @return <code>true</code> if CboeIdStruct has default values
 *
 * @author Dan Mannisto
 */
public static boolean isDefault( CboeIdStruct cboeIdStruct )
{
   return CboeId.isDefault( cboeIdStruct );
}

/**
 * Returns an empty string instead of a null string.  Non-null strings
 * are returned unchanged.
 *
 * @param value a string to be checked
 * @return original value or an empty string
 *
 * @author John Wickberg
 */
public static String nullToEmpty(String value)
{
    return value == null ? "" : value;
}
/**
 * Returns a string representation of the DateStruct.
 *
 * @param date struct to be converted
 * @return a string value for the date
 *
 * @author John Wickberg
 */
public static String toString(DateStruct date)
{
    if (isDefault(date))
    {
        return "default";
    }
    else
    {
        StringBuilder sb = new StringBuilder(10);
        return sb.append(date.month).append('/').append(date.day).append('/').append(date.year).toString();
    }
}
/**
 * Returns a string representation of the DateTimeStruct.
 *
 * @param dateTime struct to be converted
 * @return a string value for the date/time
 *
 * @author John Wickberg
 */
public static String toString(DateTimeStruct dateTime)
{
    if (isDefault(dateTime))
    {
        return "default";
    }
    else
    {
        String d = toString(dateTime.date);
        String t = toString(dateTime.time);
        StringBuilder sb = new StringBuilder(d.length()+t.length()+1);
        return sb.append(d).append(' ').append(t).toString();
    }
}

/**
 * Returns a string representation of the CboeIdStruct.
 *
 * @param cboeIdStruct cboeIdStruct to be converted
 * @return a string value for the CboeIdStruct
 *
 * @author Dan Mannisto
 */
public static String toString( CboeIdStruct cboeIdStruct )
{
   return CboeId.toString( cboeIdStruct );
}

public static boolean isEqual(DateStruct firstDate, DateStruct secondDate)
{
    if (firstDate == null || secondDate == null )
    {
        return false;
    }

    if(firstDate.day == secondDate.day && firstDate.month == secondDate.month)
    {
        return true;
    }
    else
    {
        return false;
    }
}

public static boolean isEqual(TimeStruct firstTime, TimeStruct secondTime)
{
    if (firstTime == null || secondTime == null )
    {
        return false;
    }

    if( firstTime.fraction == secondTime.fraction
        && firstTime.hour == secondTime.hour
        && firstTime.minute == secondTime.minute
        && firstTime.second == secondTime.second )
    {
        return true;
    }
    else
    {
        return false;
    }
}

public static boolean isEqual(DateTimeStruct firstDate, DateTimeStruct secondDate)
{
    if (firstDate == null || secondDate == null )
    {
        return false;
    }

    if(isEqual(firstDate.date, secondDate.date) && isEqual(firstDate.time, secondDate.time))
    {
        return true;
    }
    else
    {
        return false;
    }
}

/**
 * Compares two CboeIdStructs for equality
 *
 * @return <code>true</code> if the CboeIdStructs are equal; <code>false</code>
 *         if they are not equal
 *
 * @author Dan Mannisto
 */
public static boolean isEqual( CboeIdStruct firstCboeIdStruct,
                               CboeIdStruct secondCboeIdStruct )
{
   return CboeId.isEqual( firstCboeIdStruct, secondCboeIdStruct );
}

/**
 * Converts a PriceStruct to a string representation.
 *
 * @param price struct to be converted
 * @return a string value for the time
 *
 * @author John Wickberg
 */
public static String toString(PriceStruct price)
{
    if (isDefault(price))
    {
        return "";
    }
    else
    {
        Price tempPrice = PriceFactory.create(price);
        return tempPrice.toString();
    }
}
/**
 * Converts a TimeStruct to a string representation.
 *
 * @param time struct to be converted
 * @return a string value for the time
 *
 * @author John Wickberg
 */
public static String toString(TimeStruct time)
{
    if (isDefault(time))
    {
        return "default";
    }
    else
    {
        StringBuilder sb = new StringBuilder(12);
        return sb.append(time.hour).append(':').append(time.minute).append(':')
                 .append(time.second).append('.').append(time.fraction).toString();
    }
}

public static boolean isEqual(ExchangeFirmStruct firm1, ExchangeFirmStruct firm2)
{
    boolean result = false;
    if (firm1 != null && firm2 != null)
    {
        if (firm1.exchange != null && firm1.firmNumber != null)
        {
            if (firm1.exchange.equals(firm2.exchange) && firm1.firmNumber.equals(firm2.firmNumber))
            {
                result = true;
            }
        }
    }
    return result;
}

public static String toString(ExchangeFirmStruct firm)
{
    if (firm == null)
    {
        return "Firm struct is null !";
    }
    StringBuilder sb = new StringBuilder(firm.exchange.length()+firm.firmNumber.length()+1);
    return sb.append(firm.exchange).append(':').append(firm.firmNumber).toString();
}

public static String toString(ExchangeAcronymStruct broker)
{
    if (broker == null)
    {
       return "Acronym struct is null !";
    }
    StringBuilder sb = new StringBuilder(broker.exchange.length()+broker.acronym.length()+1);
    return sb.append(broker.exchange).append(':').append(broker.acronym).toString();
}

    public static boolean isEqual(ExchangeAcronymStruct broker1, ExchangeAcronymStruct broker2)
    {
        boolean result = false;
        if (broker1 != null && broker2 != null)
        {
            if (broker1.exchange != null && broker1.acronym != null)
            {
                if (broker1.exchange.equals(broker2.exchange) && broker1.acronym.equals(broker2.acronym))
                {
                    result = true;
                }
            }
        }
        return result;
    }


    /**
     * Creates a default TradeReportSettlementStruct.
     *
     * @return a default TradeReportSettlementStruct, with current time date, and with asOfFlag false
     *
     * @author Mei Wu
     */
    public static TradeReportSettlementStruct buildDefaultTradeReportSettlement()
    {
        TradeReportSettlementStruct result = new TradeReportSettlementStruct();
        result.settlementDate = TimeServiceWrapper.toDateStruct();
        result.transactionTime = TimeServiceWrapper.toDateTimeStruct();
        result.asOfFlag = false;
        return result;
    }

    public static AllocationStrategyStructV2 cloneAllocationStrategyStructV2(AllocationStrategyStructV2 struct)
    {
        AllocationStrategyStructV2 result = null;
        if(struct != null)
        {
            result = new AllocationStrategyStructV2(struct.allocationTradeType, struct.defaultStrategyCode,
                                                    struct.prioritizedStrategyCodes);
        }
        return result;
    }

    public static AuctionBooleanStruct cloneAuctionBooleanStruct(AuctionBooleanStruct struct)
    {
        AuctionBooleanStruct result = null;
        if(struct != null)
        {
            result = new AuctionBooleanStruct(struct.auctionType, struct.value);
        }
        return result;
    }

    public static AuctionLongStruct cloneAuctionLongStruct(AuctionLongStruct struct)
    {
        AuctionLongStruct result = null;
        if(struct != null)
        {
            result = new AuctionLongStruct(struct.auctionType, struct.value);
        }
        return result;
    }

    public static AuctionOrderSizeTicksStruct cloneAuctionOrderSizeTicksStruct(AuctionOrderSizeTicksStruct struct)
    {
        AuctionOrderSizeTicksStruct result = null;
        if(struct != null)
        {
            result = new AuctionOrderSizeTicksStruct(struct.auctionType, struct.orderSize, struct.ticksAboveNBBO);
        }
        return result;
    }

    public static AuctionRangeStruct cloneAuctionRangeStruct(AuctionRangeStruct struct)
    {
        AuctionRangeStruct result = null;
        if(struct != null)
        {
            result = new AuctionRangeStruct(struct.auctionType, struct.lowerLimit, struct.upperLimit);
        }
        return result;
    }

    public static DpmRightsScaleStruct cloneDpmRightsScaleStruct(DpmRightsScaleStruct struct)
    {
        DpmRightsScaleStruct result = null;
        if(struct != null)
        {
            result = new DpmRightsScaleStruct(struct.lowNbrParticipants, struct.highNbrParticipants,
                                              struct.scalePercentage);
        }
        return result;
    }

    public static EPWStruct cloneEPWStruct(EPWStruct struct)
    {
        EPWStruct result = null;
        if(struct != null)
        {
            result = new EPWStruct(struct.minimumBidRange, struct.maximumBidRange, struct.maximumAllowableSpread);
        }
        return result;
    }

    public static InternalizationPercentageStruct cloneInternalizationPercentageStruct(InternalizationPercentageStruct struct)
    {
        InternalizationPercentageStruct result = null;
        if(struct != null)
        {
            result = new InternalizationPercentageStruct(struct.auctionType, struct.lowerRange, struct.upperRange,
                                                         struct.percentage);
        }
        return result;
    }

    public static TimeRangeStruct cloneTimeRangeStruct(TimeRangeStruct struct)
    {
        TimeRangeStruct result = null;
        if(struct != null)
        {
            result = new TimeRangeStruct(struct.upperLimit, struct.lowerLimit);
        }
        return result;
    }

    public static AlarmActivationStruct cloneAlarmActivationStruct(AlarmActivationStruct struct)
    {
        AlarmActivationStruct newStruct = new AlarmActivationStruct();
        newStruct.activationId = struct.activationId;
        newStruct.activeStatus = struct.activeStatus;
        newStruct.lastChanged = cloneDateTime(struct.lastChanged);
        newStruct.notificationReceiver = struct.notificationReceiver;
        newStruct.notificationType = struct.notificationType;
        newStruct.definition = cloneAlarmDefinitionStruct(struct.definition);
        return newStruct;
    }

    public static AlarmDefinitionStruct cloneAlarmDefinitionStruct(AlarmDefinitionStruct struct)
    {
        AlarmDefinitionStruct newStruct = new AlarmDefinitionStruct();
        newStruct.definitionId = struct.definitionId;
        newStruct.name = struct.name;
        newStruct.severity = struct.severity;

        AlarmConditionStruct[] orgCondStructs = struct.conditions;
        newStruct.conditions = new AlarmConditionStruct[orgCondStructs.length];
        for(int i = 0; i < orgCondStructs.length; i++)
        {
            AlarmConditionStruct orgCondStruct = orgCondStructs[i];
            newStruct.conditions[i] = cloneAlarmConditionStruct(orgCondStruct);
        }
        return newStruct;
    }

    public static AlarmConditionStruct cloneAlarmConditionStruct(AlarmConditionStruct struct)
    {
        AlarmConditionStruct newStruct = new AlarmConditionStruct();
        newStruct.conditionId = struct.conditionId;
        newStruct.conditionType = struct.conditionType;
        newStruct.contextName = struct.contextName;
        newStruct.contextType = struct.contextType;
        newStruct.fieldName = struct.fieldName;
        newStruct.fieldType = struct.fieldType;
        newStruct.name = struct.name;
        newStruct.operator = struct.operator;
        newStruct.subjectName = struct.subjectName;
        newStruct.threshold = struct.threshold;
        return newStruct;
    }

    public static InstrumentorInfo cloneInstrumentorInfo(InstrumentorInfo struct)
    {
        InstrumentorInfo newStruct = new InstrumentorInfo();
        newStruct.instrumentorName = struct.instrumentorName;
        newStruct.key = struct.key;
        newStruct.timestamp = struct.timestamp;
        newStruct.userData = struct.userData;
        return newStruct;
    }

    public static QueueInstrumentorStruct cloneQueueInstrumentorStruct(QueueInstrumentorStruct struct)
    {
        QueueInstrumentorStruct newStruct = new QueueInstrumentorStruct();
        newStruct.currentSize = struct.currentSize;
        newStruct.dequeued = struct.dequeued;
        newStruct.enqueued = struct.enqueued;
        newStruct.flushed = struct.flushed;
        newStruct.highWaterMark = struct.highWaterMark;
        newStruct.overlaid = struct.overlaid;
        newStruct.status = struct.status;
        return newStruct;
    }

    public static Command cloneCommand(Command command)
    {
        Command newCommand = new Command();
        newCommand.name = command.name;
        newCommand.description = command.description;

        DataItem[] dataItems = command.args;
        newCommand.args = new DataItem[dataItems.length];
        for(int i = 0; i < dataItems.length; i++)
        {
            newCommand.args[i] = cloneDataItem(dataItems[i]);
        }

        dataItems = command.retValues;
        newCommand.retValues = new DataItem[dataItems.length];
        for(int i = 0; i < dataItems.length; i++)
        {
            newCommand.retValues[i] = cloneDataItem(dataItems[i]);
        }

        return newCommand;
    }

    public static DataItem cloneDataItem(DataItem dataItem)
    {
        DataItem newDataItem = new DataItem();
        newDataItem.description = dataItem.description;
        newDataItem.name = dataItem.name;
        newDataItem.type = dataItem.type;
        newDataItem.value = dataItem.value;
        return newDataItem;
    }
    
    public static LinkageExtensionsStruct buildLinkageExtensionsStruct()
    {
        LinkageExtensionsStruct linkageExtensions = new LinkageExtensionsStruct();
        linkageExtensions.nbbo = MarketDataStructBuilder.buildNBBOStruct(new ProductKeysStruct());
        linkageExtensions.tradeThruPrice = buildPriceStruct();
        linkageExtensions.tradeThruTime = buildDateTimeStruct();
        return linkageExtensions;
    }
}
