package com.cboe.domain.marketDataReportService;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Vector;

import com.cboe.domain.util.CboeId;
import com.cboe.domain.util.PriceSqlType;
import com.cboe.domain.util.SqlScalarTypeInitializer;
import com.cboe.domain.util.TimeServiceWrapper;
import com.cboe.idl.cmiConstants.MarketDataHistoryEntryTypes;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.interfaces.domain.HistoryServiceIdGenerator;
import com.cboe.interfaces.domain.marketDataReportService.MarketDataHistoryForReports;
import com.objectwave.persist.RDBPersistentAdapter;
import com.objectwave.persist.SQLInsert;

/**
 * A persistent implementation of MarketDataReportHistoryEntry.
 * 
 * @author Cognizant Technology Solution.
 */
public class MarketDataHistoryForReportsImpl extends PersistentBObject implements
        MarketDataHistoryForReports
{
    public static final String TABLE_NAME = "market_data_hist_for_reports";
    /**
     * Session name
     */
    public String sessionName;
    /**
     * Key of product.
     */
    public int productKey;
    /**
     * Entry type
     */
    public short entryType;
    /**
     * Entry time in milliseconds.
     */
    public long entryTime;
    /**
     * Last sale price, if entry is last sale.
     */
    public PriceSqlType lastSalePrice;
    /**
     * Last sale volume, if entry is last sale.
     */
    public int lastSaleVolume;
    /**
     * product (market condition) state.
     */
    public short productState;
    /**
     * ticker prefix.
     */
    public String tickerPrefix;
    /**
     * day of week
     */
    public byte dayOfWeek;
    /**
     * 
     */
    public static Vector classDescriptor;
    /**
     * The flag decides to bypass reflection
     */
    public boolean bypassReflection = false;
    private HistoryServiceIdGenerator idGenerator = null;
    /*
     * JavaGrinder Variables
     */
    public static Field _sessionName;
    public static Field _productKey;
    public static Field _entryType;
    public static Field _entryTime;
    public static Field _lastSalePrice;
    public static Field _lastSaleVolume;
    public static Field _productState;
    public static Field _tickerPrefix;
    public static Field _dayOfWeek;
    private Object databaseIdentifier;
    static
    {
        initFieldDescription();
        initDescriptor();
        SqlScalarTypeInitializer.initTypes();
    }

    /**
     * This static block will be regenerated if persistence is regenerated.
     */
    static void initFieldDescription()
    { /* NAME:fieldDefinition: */
        try
        {
            _sessionName = MarketDataHistoryForReportsImpl.class.getDeclaredField("sessionName");
            _productKey = MarketDataHistoryForReportsImpl.class.getDeclaredField("productKey");
            _entryType = MarketDataHistoryForReportsImpl.class.getDeclaredField("entryType");
            _entryTime = MarketDataHistoryForReportsImpl.class.getDeclaredField("entryTime");
            _lastSalePrice = MarketDataHistoryForReportsImpl.class.getDeclaredField("lastSalePrice");
            _lastSaleVolume = MarketDataHistoryForReportsImpl.class.getDeclaredField("lastSaleVolume");
            _productState = MarketDataHistoryForReportsImpl.class.getDeclaredField("productState");
            _tickerPrefix = MarketDataHistoryForReportsImpl.class.getDeclaredField("tickerPrefix");
            _dayOfWeek = MarketDataHistoryForReportsImpl.class.getDeclaredField("dayOfWeek");
            _sessionName.setAccessible(true);
            _productKey.setAccessible(true);
            _entryType.setAccessible(true);
            _entryTime.setAccessible(true);
            _lastSalePrice.setAccessible(true);
            _lastSaleVolume.setAccessible(true);
            _productState.setAccessible(true);
            _tickerPrefix.setAccessible(true);
            _dayOfWeek.setAccessible(true);
        }
        catch (NoSuchFieldException ex)
        {
            Log.exception(ex);
        }
    }

    /**
     * MarketDataHistoryForReportsImpl constructor comment.
     */
    public MarketDataHistoryForReportsImpl()
    {
        super();
    }

    /**
     * Constructor for MarketDataHistoryForReportsImpl.
     * 
     * @param bypassReflection
     * @param idGeneratorStrategy
     */
    public MarketDataHistoryForReportsImpl(boolean bypassReflection,
            HistoryServiceIdGenerator idGeneratorStrategy)
    {
        super();
        this.bypassReflection = bypassReflection;
        this.idGenerator = idGeneratorStrategy;
    }

    /**
     * Crates last sale entry to the database.
     * 
     * @param ticker : TickerStruct
     * @param entryTime
     * 
     */
    public void createLastSaleEntry(TickerStruct ticker, long entryTime)
    {
        setProductKey(ticker.productKeys.productKey);
        setEntryType(MarketDataHistoryEntryTypes.PRICE_REPORT_ENTRY);
        setEntryTime(entryTime);
        setLastSalePrice(new PriceSqlType(ticker.lastSalePrice));
        setLastSaleVolume(ticker.lastSaleVolume);
        setTickerPrefix(ticker.salePrefix);
        setSessionName(ticker.sessionName);
        setDayOfWeek(getTodayDayOfWeek());
    }

    /**
     * Get the session name
     * 
     * @return session name
     */
    public String getSessionName()
    {
        return bypassReflection
                ? sessionName
                : (String) editor.get(_sessionName, sessionName);
    }

    /**
     * Getter for entry time.
     * 
     * @return entry time
     */
    public long getEntryTime()
    {
        return bypassReflection
                ? entryTime
                : (long) editor.get(_entryTime, entryTime);
    }

    /**
     * Getter for entry type.
     * 
     * @return entry type
     */
    public short getEntryType()
    {
        return bypassReflection
                ? entryType
                : (short) editor.get(_entryType, entryType);
    }

    /**
     * Getter for last sale price.
     * 
     * @return last sale price
     */
    public PriceSqlType getLastSalePrice()
    {
        return bypassReflection
                ? lastSalePrice
                : (PriceSqlType) editor.get(_lastSalePrice, lastSalePrice);
    }

    /**
     * Getter for last sale volume.
     * 
     * @return last sale volume
     */
    public int getLastSaleVolume()
    {
        return bypassReflection
                ? lastSaleVolume
                : (int) editor.get(_lastSaleVolume, lastSaleVolume);
    }

    /**
     * Getter for productKey.
     * 
     * @return product key
     */
    public int getProductKey()
    {
        return bypassReflection
                ? productKey
                : (int) editor.get(_productKey, productKey);
    }

    /**
     * Getter for productState.
     * 
     * @return product state
     */
    public short getProductState()
    {
        return bypassReflection
                ? productState
                : (short) editor.get(_productState, productState);
    }

    /**
     * Getter for tickerPrefix
     * 
     * @return ticker prefix
     */
    public String getTickerPrefix()
    {
        String prefixToReturn = bypassReflection
                ? tickerPrefix
                : (String) editor.get(_tickerPrefix, tickerPrefix);
        if (prefixToReturn == null)
        {
            prefixToReturn = "";
        }
        return prefixToReturn;
    }

    /**
     * Getter for dayOfWeek
     * 
     * @return day of week
     */
    public byte getDayOfWeek()
    {
        return bypassReflection
                ? dayOfWeek
                : (byte) editor.get(_dayOfWeek, dayOfWeek);
    }

    /**
     * Gets bypassReflection flag
     * 
     * @return value of bypassReflection flag
     */
    public boolean getBypassReflectionFlag()
    {
        return bypassReflection;
    }

    /**
     * Sets bypassReflection flag
     * 
     * @param val
     */
    public void setBypassReflectionFlag(boolean val)
    {
        bypassReflection = val;
    }

    /**
     * Sets DayOfWeek
     * 
     * @param aValue
     */
    public void setDayOfWeek(byte aValue)
    {
        if (bypassReflection)
        {
            dayOfWeek = aValue;
        }
        else
        {
            editor.set(_dayOfWeek, aValue, dayOfWeek);
        }
    } // setDayOfWeek

    /**
     * Gets the day of week. 1 - Sunday, 2 - Monday and so on.
     */
    public static byte getTodayDayOfWeek()
    {
        Calendar calendar = TimeServiceWrapper.getCalendar();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // for Calendar, 1 - Sunday 2 - Monday...
        return (byte) (currentDayOfWeek);
    }

    /**
     * Describe how this class relates to the relational database.
     */
    public static void initDescriptor()
    {
        synchronized (MarketDataHistoryForReportsImpl.class)
        {
            if (classDescriptor != null)
                return;
            Vector tempDescriptor = getSuperDescriptor();
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("session_name", _sessionName));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prod_key", _productKey));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("entry_type", _entryType));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("entry_time", _entryTime));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("last_sale_price", _lastSalePrice));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("last_sale_vol", _lastSaleVolume));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("product_state", _productState));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("ticker_prefix", _tickerPrefix));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("dayOfWeek", _dayOfWeek));
            classDescriptor = tempDescriptor;
            RDBPersistentAdapter.setStaticClassDescription("com.cboe.domain.marketDataReportService.MarketDataHistoryForReportsImpl", classDescriptor);
        }
    }

    /**
     * Needed to define table name and the description of this class.
     */
    public ObjectChangesIF initializeObjectEditor()
    {
        final DBAdapter result = (DBAdapter) super.initializeObjectEditor();
        result.setTableName(TABLE_NAME);
        return result;
    }

    /**
     * @return unique ID generated using HistoryServiceIdGenerator
     */
    private Object generateDBId()
    {
        return this.idGenerator.getId();
    }

    public SQLInsert getSQL()
    {
        SQLInsert rval = bypassReflection
                ? new SQLMarketDataReportHistoryInsert(this)
                : null;
        if ((rval != null) && (getDatabaseIdentifier() == null))
        {
            // we need to set the database identifier as the jgrinder code
            // doesn't do it since we are bypassing the jgrinder reflective code
            setDatabaseIdentifier(generateDBId());
        }
        return rval;
    }

    /**
     * This method is called from market data history service proxy prior to making a CORBA call.
     * 
     */
    public void prepare()
    {
        if (this.getDatabaseIdentifier() == null)
        {
            setDatabaseIdentifier(generateDBId());
        }
    }

    /**
     * Setter for entry type.
     */
    public void setEntryType(short aValue)
    {
        if (bypassReflection)
        {
            entryType = aValue;
        }
        else
        {
            editor.set(_entryType, aValue, entryType);
        }
    }

    /**
     * Setter for entry time.
     */
    public void setEntryTime(long aValue)
    {
        if (bypassReflection)
        {
            entryTime = aValue;
        }
        else
        {
            editor.set(_entryTime, aValue, entryTime);
        }
        setDayOfWeek(getTodayDayOfWeek());
    }

    /**
     * Setter for last sale price.
     */
    public void setLastSalePrice(PriceSqlType aValue)
    {
        if (bypassReflection)
        {
            lastSalePrice = aValue;
        }
        else
        {
            editor.set(_lastSalePrice, aValue, lastSalePrice);
        }
    }

    /**
     * Setter for last sale volume.
     */
    public void setLastSaleVolume(int aValue)
    {
        if (bypassReflection)
        {
            lastSaleVolume = aValue;
        }
        else
        {
            editor.set(_lastSaleVolume, aValue, lastSaleVolume);
        }
    }

    /**
     * Setter for product key.
     */
    public void setProductKey(int aValue)
    {
        if (bypassReflection)
        {
            productKey = aValue;
        }
        else
        {
            editor.set(_productKey, aValue, productKey);
        }
    }

    /**
     * Setter for productState.
     */
    public void setProductState(short aValue)
    {
        if (bypassReflection)
        {
            productState = aValue;
        }
        else
        {
            editor.set(_productState, aValue, productState);
        }
    }

    /**
     * Setter for tickerPrefix.
     */
    public void setTickerPrefix(String aValue)
    {
        if (bypassReflection)
        {
            tickerPrefix = aValue;
        }
        else
        {
            editor.set(_tickerPrefix, aValue, tickerPrefix);
        }
    }

    /**
     * Set the session name
     */
    public void setSessionName(String aValue)
    {
        if (bypassReflection)
        {
            sessionName = aValue;
        }
        else
        {
            editor.set(_sessionName, aValue, sessionName);
        }
    }

    /**
     * Gets database identifier for history entry.
     * 
     * @return value of databaseIdentifier
     */
    public Object getDatabaseIdentifier()
    {
        return databaseIdentifier;
    }

    /**
     * Sets databaseIdentifier
     * 
     * @param value
     */
    public void setDatabaseIdentifier(Object value)
    {
        this.databaseIdentifier = value;
    }

    /**
     * Gets DB Identifier
     * 
     * @return value of DBId
     */
    public CboeIdStruct getDBId()
    {
        Object databaseId = this.getDatabaseIdentifier();
        CboeIdStruct rval = null;
        if (databaseId instanceof Long)
        {
            rval = CboeId.toStruct(((Long) databaseId).longValue());
        }
        else if (databaseId instanceof Integer)
        {
            rval = CboeId.toStruct(((Integer) databaseId).longValue());
        }
        else if (databaseId != null)
        {
            rval = CboeId.toStruct(Long.parseLong(databaseId.toString()));
        }
        else
        {
            rval = new CboeIdStruct();
        }
        return rval;
    }

    /**
     * Sets DB identifier
     * 
     * @param databaseIdentifier
     */
    private void setDBId(CboeIdStruct databaseIdentifier)
    {
        long value = CboeId.longValue(databaseIdentifier);
        if (value > 0)
        {
            this.setDatabaseIdentifier(new Long(value));
        }
    }
}
