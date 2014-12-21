package com.cboe.domain.marketData;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Vector;

import com.cboe.domain.util.CboeId;
import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.ExchangeVolume;
import com.cboe.domain.util.ExchangeVolumeHolder;
import com.cboe.domain.util.MarketDataStructBuilder;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.PriceSqlType;
import com.cboe.domain.util.SqlScalarTypeInitializer;
import com.cboe.domain.util.StructBuilder;
import com.cboe.domain.util.TimeServiceWrapper;
import com.cboe.idl.cmiConstants.CurrentMarketViewTypes;
import com.cboe.idl.cmiConstants.ExchangeIndicatorTypes;
import com.cboe.idl.cmiConstants.MarketDataHistoryEntryTypes;
import com.cboe.idl.cmiConstants.OverrideIndicatorTypes;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.ProductStates;
import com.cboe.idl.cmiConstants.VolumeTypes;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.ExchangeIndicatorStruct;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryDetailEntryStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryEntryStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.internalBusinessServices.MarketDataHistoryEntriesStruct;
import com.cboe.idl.internalBusinessServices.MarketDataHistoryEntryStructV1;
import com.cboe.idl.internalBusinessServices.MarketDataHistoryExpectedOpeningPriceStruct;
import com.cboe.idl.internalBusinessServices.MarketDataHistoryMarketConditionEntryStruct;
import com.cboe.idl.internalBusinessServices.MarketDataHistoryPriceReportStruct;
import com.cboe.idl.internalBusinessServices.MarketDataHistoryQuoteEntryStruct;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.infrastructureServices.systemsManagementService.ApplicationPropertyHelper;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;
import com.cboe.interfaces.domain.HistoryServiceIdGenerator;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.TradeReport;
import com.cboe.interfaces.domain.marketData.MarketDataHistoryEntry;
import com.cboe.server.util.TradeServerIdPropertyHelper;
import com.objectwave.persist.RDBPersistentAdapter;
import com.objectwave.persist.SQLInsert;

/**
 * A persistentable implementation of MarketDataHistoryEntry. We bypass jgrinder
 * reflective code in case of MDH creation. In such case we need to set the
 * databaseidentifier. The way it works is: 1. The MDH home impl creates objects
 * that are set to bypass reflection (refer to createTransientEntry () method of
 * MDHHomeImpl) 2. The code sets appropriate fields in the MDHEntryImpl. The
 * values are set in the local fields instead in a collection used by jgrinder.
 * 3. On insert, the jgrinder code calls the MDHEntryImpl back for getting the
 * SQL statement (getSQL () method). It then calls method on SQLInsert for
 * binding values. The SQLInsert code callsback MDHEntryImpl to get values of
 * the fields and bind them in the prepared statement. 4. jgrinder code does
 * transaction management. The SQLInsert code is in the class
 * SQLMarketDataHistoryInsert.java.
 * 
 * @author John Wickberg
 * @author Daljinder Singh
 */
public class MarketDataHistoryEntryImpl extends PersistentBObject implements MarketDataHistoryEntry
{
	public static final String TABLE_NAME = "mkt_data_hist";

	private final static Price NO_PRICE = PriceFactory.create(Price.NO_PRICE_STRING);
	
	public static final int MAX_BROKERS = 15; // we will persist at most this

	// many brokers or

	// contra brokers

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
	 * Bid price, if entry is quote.
	 */
	public PriceSqlType bidPrice;

	/**
	 * Bid size, if entry is quote.
	 */
	public int bidSize;

	/**
	 * Ask price, if entry is quote.
	 */
	public PriceSqlType askPrice;

	/**
	 * Ask size, if entry is quote.
	 */
	public int askSize;

	/**
	 * Non-contingent Bid price, if entry is quote.
	 */
	public PriceSqlType bestLimitBidPrice;

	/**
	 * Non-contingent Bid size, if entry is quote.
	 */
	public int bestLimitBidSize;

	/**
	 * Non-contingent Ask price, if entry is quote.
	 */
	public PriceSqlType bestLimitAskPrice;

	/**
	 * Non-contingent Ask size, if entry is quote.
	 */
	public int bestLimitAskSize;

	public PriceSqlType bestPublicBidPrice;

	public int bestPublicBidSize;

	public PriceSqlType bestPublicAskPrice;

	public int bestPublicAskSize;

	/**
	 * Last sale price, if entry is last sale.
	 */
	public PriceSqlType lastSalePrice;

	/**
	 * Last sale volume, if entry is last sale.
	 */
	public int lastSaleVolume;

	/**
	 * Price of underlying when entry was created.
	 */
	public PriceSqlType underlyingLastSalePrice;

	/**
	 * Type of expected opening price.
	 */
	public short eopType;

	/**
	 * Imbalance quantity associated with expected opening price.
	 */
	public int imbalanceQuantity;

	/**
	 * product (market condition) state.
	 */
	public short productState;

	/**
	 * ticker prefix.
	 */
	public String tickerPrefix;

	/**
	 * Session name
	 */
	public String sessionName;

	public byte dayOfWeek;
	
	public byte tradeServerId;

	/**
	 * Override Indicator
	 */
	public char overrideIndicator;

	/**
	 * NBBO Ask Price.
	 */
	public PriceSqlType nbboAskPrice;

	/**
	 * NBBO Bid Price.
	 */
	public PriceSqlType nbboBidPrice;

	/**
	 * NBBO Ask Exchange.
	 */
	public ExchangeVolumeHolder nbboAskExchange;

	/**
	 * NBBO Bid Exchange.
	 */
	public ExchangeVolumeHolder nbboBidExchange;

	// ///////////////////////////////////////////////
	/**
	 * BOTR Ask Price.
	 */
	public PriceSqlType botrAskPrice;

	/**
	 * BOTR Bid Price.
	 */
	public PriceSqlType botrBidPrice;

	/**
	 * BOTR Ask Exchange.
	 */
	public ExchangeVolumeHolder botrAskExchange;

	/**
	 * BOTR Bid Exchange.
	 */
	public ExchangeVolumeHolder botrBidExchange;

	/**
	 * Portion of customer ask in current Market quantity.
	 */
	public int bestPublicCustomerAskSize;

	/**
	 * Portion of customer bid in current Market quantity.
	 */
	public int bestPublicCustomerBidSize;

	// /////////////////////////////////////////////////

	/**
	 * Trade through Indicator.
	 */
	public char tradeThroughIndicator;

	/**
	 * Exchange Indicators.
	 */
	public ExchangeIndicatorHolder exchangeIndicators;

	/**
	 * Broker.
	 */
	public ExchangeAcronymHolder broker;

	/**
	 * Contra.
	 */
	public ExchangeAcronymHolder contra;

	/**
	 * Physical location (IP Address or some thing like that)
	 */
	public String physicalLocation;

	/**
	 * trade Id for Last Sales by HTS
	 */
	public long tradeId;

	public boolean bypassReflection = false;

	private HistoryServiceIdGenerator idGenerator = null;

	/*
	 * JavaGrinder Variables
	 */
	public static Field _productKey;

	public static Field _underlyingLastSalePrice;

	public static Field _lastSaleVolume;

	public static Field _lastSalePrice;

	public static Field _askSize;

	public static Field _askPrice;

	public static Field _bidSize;

	public static Field _bidPrice;

	public static Field _bestLimitAskSize;

	public static Field _bestLimitAskPrice;

	public static Field _bestLimitBidSize;

	public static Field _bestLimitBidPrice;

	public static Field _bestPublicAskSize;

	public static Field _bestPublicAskPrice;

	public static Field _bestPublicBidSize;

	public static Field _bestPublicBidPrice;

	public static Field _entryTime;

	public static Field _entryType;

	public static Field _eopType;

	public static Field _imbalanceQuantity;

	public static Field _productState;

	public static Field _tickerPrefix;

	public static Field _sessionName;

	public static Field _overrideIndicator;

	public static Field _nbboAskPrice;

	public static Field _nbboBidPrice;

	public static Field _nbboAskExchange;

	public static Field _nbboBidExchange;

	public static Field _botrAskPrice;

	public static Field _botrBidPrice;

	public static Field _botrAskExchange;

	public static Field _botrBidExchange;

	public static Field _bestPublicCustomerAskSize;

	public static Field _bestPublicCustomerBidSize;

	public static Field _tradeThroughIndicator;

	public static Field _exchangeIndicators;

	public static Field _broker;

	public static Field _contra;

	public static Field _physicalLocation;

	public static Field _dayOfWeek;
	
	public static Field _tradeServerId;

	public static Field _tradeID;

	public static Vector classDescriptor;

	private Object databaseIdentifier;

	private static ExchangeIndicatorStruct[] nullExchangeIndicatorStruct;

	private static ExchangeAcronymStruct[] nullExchangeAcronymStruct;

	private static ExchangeVolumeStruct[] nullExchangeVolumeStruct;

	private static PriceStruct nullPriceStruct;

	static
	{
		initFieldDescription();
		initDescriptor();
        initTradeServerId();

		nullPriceStruct = new PriceStruct();

		nullPriceStruct.type = 0;

		nullExchangeVolumeStruct = new ExchangeVolumeStruct[0];

		nullExchangeAcronymStruct = new ExchangeAcronymStruct[0];

		nullExchangeIndicatorStruct = new ExchangeIndicatorStruct[0];

        SqlScalarTypeInitializer.initTypes();
	}
	
    private static transient byte serverInstanceNumber;
    static void initTradeServerId()
    {
        try
        {
            String serverInstanceNumberStr = ApplicationPropertyHelper.getProperty("serverInstanceNumber");
            String sessionList = ApplicationPropertyHelper.getProperty("sessionNames");
            String tradeServerIdStr = TradeServerIdPropertyHelper.getTradeServerId(serverInstanceNumberStr, sessionList);
            serverInstanceNumber = (byte) Integer.parseInt(tradeServerIdStr);
            Log.information("MarketDataHistoryEntryImpl: trade server id set to " + serverInstanceNumber);
        }
        catch (NoSuchPropertyException e)
        {
            Log.information("MarketDataHistoryEntryImpl:  Unable to determine trade server id value from defined properties. Using 1 as default value");
            serverInstanceNumber = 1;
        }
        catch (NumberFormatException nfe)
        {
            Log.alarm("MarketDataHistoryEntryImpl: Unable to determine trade server id value. Invalid value defined for serverInstanceNumber. Using 1 as default value");
            serverInstanceNumber = 1;
        }
    }

	/**
	 * This static block will be regenerated if persistence is regenerated.
	 */
	static void initFieldDescription()
	{ /* NAME:fieldDefinition: */
		try
		{
			_productKey = MarketDataHistoryEntryImpl.class.getDeclaredField("productKey");
			_entryType = MarketDataHistoryEntryImpl.class.getDeclaredField("entryType");
			_entryTime = MarketDataHistoryEntryImpl.class.getDeclaredField("entryTime");
			_bidPrice = MarketDataHistoryEntryImpl.class.getDeclaredField("bidPrice");
			_bidSize = MarketDataHistoryEntryImpl.class.getDeclaredField("bidSize");
			_askPrice = MarketDataHistoryEntryImpl.class.getDeclaredField("askPrice");
			_askSize = MarketDataHistoryEntryImpl.class.getDeclaredField("askSize");
			_bestLimitBidPrice = MarketDataHistoryEntryImpl.class.getDeclaredField("bestLimitBidPrice");
			_bestLimitBidSize = MarketDataHistoryEntryImpl.class.getDeclaredField("bestLimitBidSize");
			_bestLimitAskPrice = MarketDataHistoryEntryImpl.class.getDeclaredField("bestLimitAskPrice");
			_bestLimitAskSize = MarketDataHistoryEntryImpl.class.getDeclaredField("bestLimitAskSize");
			_bestPublicBidPrice = MarketDataHistoryEntryImpl.class.getDeclaredField("bestPublicBidPrice");
			_bestPublicBidSize = MarketDataHistoryEntryImpl.class.getDeclaredField("bestPublicBidSize");
			_bestPublicAskPrice = MarketDataHistoryEntryImpl.class.getDeclaredField("bestPublicAskPrice");
			_bestPublicAskSize = MarketDataHistoryEntryImpl.class.getDeclaredField("bestPublicAskSize");
			_lastSalePrice = MarketDataHistoryEntryImpl.class.getDeclaredField("lastSalePrice");
			_lastSaleVolume = MarketDataHistoryEntryImpl.class.getDeclaredField("lastSaleVolume");
			_underlyingLastSalePrice = MarketDataHistoryEntryImpl.class.getDeclaredField("underlyingLastSalePrice");
			_eopType = MarketDataHistoryEntryImpl.class.getDeclaredField("eopType");
			_imbalanceQuantity = MarketDataHistoryEntryImpl.class.getDeclaredField("imbalanceQuantity");
			_productState = MarketDataHistoryEntryImpl.class.getDeclaredField("productState");
			_tickerPrefix = MarketDataHistoryEntryImpl.class.getDeclaredField("tickerPrefix");
			_sessionName = MarketDataHistoryEntryImpl.class.getDeclaredField("sessionName");
			_overrideIndicator = MarketDataHistoryEntryImpl.class.getDeclaredField("overrideIndicator");

			_nbboAskPrice = MarketDataHistoryEntryImpl.class.getDeclaredField("nbboAskPrice");
			_nbboBidPrice = MarketDataHistoryEntryImpl.class.getDeclaredField("nbboBidPrice");
			_nbboAskExchange = MarketDataHistoryEntryImpl.class.getDeclaredField("nbboAskExchange");
			_nbboBidExchange = MarketDataHistoryEntryImpl.class.getDeclaredField("nbboBidExchange");

			_botrAskPrice = MarketDataHistoryEntryImpl.class.getDeclaredField("botrAskPrice");
			_botrBidPrice = MarketDataHistoryEntryImpl.class.getDeclaredField("botrBidPrice");
			_botrAskExchange = MarketDataHistoryEntryImpl.class.getDeclaredField("botrAskExchange");
			_botrBidExchange = MarketDataHistoryEntryImpl.class.getDeclaredField("botrBidExchange");

			_bestPublicCustomerAskSize = MarketDataHistoryEntryImpl.class.getDeclaredField("bestPublicCustomerAskSize");
			_bestPublicCustomerBidSize = MarketDataHistoryEntryImpl.class.getDeclaredField("bestPublicCustomerBidSize");

			_tradeThroughIndicator = MarketDataHistoryEntryImpl.class.getDeclaredField("tradeThroughIndicator");
			_exchangeIndicators = MarketDataHistoryEntryImpl.class.getDeclaredField("exchangeIndicators");
			_broker = MarketDataHistoryEntryImpl.class.getDeclaredField("broker");
			_contra = MarketDataHistoryEntryImpl.class.getDeclaredField("contra");
			_physicalLocation = MarketDataHistoryEntryImpl.class.getDeclaredField("physicalLocation");
			_tradeID = MarketDataHistoryEntryImpl.class.getDeclaredField("tradeId");
			_dayOfWeek = MarketDataHistoryEntryImpl.class.getDeclaredField("dayOfWeek");
			_tradeServerId = MarketDataHistoryEntryImpl.class.getDeclaredField("tradeServerId");

			_productKey.setAccessible(true);
			_underlyingLastSalePrice.setAccessible(true);
			_lastSaleVolume.setAccessible(true);
			_lastSalePrice.setAccessible(true);
			_askSize.setAccessible(true);
			_askPrice.setAccessible(true);
			_bidSize.setAccessible(true);
			_bidPrice.setAccessible(true);
			_bestLimitAskSize.setAccessible(true);
			_bestLimitAskPrice.setAccessible(true);
			_bestLimitBidSize.setAccessible(true);
			_bestLimitBidPrice.setAccessible(true);
			_bestPublicAskSize.setAccessible(true);
			_bestPublicAskPrice.setAccessible(true);
			_bestPublicBidSize.setAccessible(true);
			_bestPublicBidPrice.setAccessible(true);
			_entryTime.setAccessible(true);
			_entryType.setAccessible(true);
			_eopType.setAccessible(true);
			_imbalanceQuantity.setAccessible(true);
			_productState.setAccessible(true);
			_tickerPrefix.setAccessible(true);
			_sessionName.setAccessible(true);
			_overrideIndicator.setAccessible(true);

			_nbboAskPrice.setAccessible(true);
			_nbboBidPrice.setAccessible(true);
			_nbboAskExchange.setAccessible(true);
			_nbboBidExchange.setAccessible(true);

			_botrAskPrice.setAccessible(true);
			_botrBidPrice.setAccessible(true);
			_botrAskExchange.setAccessible(true);
			_botrBidExchange.setAccessible(true);

			_bestPublicCustomerAskSize.setAccessible(true);
			_bestPublicCustomerBidSize.setAccessible(true);

			_tradeThroughIndicator.setAccessible(true);
			_exchangeIndicators.setAccessible(true);
			_broker.setAccessible(true);
			_contra.setAccessible(true);
			_physicalLocation.setAccessible(true);
			_dayOfWeek.setAccessible(true);
			_tradeServerId.setAccessible(true);
			_tradeID.setAccessible(true);

		}
		catch (NoSuchFieldException ex)
		{
			System.out.println(ex);
		}
	}

	/**
	 * MarketDataHistoryEntryImpl constructor comment.
	 */
	public MarketDataHistoryEntryImpl()
	{
		super();
	}

	public MarketDataHistoryEntryImpl(boolean bypassReflection, HistoryServiceIdGenerator idGeneratorStrategy)
	{
		super();

		this.bypassReflection = bypassReflection;
		this.idGenerator = idGeneratorStrategy;
	}

	public void init()
	{
		setRetrievedFromDatabase(false);
		setAskPrice(null);
		setAskSize(0);
		setBestPublicCustomerAskSize(0);
		setBestPublicAskPrice(null);
		setBestPublicAskSize(0);
		setBestPublicCustomerBidSize(0);
		setBestPublicBidPrice(null);
		setBestPublicBidSize(0);
		setBidPrice(null);
		setBidSize(0);
		setBotrAskExchange(null);
		setBotrAskPrice(null);
		setBotrBidExchange(null);
		setBotrBidPrice(null);
		setBrokerHolder(null);
		setContraHolder(null);
		setDayOfWeek((byte) 0);
		setTradeServerId(serverInstanceNumber);
		setEntryTime(0);
		setEntryType((short) 0);
		setEopType((short) 0);
		setExchangeIndicators((ExchangeIndicatorHolder)null);
		setImbalanceQuantity(0);
		setLastSalePrice(null);
		setLastSaleVolume(0);
		setNbboAskExchange(null);
		setNbboAskPrice(null);
		setNbboBidExchange(null);
		setNbboBidPrice(null);
		setBestLimitAskPrice(null);
		setBestLimitAskSize(0);
		setBestLimitBidPrice(null);
		setBestLimitBidSize(0);
		setOverrideIndicator((char) 0);
		setPhysicalLocation(null);
		setProductKey(0);
		setProductState((short) 0);
		setSessionName(null);
		setTickerPrefix(null);
		setTradeID(null);
		setTradeThroughIndicatorAsChar((char) 0);
		setUnderlyingLastSalePrice(null);
		setDatabaseIdentifier(null);
	};

	/**
	 * @param c
	 */
	private void setTradeThroughIndicatorAsChar(char c)
	{
		this.tradeThroughIndicator = c;

	}

	/**
	 * @param object
	 */
	public void setNbboBidPrice(PriceSqlType object)
	{
		this.nbboBidPrice = object;
	}

	/**
	 * @param i
	 */
	private void setNbboBidExchange(ExchangeVolumeHolder object)
	{
		this.nbboBidExchange = object;

	}

	/**
	 * @param object
	 */
	public void setNbboAskPrice(PriceSqlType object)
	{
		this.nbboAskPrice = object;

	}

	/**
	 * @param i
	 */
	private void setNbboAskExchange(ExchangeVolumeHolder object)
	{
		this.nbboAskExchange = object;

	}

	/**
	 * @param object
	 */
	private void setExchangeIndicators(ExchangeIndicatorHolder object)
	{
		this.exchangeIndicators = object;

	}

	/**
	 * @param i
	 */
	private void setContraHolder(ExchangeAcronymHolder i)
	{
		this.contra = i;

	}

	/**
	 * @param i
	 */
	private void setBrokerHolder(ExchangeAcronymHolder i)
	{
		this.broker = i;

	}

	/**
	 * @param object
	 */
	public void setBotrBidPrice(PriceSqlType object)
	{
		this.botrBidPrice = object;
	}

	/**
	 * @param i
	 */
	private void setBotrBidExchange(ExchangeVolumeHolder i)
	{
		this.botrBidExchange = i;
	}

	/**
	 * @param object
	 */
	public void setBotrAskPrice(PriceSqlType object)
	{
		this.botrAskPrice = object;
	}

	/**
	 * @param object
	 */
	private void setBotrAskExchange(ExchangeVolumeHolder object)
	{
		this.botrAskExchange = object;

	}

	/**
	 * Returns the sum of the quantities of the volumes in the
	 * MarketVolumeStruct array which are of the type LIMIT or IOC. NOTE:
	 * DTEqa00595 fixes this so that the quantity is added to the sum ONLY IF
	 * the volume type is LIMIT or IOC. *
	 * 
	 * @author John Wickberg
	 * @author Eric Fredericks - Made modifications for DTEqa00595
	 */
	public int calcSize(MarketVolumeStruct[] volumes, short marketViewType)
	{
		int total = 0;
		if (marketViewType == CurrentMarketViewTypes.BEST_LIMIT_PRICE)
		{
			for (int i = 0; i < volumes.length; i++)
			{
				if ((volumes[i].volumeType == VolumeTypes.LIMIT) || volumes[i].volumeType == VolumeTypes.IOC)
				{
					total += volumes[i].quantity;
				}
			}
		}
		else
		{
			for (int i = 0; i < volumes.length; i++)
			{
				total += volumes[i].quantity;
			}
		}
		return total;
	}

	/**
	 * Creates a market best history entry.
	 * 
	 * @author John Wickberg
	 */
	public void createCurrentMarketEntry(CurrentMarketStruct bestMarket, CurrentMarketStruct bestLimitMarket,
			CurrentMarketStruct bestPublicMarket, NBBOStruct nbboStruct, NBBOStruct botrStruct,
			ExchangeIndicatorStruct[] exchangeIndicatorStruct, Price underlyingPrice, short productState, long entryTime, String location)
	{
		setSessionName(bestMarket.sessionName);
		setProductKey(bestMarket.productKeys.productKey);
		setEntryType(MarketDataHistoryEntryTypes.QUOTE_ENTRY);
		setEntryTime(entryTime);
		setValuesForBestMarket(bestMarket);
		setValuesForBestLimitMarket(bestLimitMarket);
		setValuesForBestPublicMarket(bestPublicMarket);
		setUnderlyingLastSalePrice((PriceSqlType) underlyingPrice);
		setProductState(productState);

		if (nbboStruct != null)
		{
			setNBBOAskPrice(new PriceSqlType(nbboStruct.askPrice));
			setNBBOBidPrice(new PriceSqlType(nbboStruct.bidPrice));
			setNBBOAskExchange(nbboStruct.askExchangeVolume);
			setNBBOBidExchange(nbboStruct.bidExchangeVolume);
			setExchangeIndicators(exchangeIndicatorStruct, productState);
		}

		if (botrStruct != null)
		{
			setBOTRAskPrice(new PriceSqlType(botrStruct.askPrice));
			setBOTRBidPrice(new PriceSqlType(botrStruct.bidPrice));
			setBOTRAskExchange(botrStruct.askExchangeVolume);
			setBOTRBidExchange(botrStruct.bidExchangeVolume);
		}
		setValuesForBestCustomerPublicMarket(bestPublicMarket);
		setPhysicalLocation(location);
	}

	/**
	 * Creates an expected opening price history entry.
	 * 
	 * @param expectedOpenPrice
	 *            com.cboe.idl.cmiUtil.PriceStruct
	 * @param underlyingPrice
	 *            com.cboe.util.Price
	 * @author Magic Magee
	 */
	public void createExpectedOpenPriceEntry(ExpectedOpeningPriceStruct expectedOpenPrice, Price underlyingPrice,
			short productState, long entryTime)
	{
		setProductKey(expectedOpenPrice.productKeys.productKey);
		setEntryType(MarketDataHistoryEntryTypes.EXPECTED_OPEN_PRICE);
		setEntryTime(entryTime);
        if (expectedOpenPrice.expectedOpeningPrice.type == PriceTypes.NO_PRICE)
        {
           setLastSalePrice(new PriceSqlType(0.0));
        }
        else
        {
		   setLastSalePrice(new PriceSqlType(expectedOpenPrice.expectedOpeningPrice));
        }
		setUnderlyingLastSalePrice((PriceSqlType) underlyingPrice);
		setEopType(expectedOpenPrice.eopType);
		setImbalanceQuantity(expectedOpenPrice.imbalanceQuantity);
		setProductState(productState);
		setSessionName(expectedOpenPrice.sessionName);
	}

	public void createLastSaleEntry(TimeStruct saleTime, InternalTickerDetailStruct tickerDetail, Price underlyingPrice,
			short productState, long entryTime)
	{
		setProductKey(tickerDetail.lastSaleTicker.ticker.productKeys.productKey);
		setEntryType(MarketDataHistoryEntryTypes.PRICE_REPORT_ENTRY);
		setEntryTime(entryTime);
		setLastSalePrice(new PriceSqlType(tickerDetail.lastSaleTicker.ticker.lastSalePrice));
		setLastSaleVolume(tickerDetail.lastSaleTicker.ticker.lastSaleVolume);
		setUnderlyingLastSalePrice((PriceSqlType) underlyingPrice);
		setProductState(productState);
		setTickerPrefix(tickerDetail.lastSaleTicker.ticker.salePrefix);
		setSessionName(tickerDetail.lastSaleTicker.ticker.sessionName);

		// For linkage order traded in CBOEDIRECT then we save L in the Market
		// Data History.
		if (tickerDetail.lastSaleTicker.ticker.salePostfix.equals(TradeReport.LINKAGE_POSTFIX))
		{
			setOverrideIndicator(OverrideIndicatorTypes.LINKAGE);
		}
		else
		{
			setOverrideIndicator(tickerDetail.detailData.overrideIndicator);
		}

		if (tickerDetail.generatingId != null)
		{
			setPhysicalLocation(tickerDetail.generatingId);
		}

		if (tickerDetail.detailData.brokers != null)
		{
			setBroker(tickerDetail.detailData.brokers);
		}

		if (tickerDetail.detailData.contras != null)
		{
			setContra(tickerDetail.detailData.contras);
		}

		if (tickerDetail.tradeId != null)
		{
			setTradeID(tickerDetail.tradeId);
		}

		if (tickerDetail.detailData.exchangeIndicators != null)
		{
			setExchangeIndicators(tickerDetail.detailData.exchangeIndicators, productState);
		}

		if (tickerDetail.detailData.tradeThroughIndicator == true)
		{
			setTradeThroughIndicator('Y');
		}
		else
		{
			setTradeThroughIndicator('N');
		}

		if (tickerDetail.detailData.nbboBidPrice != null)
		{
			setNBBOBidPrice(new PriceSqlType(tickerDetail.detailData.nbboBidPrice));
		}
		if (tickerDetail.detailData.nbboAskPrice != null)
		{
			setNBBOAskPrice(new PriceSqlType(tickerDetail.detailData.nbboAskPrice));
		}
		if (tickerDetail.detailData.nbboBidExchanges != null)
		{
			setNBBOBidExchange(tickerDetail.detailData.nbboBidExchanges);
		}
		if (tickerDetail.detailData.nbboAskExchanges != null)
		{
			setNBBOAskExchange(tickerDetail.detailData.nbboAskExchanges);
		}

		if (tickerDetail.botrStruct != null)
		{
			setBOTRBidPrice(new PriceSqlType(tickerDetail.botrStruct.bidPrice));
			setBOTRAskPrice(new PriceSqlType(tickerDetail.botrStruct.askPrice));
			setBOTRBidExchange(tickerDetail.botrStruct.bidExchangeVolume);
			setBOTRAskExchange(tickerDetail.botrStruct.askExchangeVolume);
		}

		// NOTE: we store best published "volumes" in BEST_PUB_*CUST_SIZE fileds
		// and prices in BEST_PUB_*_PRICE fields.
		if (tickerDetail.detailData.bestPublishedBidPrice != null)
		{
			setBestPublicBidPrice(new PriceSqlType(tickerDetail.detailData.bestPublishedBidPrice));
		}

		setBestPublicCustomerBidSize(tickerDetail.detailData.bestPublishedBidVolume);

		if (tickerDetail.detailData.bestPublishedAskPrice != null)
		{
			setBestPublicAskPrice(new PriceSqlType(tickerDetail.detailData.bestPublishedAskPrice));
		}

		setBestPublicCustomerAskSize(tickerDetail.detailData.bestPublishedAskVolume);

	}

	/**
	 * Creates an history entry from a product State change.
	 * 
	 * @param productKey
	 *            product changing state
	 * @param Price
	 *            an underlying last sale price
	 * @param productState
	 *            new state of product
	 * @author Magic Magee
	 */
	public void createProductStateChangeEntry(String sessionName, int productKey, Price underlyingPrice, short productState)
	{
		setSessionName(sessionName);
		setProductKey(productKey);
		setUnderlyingLastSalePrice((PriceSqlType) underlyingPrice);
		setEntryType(MarketDataHistoryEntryTypes.MARKET_CONDITION_ENTRY);
		setEntryTime(FoundationFramework.getInstance().getTimeService().getCurrentDateTime());
		setProductState(productState);
	}

	/**
	 * Getter for ask price.
	 */
	public PriceSqlType getAskPrice()
	{
		return bypassReflection ? askPrice : (PriceSqlType) editor.get(_askPrice, askPrice);
	}

	/**
	 * Getter for ask size.
	 */
	public int getAskSize()
	{
		return bypassReflection ? askSize : (int) editor.get(_askSize, askSize);
	}

	/**
	 * Getter for bid price.
	 */
	public PriceSqlType getBidPrice()
	{
		return bypassReflection ? bidPrice : (PriceSqlType) editor.get(_bidPrice, bidPrice);
	}

	/**
	 * Getter for bid size.
	 */
	public int getBidSize()
	{
		return bypassReflection ? bidSize : (int) editor.get(_bidSize, bidSize);
	}

	/**
	 * Getter for entry time.
	 */
	public long getEntryTime()
	{
		return bypassReflection ? entryTime : (long) editor.get(_entryTime, entryTime);
	}

	/**
	 * Getter for entry type.
	 */
	public short getEntryType()
	{
		return bypassReflection ? entryType : (short) editor.get(_entryType, entryType);
	}

	/**
	 * Getter for eop type.
	 */
	public short getEopType()
	{
		return bypassReflection ? eopType : (short) editor.get(_eopType, eopType);
	}

	/**
	 * Getter for imbalance quantity.
	 */
	public int getImbalanceQuantity()
	{
		return bypassReflection ? imbalanceQuantity : (int) editor.get(_imbalanceQuantity, imbalanceQuantity);
	}

	/**
	 * Getter for last sale price.
	 */
	public Price getLastSalePrice()
	{
		return bypassReflection ? lastSalePrice : (PriceSqlType) editor.get(_lastSalePrice, lastSalePrice);	
	}

	/**
	 * Getter for last sale volume.
	 */
	public int getLastSaleVolume()
	{
		return bypassReflection ? lastSaleVolume : (int) editor.get(_lastSaleVolume, lastSaleVolume);
	}

	/**
	 * Getter for non-contingent ask price.
	 */
	public PriceSqlType getBestLimitAskPrice()
	{
		return bypassReflection ? bestLimitAskPrice : (PriceSqlType) editor.get(_bestLimitAskPrice, bestLimitAskPrice);
	}

	/**
	 * Getter for non-contingent ask size.
	 */
	public int getBestLimitAskSize()
	{
		return bypassReflection ? bestLimitAskSize : (int) editor.get(_bestLimitAskSize, bestLimitAskSize);
	}

	/**
	 * Getter for non-contingent bid price.
	 */
	public PriceSqlType getBestLimitBidPrice()
	{
		return bypassReflection ? bestLimitBidPrice : (PriceSqlType) editor.get(_bestLimitBidPrice, bestLimitBidPrice);
	}

	/**
	 * Getter for non-contingent bid size.
	 */
	public int getBestLimitBidSize()
	{
		return bypassReflection ? bestLimitBidSize : (int) editor.get(_bestLimitBidSize, bestLimitBidSize);
	}

	/**
	 * Getter for productKey.
	 */
	public int getProductKey()
	{
		return bypassReflection ? productKey : (int) editor.get(_productKey, productKey);
	}

	/**
	 * Getter for productState.
	 */
	public short getProductState()
	{
		return bypassReflection ? productState : (short) editor.get(_productState, productState);
	}

	/**
	 * Getter for tickerPrefix
	 */
	public String getTickerPrefix()
	{
		String prefixToReturn = bypassReflection ? tickerPrefix : (String) editor.get(_tickerPrefix, tickerPrefix);
		if (prefixToReturn == null)
		{
			prefixToReturn = "";
		}
		return prefixToReturn;
	}

	/**
	 * Get the session name
	 */
	public String getSessionName()
	{
		return bypassReflection ? sessionName : (String) editor.get(_sessionName, sessionName);
	}

	public byte getDayOfWeek()
	{
		return bypassReflection ? dayOfWeek : (byte) editor.get(_dayOfWeek, dayOfWeek);
	} // getDayOfWeek

	public byte getTradeServerId()
	{
	    return bypassReflection ? tradeServerId : (byte) editor.get(_tradeServerId, tradeServerId);
	}
	
	public boolean getBypassReflectionFlag()
	{
		return bypassReflection;
	}

	public void setBypassReflectionFlag(boolean val)
	{
		bypassReflection = val;
	}

	/**
	 * Getter for tradeID
	 */
	public CboeIdStruct getTradeId()
	{
		return CboeId.toStruct(bypassReflection ? tradeId : editor.get(_tradeID, tradeId));
	}

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

	private void setDBId(CboeIdStruct databaseIdentifier)
	{
		long value = CboeId.longValue(databaseIdentifier);

		if (value > 0)
		{
			this.setDatabaseIdentifier(new Long(value));
		}
	}

	// getTradeID

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

	public void setTradeServerId(byte aValue)
	{
	    if (bypassReflection)
	    {
	        tradeServerId = aValue;
	    }
	    else
	    {
	        editor.set(_tradeServerId, aValue, tradeServerId);
	    }
	} // setTradeServerId

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
	 * Getter for underlying price.
	 */
	public PriceSqlType getUnderlyingLastSalePrice()
	{
		return bypassReflection ? underlyingLastSalePrice : (PriceSqlType) editor.get(_underlyingLastSalePrice,
				underlyingLastSalePrice);
	}

	/**
	 * Getter for Override Indicator.
	 */
	public char getOverrideIndicator()
	{
		return bypassReflection ? overrideIndicator : editor.get(_overrideIndicator, overrideIndicator);
	}

	/**
	 * Getter for nbbo ask price.
	 */
	public PriceSqlType getNBBOAskPrice()
	{
		return bypassReflection ? nbboAskPrice : (PriceSqlType) editor.get(_nbboAskPrice, nbboAskPrice);
	}

	/**
	 * Getter for nbbo bid price.
	 */
	public PriceSqlType getNBBOBidPrice()
	{
		return bypassReflection ? nbboBidPrice : (PriceSqlType) editor.get(_nbboBidPrice, nbboBidPrice);
	}

	/**
	 * Getter for nbbo ask exchange.
	 */

	public ExchangeVolumeStruct[] getNBBOAskExchange()
	{
		ExchangeVolumeHolder volumeAskHolder = bypassReflection ? nbboAskExchange : (ExchangeVolumeHolder) editor.get(
				_nbboAskExchange, nbboAskExchange);
		if (volumeAskHolder != null)
		{
			ExchangeVolume[] exchangeVolumes = volumeAskHolder.getExchangeVolumes();
			int size = exchangeVolumes.length;
			ExchangeVolumeStruct[] volumeStruct = new ExchangeVolumeStruct[size];
			for (int i = 0; i < size; i++)
			{
				volumeStruct[i] = exchangeVolumes[i].toStruct();
			}

			return volumeStruct;
		}
		else
			return null;
	}

	/**
	 * Getter for nbbo bid exchange.
	 */
	public ExchangeVolumeStruct[] getNBBOBidExchange()
	{
		ExchangeVolumeHolder volumeBidHolder = bypassReflection ? nbboBidExchange : (ExchangeVolumeHolder) editor.get(
				_nbboBidExchange, nbboBidExchange);
		if (volumeBidHolder != null)
		{
			ExchangeVolume[] exchangeVolumes = volumeBidHolder.getExchangeVolumes();
			int size = exchangeVolumes.length;
			ExchangeVolumeStruct[] volumeStruct = new ExchangeVolumeStruct[size];
			for (int i = 0; i < size; i++)
			{
				volumeStruct[i] = exchangeVolumes[i].toStruct();
			}
			return volumeStruct;
		}
		else
			return null;
	}

	/**
	 * Getter for trade through indicator.
	 */
	public boolean getTradeThroughIndicator()
	{
		char ttIndicator = bypassReflection ? tradeThroughIndicator : editor.get(_tradeThroughIndicator, tradeThroughIndicator);
		if ((ttIndicator == 'Y') || (ttIndicator == 'y'))
			return true;
		else
			return false;
	}

	/**
	 * Getter for exchange indicator.
	 */
	public ExchangeIndicatorStruct[] getExchangeIndicator()
	{
		ExchangeIndicatorHolder exchangeIndicatorHolder = bypassReflection ? exchangeIndicators : (ExchangeIndicatorHolder) editor
				.get(_exchangeIndicators, exchangeIndicators);
		if (exchangeIndicatorHolder != null)
		{
			ExchangeIndicator[] exchangeIndicators = exchangeIndicatorHolder.getExchangeIndicators();
			int size = exchangeIndicators.length;
			ExchangeIndicatorStruct[] indicatorStruct = new ExchangeIndicatorStruct[size];
			for (int i = 0; i < size; i++)
			{
				indicatorStruct[i] = exchangeIndicators[i].toStruct();
			}
			return indicatorStruct;
		}
		else
			return null;
	}

	/**
	 * Getter for nbbo bid price.
	 */
	public PriceSqlType getBestPublishedAskPrice()
	{
		return bypassReflection ? nbboAskPrice : (PriceSqlType) editor.get(_nbboAskPrice, nbboAskPrice);
	}

	public PriceSqlType getBestPublishedBidPrice()
	{
		return bypassReflection ? nbboBidPrice : (PriceSqlType) editor.get(_nbboBidPrice, nbboBidPrice);
	}

	/**
	 * Getter for broker.
	 */
	public ExchangeAcronymStruct[] getBroker()
	{
		ExchangeAcronymHolder exchangeBrokerAcronymHolder;
		exchangeBrokerAcronymHolder = bypassReflection ? broker : (ExchangeAcronymHolder) editor.get(_broker, broker);
		if (exchangeBrokerAcronymHolder != null)
		{
			ExchangeAcronym[] brokerExchangeAcronyms = exchangeBrokerAcronymHolder.getExchangeAcronyms();
			int size = brokerExchangeAcronyms.length;
			ExchangeAcronymStruct[] brokerAcronymStruct = new ExchangeAcronymStruct[size];
			for (int i = 0; i < size; i++)
			{
				brokerAcronymStruct[i] = brokerExchangeAcronyms[i].toStruct();
			}
			return brokerAcronymStruct;
		}
		else
			return null;
	}

	/**
	 * Getter for contra.
	 */
	public ExchangeAcronymStruct[] getContra()
	{
		ExchangeAcronymHolder exchangeContraAcronymHolder = bypassReflection ? contra : (ExchangeAcronymHolder) editor.get(_contra,
				contra);
		if (exchangeContraAcronymHolder != null)
		{
			ExchangeAcronym[] contraExchangeAcronyms = exchangeContraAcronymHolder.getExchangeAcronyms();
			int size = contraExchangeAcronyms.length;
			ExchangeAcronymStruct[] contraAcronymStruct = new ExchangeAcronymStruct[size];
			for (int i = 0; i < size; i++)
			{
				contraAcronymStruct[i] = contraExchangeAcronyms[i].toStruct();
			}
			return contraAcronymStruct;
		}
		else
			return null;
	}

	/**
	 * Getter for PhysicalLocation.
	 */
	public String getPhysicalLocation()
	{
		return bypassReflection ? physicalLocation : (String) editor.get(_physicalLocation, physicalLocation);
	}

	/**
	 * Describe how this class relates to the relational database.
	 */
	public static void initDescriptor()
	{
		synchronized (MarketDataHistoryEntryImpl.class)
		{
			if (classDescriptor != null)
				return;
			Vector tempDescriptor = getSuperDescriptor();
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prod_key", _productKey));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("entry_type", _entryType));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("entry_time", _entryTime));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("bid_price", _bidPrice));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("bid_size", _bidSize));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("ask_price", _askPrice));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("ask_size", _askSize));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("non_cont_bid_price", _bestLimitBidPrice));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("non_cont_bid_size", _bestLimitBidSize));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("non_cont_ask_price", _bestLimitAskPrice));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("non_cont_ask_size", _bestLimitAskSize));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("best_pub_bid_price", _bestPublicBidPrice));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("best_pub_bid_size", _bestPublicBidSize));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("best_pub_ask_price", _bestPublicAskPrice));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("best_pub_ask_size", _bestPublicAskSize));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("last_sale_price", _lastSalePrice));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("last_sale_vol", _lastSaleVolume));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("undly_last_sale_price", _underlyingLastSalePrice));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("eop_type", _eopType));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("imbalance_qty", _imbalanceQuantity));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("product_state", _productState));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("ticker_prefix", _tickerPrefix));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("session_name", _sessionName));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("override_indicator", _overrideIndicator));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("nbbo_ask_price", _nbboAskPrice));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("nbbo_bid_price", _nbboBidPrice));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("nbbo_ask_exchanges", _nbboAskExchange));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("nbbo_bid_exchanges", _nbboBidExchange));

			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("botr_ask_price", _botrAskPrice));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("botr_bid_price", _botrBidPrice));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("botr_ask_exchanges", _botrAskExchange));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("botr_bid_exchanges", _botrBidExchange));

			tempDescriptor.addElement(AttributeDefinition
					.getAttributeRelation("best_pub_ask_cust_size", _bestPublicCustomerAskSize));
			tempDescriptor.addElement(AttributeDefinition
					.getAttributeRelation("best_pub_bid_cust_size", _bestPublicCustomerBidSize));

			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("trade_through_indicator", _tradeThroughIndicator));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("exchanges_indicators", _exchangeIndicators));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("broker", _broker));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("contra", _contra));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("physical_location", _physicalLocation));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("dayOfWeek", _dayOfWeek));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("trade_server_id", _tradeServerId));
			tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("trade_id", _tradeID));
			classDescriptor = tempDescriptor;

			RDBPersistentAdapter
					.setStaticClassDescription("com.cboe.domain.marketData.MarketDataHistoryEntryImpl", classDescriptor);

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

	private Object generateDBId()
	{
		return this.idGenerator.getId();
	}

	public SQLInsert getSQL()
	{
		SQLInsert rval = bypassReflection ? new SQLMarketDataHistoryInsert(this) : null;

		if ((rval != null) && (getDatabaseIdentifier() == null))
		{
			// we need to set the database identifier as the jgrinder code
			// doesn't do it since we are bypassing the jgrinder reflective code
			setDatabaseIdentifier(generateDBId());
		}

		return rval;
	}

	/**
	 * This method is called from market data history service proxy prior to
	 * making a CORBA call.
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
	 * Setter for ask price.
	 */
	public void setAskPrice(PriceSqlType aValue)
	{
		if (bypassReflection)
		{
			askPrice = aValue;
		}
		else
		{
			editor.set(_askPrice, aValue, askPrice);
		}
	}

	/**
	 * Setter for ask size.
	 */
	public void setAskSize(int aValue)
	{
		if (bypassReflection)
		{
			askSize = aValue;
		}
		else
		{
			editor.set(_askSize, aValue, askSize);
		}
	}

	/**
	 * Setter for bid price.
	 */
	public void setBidPrice(PriceSqlType aValue)
	{
		if (bypassReflection)
		{
			bidPrice = aValue;
		}
		else
		{
			editor.set(_bidPrice, aValue, bidPrice);
		}
	}

	/**
	 * Setter for bid size.
	 */
	public void setBidSize(int aValue)
	{
		if (bypassReflection)
		{
			bidSize = aValue;
		}
		else
		{
			editor.set(_bidSize, aValue, bidSize);
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
	 * Setter for eop type.
	 */
	public void setEopType(short aValue)
	{
		if (bypassReflection)
		{
			eopType = aValue;
		}
		else
		{
			editor.set(_eopType, aValue, eopType);
		}
	}

	/**
	 * Setter for imbalance Quantity.
	 */
	public void setImbalanceQuantity(int aValue)
	{
		if (bypassReflection)
		{
			imbalanceQuantity = aValue;
		}
		else
		{
			editor.set(_imbalanceQuantity, aValue, imbalanceQuantity);
		}
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
	 * Setter for non-contingent ask price.
	 */
	public void setBestLimitAskPrice(PriceSqlType aValue)
	{
		if (bypassReflection)
		{
			bestLimitAskPrice = aValue;
		}
		else
		{
			editor.set(_bestLimitAskPrice, aValue, bestLimitAskPrice);
		}
	}

	/**
	 * Setter for non-contingent ask size.
	 */
	public void setBestLimitAskSize(int aValue)
	{
		if (bypassReflection)
		{
			bestLimitAskSize = aValue;
		}
		else
		{
			editor.set(_bestLimitAskSize, aValue, bestLimitAskSize);
		}
	}

	/**
	 * Setter for non-contingent bid price.
	 */
	public void setBestLimitBidPrice(PriceSqlType aValue)
	{
		if (bypassReflection)
		{
			bestLimitBidPrice = aValue;
		}
		else
		{
			editor.set(_bestLimitBidPrice, aValue, bestLimitBidPrice);
		}
	}

	/**
	 * Setter for non-contingent bid size.
	 */
	public void setBestLimitBidSize(int aValue)
	{
		if (bypassReflection)
		{
			bestLimitBidSize = aValue;
		}
		else
		{
			editor.set(_bestLimitBidSize, aValue, bestLimitBidSize);
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
	 * Set the override indicator
	 */
	public void setOverrideIndicator(char aValue)
	{
		if (bypassReflection)
		{
			overrideIndicator = aValue;
		}
		else
		{
			editor.set(_overrideIndicator, aValue, overrideIndicator);
		}
	}

	/**
	 * Set the nbbo ask price
	 */
	public void setNBBOAskPrice(PriceSqlType aValue)
	{
		if (bypassReflection)
		{
			nbboAskPrice = aValue;
		}
		else
		{
			editor.set(_nbboAskPrice, aValue, nbboAskPrice);
		}
	}

	/**
	 * Set the nbbo bid price
	 */
	public void setNBBOBidPrice(PriceSqlType aValue)
	{
		if (bypassReflection)
		{
			nbboBidPrice = aValue;
		}
		else
		{
			editor.set(_nbboBidPrice, aValue, nbboBidPrice);
		}
	}

	/**
	 * Set the nbbo ask exchange and volume
	 */

	public void setNBBOAskExchange(ExchangeVolumeStruct[] askVolumesStruct)
	{
		if (askVolumesStruct == null)
			return;

		ExchangeVolume[] volumes = new ExchangeVolume[askVolumesStruct.length];
		for (int i = 0; i < askVolumesStruct.length; i++)
		{
			if (askVolumesStruct[i].exchange == null)
			{
				askVolumesStruct[i].exchange = "";
			}
			volumes[i] = new ExchangeVolume(askVolumesStruct[i]);
		}
		ExchangeVolumeHolder volumeHolder = new ExchangeVolumeHolder(volumes);
		if (bypassReflection)
		{
			nbboAskExchange = volumeHolder;
		}
		else
		{
			editor.set(_nbboAskExchange, volumeHolder, nbboAskExchange);
		}
	}

	/**
	 * Set the nbbo bid exchange and volume
	 */
	public void setNBBOBidExchange(ExchangeVolumeStruct[] bidVolumesStruct)
	{

		if (bidVolumesStruct == null)
			return;

		ExchangeVolume[] volumes = new ExchangeVolume[bidVolumesStruct.length];
		for (int i = 0; i < bidVolumesStruct.length; i++)
		{
			if (bidVolumesStruct[i].exchange == null)
			{
				bidVolumesStruct[i].exchange = "";
			}
			volumes[i] = new ExchangeVolume(bidVolumesStruct[i]);
		}
		ExchangeVolumeHolder volumeHolder = new ExchangeVolumeHolder(volumes);
		if (bypassReflection)
		{
			nbboBidExchange = volumeHolder;
		}
		else
		{
			editor.set(_nbboBidExchange, volumeHolder, nbboBidExchange);
		}
	}

	/**
	 * Set the botr ask price
	 */
	public void setBOTRAskPrice(PriceSqlType aValue)
	{
		if (bypassReflection)
		{
			botrAskPrice = aValue;
		}
		else
		{
			editor.set(_botrAskPrice, aValue, botrAskPrice);
		}
	}

	/**
	 * Set the botr bid price
	 */
	public void setBOTRBidPrice(PriceSqlType aValue)
	{
		if (bypassReflection)
		{
			botrBidPrice = aValue;
		}
		else
		{
			editor.set(_botrBidPrice, aValue, botrBidPrice);
		}
	}

	/**
	 * Set the botr ask exchange and volume
	 */

	public void setBOTRAskExchange(ExchangeVolumeStruct[] askVolumesStruct)
	{
		if (askVolumesStruct == null)
			return;

		ExchangeVolume[] volumes = new ExchangeVolume[askVolumesStruct.length];
		for (int i = 0; i < askVolumesStruct.length; i++)
		{
			if (askVolumesStruct[i].exchange == null)
			{
				askVolumesStruct[i].exchange = "";
			}
			volumes[i] = new ExchangeVolume(askVolumesStruct[i]);
		}
		ExchangeVolumeHolder volumeHolder = new ExchangeVolumeHolder(volumes);
		if (bypassReflection)
		{
			botrAskExchange = volumeHolder;
		}
		else
		{
			editor.set(_botrAskExchange, volumeHolder, botrAskExchange);
		}
	}

	/**
	 * Set the nbbo bid exchange and volume
	 */
	public void setBOTRBidExchange(ExchangeVolumeStruct[] bidVolumesStruct)
	{
		if (bidVolumesStruct == null)
			return;

		ExchangeVolume[] volumes = new ExchangeVolume[bidVolumesStruct.length];
		for (int i = 0; i < bidVolumesStruct.length; i++)
		{
			if (bidVolumesStruct[i].exchange == null)
			{
				bidVolumesStruct[i].exchange = "";
			}
			volumes[i] = new ExchangeVolume(bidVolumesStruct[i]);
		}
		ExchangeVolumeHolder volumeHolder = new ExchangeVolumeHolder(volumes);
		if (bypassReflection)
		{
			botrBidExchange = volumeHolder;
		}
		else
		{
			editor.set(_botrBidExchange, volumeHolder, botrBidExchange);
		}
	}

	/**
	 * Set the trade through indicator
	 */
	public void setTradeThroughIndicator(char aValue)
	{
		if (bypassReflection)
		{
			tradeThroughIndicator = aValue;
		}
		else
		{
			editor.set(_tradeThroughIndicator, aValue, tradeThroughIndicator);
		}
	}

	/**
	 * Set the exchange indicator
	 */
	public void setExchangeIndicators(ExchangeIndicatorStruct[] exchangeIndicatorStruct, short productState)
	{
		boolean cboeAlreadyIn = false;
		if (exchangeIndicatorStruct == null)
			return;
		// we assume that most of the time CBOE will not be there so we will
		// reserve room for it
		ExchangeIndicator[] indicators = new ExchangeIndicator[exchangeIndicatorStruct.length + 1]; // make
		// room
		// for
		// CBOE
		for (int i = 0; i < exchangeIndicatorStruct.length; i++)
		{
			if (exchangeIndicatorStruct[i].exchange == null)
			{
				exchangeIndicatorStruct[i].exchange = "";
			}
			if (exchangeIndicatorStruct[i].exchange.equals("CBOE"))
			{
				cboeAlreadyIn = true; // CBOE already in - probably coming
				// from TPF
			}
			indicators[i] = new ExchangeIndicator(exchangeIndicatorStruct[i]);
		}
		ExchangeIndicatorHolder indicatorHolder = null;
		if (cboeAlreadyIn)
		{
			ExchangeIndicator[] newIndicators = new ExchangeIndicator[exchangeIndicatorStruct.length];
			System.arraycopy(indicators, 0, newIndicators, 0, newIndicators.length); // need
			// to
			// copy to a
			// smaller
			// array
			indicatorHolder = new ExchangeIndicatorHolder(newIndicators);
		}
		else
		{
			ExchangeIndicatorStruct eiStruct = new ExchangeIndicatorStruct("CBOE", mapProductStateToMarketCondition(productState)); // set
			// it
			// based
			// on
			// the
			// state
			indicators[indicators.length - 1] = new ExchangeIndicator(eiStruct); // this
			// will
			// be
			// the last
			// element
			indicatorHolder = new ExchangeIndicatorHolder(indicators);
		}

		setExchangeIndicator(indicatorHolder);
	}

	private void setExchangeIndicator(ExchangeIndicatorHolder indicatorHolder)
	{
		if (bypassReflection)
		{
			exchangeIndicators = indicatorHolder;
		}
		else
		{
			editor.set(_exchangeIndicators, indicatorHolder, exchangeIndicators);
		}
	}

	private void setExchangeIndicators(ExchangeIndicatorStruct[] exchangeIndicatorStruct)
	{
		ExchangeIndicator[] indicators = new ExchangeIndicator[exchangeIndicatorStruct.length];

		for (int i = 0; i < exchangeIndicatorStruct.length; i++)
		{
			indicators[i] = new ExchangeIndicator(exchangeIndicatorStruct[i]);
		}

		ExchangeIndicatorHolder holder = new ExchangeIndicatorHolder(indicators);

		setExchangeIndicator(holder);
	}

	public short mapProductStateToMarketCondition(short newState)
	{
		switch (newState)
		{
		case ProductStates.FAST_MARKET:
			return ExchangeIndicatorTypes.FAST_MARKET;
		case ProductStates.HALTED:
			return ExchangeIndicatorTypes.HALTED;
		case ProductStates.OPENING_ROTATION:
			return ExchangeIndicatorTypes.OPENING_ROTATION;
		default:
			return ExchangeIndicatorTypes.CLEAR;
		}
	}

	/**
	 * Set the broker
	 */
	public void setBroker(ExchangeAcronymStruct[] exchangeAcronymStruct)
	{
		if (exchangeAcronymStruct == null)
			return;
		int numberOfBrokers = exchangeAcronymStruct.length < MAX_BROKERS ? exchangeAcronymStruct.length : MAX_BROKERS;
		ExchangeAcronym[] brokerExchangeAcronyms = new ExchangeAcronym[numberOfBrokers];
		for (int i = 0; i < numberOfBrokers; i++)
		{
			if (exchangeAcronymStruct[i].exchange == null)
			{
				exchangeAcronymStruct[i].exchange = "";
			}
			if (exchangeAcronymStruct[i].acronym == null)
			{
				exchangeAcronymStruct[i].acronym = "";
			}
			brokerExchangeAcronyms[i] = new ExchangeAcronym(exchangeAcronymStruct[i]);
		}
		ExchangeAcronymHolder brokerAcronymHolder = new ExchangeAcronymHolder(brokerExchangeAcronyms);
		if (bypassReflection)
		{
			broker = brokerAcronymHolder;
		}
		else
		{
			editor.set(_broker, brokerAcronymHolder, broker);
		}
	}

	/**
	 * Set the contra
	 */
	public void setContra(ExchangeAcronymStruct[] exchangeAcronymStruct)
	{
		if (exchangeAcronymStruct == null)
			return;
		int numberOfBrokers = exchangeAcronymStruct.length < MAX_BROKERS ? exchangeAcronymStruct.length : MAX_BROKERS;
		ExchangeAcronym[] contraExchangeAcronyms = new ExchangeAcronym[numberOfBrokers];
		for (int i = 0; i < numberOfBrokers; i++)
		{
			if (exchangeAcronymStruct[i].exchange == null)
			{
				exchangeAcronymStruct[i].exchange = "";
			}
			if (exchangeAcronymStruct[i].acronym == null)
			{
				exchangeAcronymStruct[i].acronym = "";
			}
			contraExchangeAcronyms[i] = new ExchangeAcronym(exchangeAcronymStruct[i]);
		}
		ExchangeAcronymHolder contraAcronymHolder = new ExchangeAcronymHolder(contraExchangeAcronyms);
		if (bypassReflection)
		{
			contra = contraAcronymHolder;
		}
		else
		{
			editor.set(_contra, contraAcronymHolder, contra);
		}
	}

	/**
	 * Set the physical location
	 */
	public void setPhysicalLocation(String aValue)
	{
		if (bypassReflection)
		{
			physicalLocation = aValue;
		}
		else
		{
			editor.set(_physicalLocation, aValue, physicalLocation);
		}
	}

	/**
	 * Setter for tradeID.
	 */
	public void setTradeID(CboeIdStruct aValue)
	{
		if (bypassReflection)
		{
			tradeId = aValue != null ? CboeId.longValue(aValue) : 0;
		}
		else
		{
			editor.set(_tradeID, CboeId.longValue(new CboeIdStruct(aValue.highCboeId, aValue.lowCboeId)), tradeId);
		}
	}

	/**
	 * Setter for underlying price.
	 */
	public void setUnderlyingLastSalePrice(PriceSqlType aValue)
	{
		if (bypassReflection)
		{
			underlyingLastSalePrice = aValue;
		}
		else
		{
			editor.set(_underlyingLastSalePrice, aValue, underlyingLastSalePrice);
		}
	}

	/**
	 * Converts this entry to a CORBA struct.
	 * 
	 * @author John Wickberg
	 */
	public MarketDataHistoryEntryStruct toStruct()
	{
		MarketDataHistoryEntryStruct struct = MarketDataStructBuilder.buildMarketDataHistoryEntryStruct();
		struct.entryType = getEntryType();
		struct.reportTime = DateWrapper.convertToDateTime(getEntryTime());
		struct.underlyingLastSalePrice = getUnderlyingLastSalePrice().toStruct();
		struct.marketCondition = getProductState();
		struct.prefix = getTickerPrefix();

		Price lastSalePrice;
		switch (struct.entryType)
		{
		case MarketDataHistoryEntryTypes.PRICE_REPORT_ENTRY:
            lastSalePrice = getLastSalePrice();
            if(lastSalePrice == null)
            {
                lastSalePrice = NO_PRICE;
            }
			struct.price = lastSalePrice.toStruct();
			struct.quantity = getLastSaleVolume();
			break;
		case MarketDataHistoryEntryTypes.QUOTE_ENTRY:
			if (getBestLimitBidPrice() != null)
			{
				struct.bidPrice = getBestLimitBidPrice().toStruct();
			}
			struct.bidSize = getBestLimitBidSize();
			if (getBestLimitAskPrice() != null)
			{
				struct.askPrice = getBestLimitAskPrice().toStruct();
			}
			struct.askSize = getBestLimitAskSize();
			break;
		case MarketDataHistoryEntryTypes.EXPECTED_OPEN_PRICE:
            lastSalePrice = getLastSalePrice();
            if(lastSalePrice == null)
              {
                lastSalePrice = NO_PRICE;
              }

			struct.price = lastSalePrice.toStruct();
			break;
		case MarketDataHistoryEntryTypes.MARKET_CONDITION_ENTRY:
			// actually all the data for this was filled in before the switch,
			// this case is coded to
			// prevent fallthru to default (exception)
			break;
        case MarketDataHistoryEntryTypes.SHORT_SALE_TRIGGER_ON:
            break;
        case MarketDataHistoryEntryTypes.SHORT_SALE_TRIGGER_OFF:
            break;
		default:
			IllegalArgumentException e = new IllegalArgumentException("Market history entry has invalid type value of "
					+ struct.entryType);
			Log.exception(this, e);
			throw e;
		}
		return struct;
	}

	/**
	 * Converts this entry to a CORBA struct.
	 * 
	 * @author Nikhil Patel
	 */
	public MarketDataHistoryDetailEntryStruct toDetailStruct()
	{
		MarketDataHistoryDetailEntryStruct struct;
		struct = MarketDataStructBuilder.buildMarketDataHistoryDetailEntryStruct();

		if (getOverrideIndicator() == '0')
			struct.detailData.overrideIndicator = ' ';
		else
			struct.detailData.overrideIndicator = getOverrideIndicator();

		if (getPhysicalLocation() != null)
		{
			struct.historyEntry.physLocation = getPhysicalLocation();
		}
		if (getNBBOAskPrice() != null)
		{
			struct.detailData.nbboAskPrice = getNBBOAskPrice().toStruct();
		}
		if (getNBBOBidPrice() != null)
		{
			struct.detailData.nbboBidPrice = getNBBOBidPrice().toStruct();
		}
		ExchangeVolumeStruct[] askVolumeStruct = getNBBOAskExchange();
		if (askVolumeStruct != null)
		{
			if (askVolumeStruct.length > 0)
			{
				struct.detailData.nbboAskExchanges = new ExchangeVolumeStruct[askVolumeStruct.length];
				for (int i = 0; i < askVolumeStruct.length; i++)
				{
					/*ExchangeVolumeStruct askResult = new ExchangeVolumeStruct();
					askResult.exchange = askVolumeStruct[i].exchange;
					askResult.volume = askVolumeStruct[i].volume;*/
					ExchangeVolumeStruct askResult = MarketDataStructBuilder.getExchangeVolumeStruct(askVolumeStruct[i].exchange,askVolumeStruct[i].volume); 
					struct.detailData.nbboAskExchanges[i] = askResult;
				}
			}
		}

		ExchangeVolumeStruct[] bidVolumeStruct = getNBBOBidExchange();
		if (bidVolumeStruct != null)
		{
			if (bidVolumeStruct.length > 0)
			{
				struct.detailData.nbboBidExchanges = new ExchangeVolumeStruct[bidVolumeStruct.length];
				for (int i = 0; i < bidVolumeStruct.length; i++)
				{
					//ExchangeVolumeStruct bidResult = new ExchangeVolumeStruct();
					ExchangeVolumeStruct bidResult = MarketDataStructBuilder.getExchangeVolumeStruct(bidVolumeStruct[i].exchange,bidVolumeStruct[i].volume);
					//bidResult.exchange = bidVolumeStruct[i].exchange;
					//bidResult.volume = bidVolumeStruct[i].volume;
					struct.detailData.nbboBidExchanges[i] = bidResult;
				}
			}
		}

		struct.detailData.tradeThroughIndicator = getTradeThroughIndicator();

		ExchangeIndicatorStruct[] exchangeIndicatorStruct = getExchangeIndicator();
		if (exchangeIndicatorStruct != null)
		{
			if (exchangeIndicatorStruct.length > 0)

			{
				struct.detailData.exchangeIndicators = new ExchangeIndicatorStruct[exchangeIndicatorStruct.length];
				for (int i = 0; i < exchangeIndicatorStruct.length; i++)
				{
					ExchangeIndicatorStruct exchangeindicatorResult = new ExchangeIndicatorStruct();
					exchangeindicatorResult.exchange = exchangeIndicatorStruct[i].exchange;
					exchangeindicatorResult.marketCondition = exchangeIndicatorStruct[i].marketCondition;
					struct.detailData.exchangeIndicators[i] = exchangeindicatorResult;
				}
			}
		}
		if (getBestPublicBidPrice() != null)
		{
			struct.detailData.bestPublishedBidPrice = getBestPublicBidPrice().toStruct();
		}
		if (getBestPublicAskPrice() != null)
		{
			struct.detailData.bestPublishedAskPrice = getBestPublicAskPrice().toStruct();
		}
		struct.detailData.bestPublishedBidVolume = getBestPublicCustomerBidSize();
		struct.detailData.bestPublishedAskVolume = getBestPublicCustomerAskSize();

		ExchangeAcronymStruct[] brokerAcronymStruct = getBroker();

		if (brokerAcronymStruct != null)
		{
			if (brokerAcronymStruct.length > 0)
			{
				struct.detailData.brokers = new ExchangeAcronymStruct[brokerAcronymStruct.length];
				for (int i = 0; i < brokerAcronymStruct.length; i++)
				{
					ExchangeAcronymStruct brokerResult = new ExchangeAcronymStruct();
					brokerResult.exchange = brokerAcronymStruct[i].exchange;
					brokerResult.acronym = brokerAcronymStruct[i].acronym;
					struct.detailData.brokers[i] = brokerResult;
				}
			}
		}

		ExchangeAcronymStruct[] contraAcronymStruct = getContra();
		if (contraAcronymStruct != null)
		{
			if (contraAcronymStruct.length > 0)
			{
				struct.detailData.contras = new ExchangeAcronymStruct[contraAcronymStruct.length];
				for (int i = 0; i < contraAcronymStruct.length; i++)
				{
					ExchangeAcronymStruct contraResult = new ExchangeAcronymStruct();
					contraResult.exchange = contraAcronymStruct[i].exchange;
					contraResult.acronym = contraAcronymStruct[i].acronym;
					struct.detailData.contras[i] = contraResult;
				}
			}
		}
		struct.historyEntry.entryType = getEntryType();
		struct.historyEntry.reportTime = DateWrapper.convertToDateTime(getEntryTime());
		struct.historyEntry.underlyingLastSalePrice = getUnderlyingLastSalePrice().toStruct();
		struct.historyEntry.marketCondition = getProductState();
		struct.historyEntry.prefix = getTickerPrefix();

		Price lastSalePrice;
		switch (struct.historyEntry.entryType)
		{
		case MarketDataHistoryEntryTypes.PRICE_REPORT_ENTRY:
            lastSalePrice = getLastSalePrice();
            if(lastSalePrice == null)
              {
                lastSalePrice = NO_PRICE;
              }
			struct.historyEntry.price = lastSalePrice.toStruct();
			struct.historyEntry.quantity = getLastSaleVolume();
			break;
		case MarketDataHistoryEntryTypes.QUOTE_ENTRY:
			if (getBestLimitBidPrice() != null)
			{
				struct.historyEntry.bidPrice = getBestLimitBidPrice().toStruct();
			}
			struct.historyEntry.bidSize = getBestLimitBidSize();
			if (getBestLimitAskPrice() != null)
			{
				struct.historyEntry.askPrice = getBestLimitAskPrice().toStruct();
			}
			struct.historyEntry.askSize = getBestLimitAskSize();
			break;
		case MarketDataHistoryEntryTypes.EXPECTED_OPEN_PRICE:
		    
		      lastSalePrice = getLastSalePrice();
		      if(lastSalePrice == null)
		        {
		          lastSalePrice = NO_PRICE;
		        }
  	           struct.historyEntry.price = lastSalePrice.toStruct();

			break;
		case MarketDataHistoryEntryTypes.MARKET_CONDITION_ENTRY:
			// actually all the data for this was filled in before the switch,
			// this case is coded to
			// prevent fallthru to default (exception)
			break;
        case MarketDataHistoryEntryTypes.SHORT_SALE_TRIGGER_ON:
            break;
        case MarketDataHistoryEntryTypes.SHORT_SALE_TRIGGER_OFF:
            break;
		default:
			IllegalArgumentException e = new IllegalArgumentException("Market history entry has invalid type value of "
					+ struct.historyEntry.entryType);
			Log.exception(this, e);
			throw e;
		}
		return struct;
	}

	/**
	 * This method allows me to get arounds security problems with updating and
	 * object from a generic framework.
	 */
	public void update(boolean get, Object[] data, Field[] fields)
	{
		for (int i = 0; i < data.length; i++)
		{
			try
			{
				if (get)
					data[i] = fields[i].get(this);
				else
					fields[i].set(this, data[i]);
			}
			catch (IllegalAccessException ex)
			{
				System.out.println("Cannot set " + fields[i].getName() + " of " + this.getClass().getName() + ":" + ex);
			}
			catch (IllegalArgumentException ex)
			{
				System.out.println("Cannot set " + fields[i].getName() + " of " + this.getClass().getName() + ":" + ex);
			}
		}
	}

	public void setBestPublicBidPrice(PriceSqlType aValue)
	{
		if (bypassReflection)
		{
			bestPublicBidPrice = aValue;
		}
		else
		{
			editor.set(_bestPublicBidPrice, aValue, bestPublicBidPrice);
		}
	}

	public void setBestPublicBidSize(int aValue)
	{
		if (bypassReflection)
		{
			bestPublicBidSize = aValue;
		}
		else
		{
			editor.set(_bestPublicBidSize, aValue, bestPublicBidSize);
		}
	}

	public void setBestPublicAskPrice(PriceSqlType aValue)
	{
		if (bypassReflection)
		{
			bestPublicAskPrice = aValue;
		}
		else
		{
			editor.set(_bestPublicAskPrice, aValue, bestPublicAskPrice);
		}
	}

	public void setBestPublicCustomerBidSize(int aValue)
	{
		if (bypassReflection)
		{
			bestPublicCustomerBidSize = aValue;
		}
		else
		{
			editor.set(_bestPublicCustomerBidSize, aValue, bestPublicCustomerBidSize);
		}

	}

	public void setBestPublicCustomerAskSize(int aValue)
	{
		if (bypassReflection)
		{
			bestPublicCustomerAskSize = aValue;
		}
		else
		{
			editor.set(_bestPublicCustomerAskSize, aValue, bestPublicCustomerAskSize);
		}
	}

	public void setBestPublicAskSize(int aValue)
	{
		if (bypassReflection)
		{
			bestPublicAskSize = aValue;
		}
		else
		{
			editor.set(_bestPublicAskSize, aValue, bestPublicAskSize);
		}
	}

	public PriceSqlType getBestPublicAskPrice()
	{
		return bypassReflection ? bestPublicAskPrice : (PriceSqlType) editor.get(_bestPublicAskPrice, bestPublicAskPrice);
	}

	public int getBestPublicAskSize()
	{
		return bypassReflection ? bestPublicAskSize : (int) editor.get(_bestPublicAskSize, bestPublicAskSize);
	}

	public PriceSqlType getBestPublicBidPrice()
	{
		return bypassReflection ? bestPublicBidPrice : (PriceSqlType) editor.get(_bestPublicBidPrice, bestPublicBidPrice);
	}

	public int getBestPublicBidSize()
	{
		return bypassReflection ? bestPublicBidSize : (int) editor.get(_bestPublicBidSize, bestPublicBidSize);
	}

	public int getBestPublicCustomerAskSize()
	{
		return bypassReflection ? bestPublicCustomerAskSize : (int) editor.get(_bestPublicCustomerAskSize,
				bestPublicCustomerAskSize);
	}

	public int getBestPublicCustomerBidSize()
	{
		return bypassReflection ? bestPublicCustomerBidSize : (int) editor.get(_bestPublicCustomerBidSize,
				bestPublicCustomerBidSize);
	}

	public PriceSqlType getPriceForDB(PriceStruct aPrice)
	{
		if (aPrice == null || StructBuilder.isDefault(aPrice))
		{
			return null;
		}
		return new PriceSqlType(aPrice);
	}

	/**
	 * set the values related to best market
	 */
	public void setValuesForBestMarket(CurrentMarketStruct aMarket)
	{
		if (aMarket == null)
		{
			return;
		}
		setBidPrice(getPriceForDB(aMarket.bidPrice));
		setBidSize(calcSize(aMarket.bidSizeSequence, CurrentMarketViewTypes.BEST_PRICE));
		setAskPrice(getPriceForDB(aMarket.askPrice));
		setAskSize(calcSize(aMarket.askSizeSequence, CurrentMarketViewTypes.BEST_PRICE));
	}

	/**
	 * set the values related to best limit market
	 */
	public void setValuesForBestLimitMarket(CurrentMarketStruct aMarket)
	{
		if (aMarket == null)
		{
			return;
		}
		setBestLimitBidPrice(getPriceForDB(aMarket.bidPrice));
		setBestLimitBidSize(calcSize(aMarket.bidSizeSequence, CurrentMarketViewTypes.BEST_LIMIT_PRICE));
		setBestLimitAskPrice(getPriceForDB(aMarket.askPrice));
		setBestLimitAskSize(calcSize(aMarket.askSizeSequence, CurrentMarketViewTypes.BEST_LIMIT_PRICE));
	}

	/**
	 * set the values related to best limit market
	 */
	public void setValuesForBestPublicMarket(CurrentMarketStruct aMarket)
	{
		if (aMarket == null)
		{
			return;
		}
		setBestPublicBidPrice(getPriceForDB(aMarket.bidPrice));
		setBestPublicBidSize(calcSize(aMarket.bidSizeSequence, CurrentMarketViewTypes.BEST_PUBLIC_ORDER_PRICE));
		setBestPublicAskPrice(getPriceForDB(aMarket.askPrice));
		setBestPublicAskSize(calcSize(aMarket.askSizeSequence, CurrentMarketViewTypes.BEST_PUBLIC_ORDER_PRICE));
	}

	/**
	 * set the values related to best limit market
	 */
	public void setValuesForBestCustomerPublicMarket(CurrentMarketStruct aMarket)
	{
		if (aMarket == null)
		{
			return;
		}
		setBestPublicCustomerBidSize(calcCustomerSize(aMarket.bidSizeSequence));
		setBestPublicCustomerAskSize(calcCustomerSize(aMarket.askSizeSequence));
	}

	public int calcCustomerSize(MarketVolumeStruct[] volume)
	{
		int total = 0;

		if (volume != null)
		{
			for (int i = 0; i < volume.length; i++)
			{
				if (volume[i].volumeType == VolumeTypes.CUSTOMER_ORDER)
				{
					total += volume[i].quantity;
				}
			}
		}
		return total;
	}

	/**
	 * @return
	 */
	public ExchangeVolumeHolder getBotrAskExchange()
	{
		return bypassReflection ? this.botrAskExchange : (ExchangeVolumeHolder) editor.get(_botrAskExchange, botrAskExchange);
	}

	public ExchangeVolumeStruct[] getBOTRAskExchange()
	{
		ExchangeVolumeHolder holder = getBotrAskExchange();
		ExchangeVolumeStruct[] rval = null;

		if (holder != null)
		{
			ExchangeVolume[] exchangeVolumes = holder.getExchangeVolumes();
			int size = exchangeVolumes.length;
			rval = new ExchangeVolumeStruct[size];
			for (int i = 0; i < size; i++)
			{
				rval[i] = exchangeVolumes[i].toStruct();
			}
		}

		return rval;
	}

	/**
	 * @return
	 */
	public PriceSqlType getBotrAskPrice()
	{
		return bypassReflection ? this.botrAskPrice : (PriceSqlType) editor.get(_botrAskPrice, botrAskPrice);
	}

	/**
	 * @return
	 */
	public ExchangeVolumeHolder getBotrBidExchange()
	{
		return bypassReflection ? this.botrBidExchange : (ExchangeVolumeHolder) editor.get(_botrBidExchange, botrBidExchange);
	}

	public ExchangeVolumeStruct[] getBOTRBidExchange()
	{
		ExchangeVolumeHolder holder = getBotrBidExchange();
		ExchangeVolumeStruct[] rval = null;

		if (holder != null)
		{
			ExchangeVolume[] exchangeVolumes = holder.getExchangeVolumes();
			int size = exchangeVolumes.length;
			rval = new ExchangeVolumeStruct[size];
			for (int i = 0; i < size; i++)
			{
				rval[i] = exchangeVolumes[i].toStruct();
			}
		}

		return rval;
	}

	/**
	 * @return
	 */
	public PriceSqlType getBotrBidPrice()
	{
		return bypassReflection ? botrBidPrice : (PriceSqlType) editor.get(_botrBidPrice, botrBidPrice);
	}

	/**
	 * @return
	 */
	public ExchangeIndicatorHolder getExchangeIndicators()
	{
		return bypassReflection ? exchangeIndicators : (ExchangeIndicatorHolder) editor
				.get(_exchangeIndicators, exchangeIndicators);
	}

	/**
	 * @return
	 */
	public ExchangeVolumeHolder getNbboAskExchange()
	{
		return bypassReflection ? nbboAskExchange : (ExchangeVolumeHolder) editor.get(_nbboAskExchange, nbboAskExchange);
	}

	/**
	 * @return
	 */
	public PriceSqlType getNbboAskPrice()
	{
		return bypassReflection ? nbboAskPrice : (PriceSqlType) editor.get(_nbboAskPrice, nbboAskPrice);
	}

	/**
	 * @return
	 */
	public ExchangeVolumeHolder getNbboBidExchange()
	{
		return bypassReflection ? nbboBidExchange : (ExchangeVolumeHolder) editor.get(_nbboBidExchange, nbboBidExchange);
	}

	/**
	 * @return
	 */
	public PriceSqlType getNbboBidPrice()
	{
		return bypassReflection ? nbboBidPrice : (PriceSqlType) editor.get(_nbboBidPrice, nbboBidPrice);
	}

	/**
	 * @return
	 */
	public long getTradeID()
	{
		return bypassReflection ? tradeId : editor.get(_tradeID, tradeId);
	}

	/**
	 * @return
	 */
	public ExchangeAcronymHolder getBrokerHolder()
	{
		return bypassReflection ? broker : (ExchangeAcronymHolder) editor.get(_broker, broker);
	}

	/**
	 * @return
	 */
	public ExchangeAcronymHolder getContraHolder()
	{
		return bypassReflection ? contra : (ExchangeAcronymHolder) editor.get(_contra, contra);
	}

	/**
	 * @return
	 */
	public char getTradeThroughIndicatorAsChar()
	{
		return bypassReflection ? tradeThroughIndicator : editor.get(_tradeThroughIndicator, tradeThroughIndicator);
	}

	public void setDatabaseIdentifier(Object value)
	{
		this.databaseIdentifier = value;
	}

	/**
	 * @return
	 */
	public Object getDatabaseIdentifier()
	{
		return databaseIdentifier;
	}

	private PriceStruct toStruct(Price type)
	{
		return type == null ? nullPriceStruct : type.toStruct();
	}

	private PriceSqlType fmStruct(PriceStruct struct)
	{
		return struct.type > 0 ? new PriceSqlType(struct) : null;
	}

	public MarketDataHistoryEntryStructV1 toMarketDataHistoryEntryStructV1Struct()
	{
		MarketDataHistoryEntryImpl entry = this;
		MarketDataHistoryEntryStructV1 struct = new MarketDataHistoryEntryStructV1();

		struct.productKey = entry.getProductKey();
		struct.entryType = entry.getEntryType();
		struct.entryTime = entry.getEntryTime();
		struct.bidPrice = toStruct(entry.getBidPrice());
		struct.bidSize = entry.getBidSize();
		struct.askPrice = toStruct(entry.getAskPrice());
		struct.askSize = entry.getAskSize();
		struct.bestLimitBidPrice = toStruct(entry.getBestLimitBidPrice());
		struct.bestLimitBidSize = entry.getBestLimitBidSize();
		struct.bestLimitAskPrice = toStruct(entry.getBestLimitAskPrice());
		struct.bestLimitAskSize = entry.getBestLimitAskSize();
		struct.bestPublicBidPrice = toStruct(entry.getBestPublicBidPrice());
		struct.bestPublicBidSize = entry.getBestPublicBidSize();
		struct.bestPublicAskPrice = toStruct(entry.getBestPublicAskPrice());
		struct.bestPublicAskSize = entry.getBestPublicAskSize();
		struct.lastSalePrice = toStruct(entry.getLastSalePrice());
		struct.lastSaleVolume = entry.getLastSaleVolume();
		struct.underlyingLastSalePrice = toStruct(entry.getUnderlyingLastSalePrice());
		struct.eopType = entry.getEopType();
		struct.imbalanceQuantity = entry.getImbalanceQuantity();
		struct.productState = entry.getProductState();
		struct.tickerPrefix = entry.getTickerPrefix();
		struct.sessionName = entry.getSessionName();
		struct.overrideIndicator = entry.getOverrideIndicator();
		struct.nbboAskPrice = toStruct(entry.getNbboAskPrice());
		struct.nbboBidPrice = toStruct(entry.getNbboBidPrice());
		struct.nbboAskExchange = toStruct(entry.getNBBOAskExchange());
		struct.nbboBidExchange = toStruct(entry.getNBBOBidExchange());
		struct.botrAskPrice = toStruct(entry.getBotrAskPrice());
		struct.botrBidPrice = toStruct(entry.getBotrBidPrice());
		struct.botrAskExchange = toStruct(entry.getBOTRAskExchange());
		struct.botrBidExchange = toStruct(entry.getBOTRBidExchange());
		struct.bestPublicCustomerAskSize = entry.getBestPublicCustomerAskSize();
		struct.bestPublicCustomerBidSize = entry.getBestPublicCustomerBidSize();
		struct.tradeThroughIndicator = entry.getTradeThroughIndicatorAsChar();
		struct.exchangeIndicators = toStruct(entry.getExchangeIndicator());
		struct.broker = toStruct(entry.getBroker());
		struct.contra = toStruct(entry.getContra());
		struct.physicalLocation = entry.getPhysicalLocation();
		struct.dayOfWeek = entry.getDayOfWeek();
		struct.tradeServerId = entry.getTradeServerId();
		struct.tradeID = entry.getTradeId();
		struct.databaseIdentifier = entry.getDBId();

		return struct;
	}

	private ExchangeIndicatorStruct[] toStruct(ExchangeIndicatorStruct[] exchangeIndicator)
	{
		return exchangeIndicator == null ? nullExchangeIndicatorStruct : exchangeIndicator;
	}

	private ExchangeAcronymStruct[] toStruct(ExchangeAcronymStruct[] struct)
	{
		return struct == null ? nullExchangeAcronymStruct : struct;
	}

	private ExchangeVolumeStruct[] toStruct(ExchangeVolumeStruct[] exchange)
	{
		return (exchange == null) ? nullExchangeVolumeStruct : exchange;
	}

	public MarketDataHistoryEntry fromCORBAStruct(MarketDataHistoryEntryStructV1 entry)
	{
		MarketDataHistoryEntryImpl impl = this;

		impl.setBypassReflectionFlag(true);
		impl.setProductKey(entry.productKey);
		impl.setEntryType(entry.entryType);
		impl.setEntryTime(entry.entryTime);
		impl.setBidPrice(fmStruct(entry.bidPrice));
		impl.setBidSize(entry.bidSize);
		impl.setAskPrice(fmStruct(entry.askPrice));
		impl.setAskSize(entry.askSize);
		impl.setBestLimitAskPrice(fmStruct(entry.bestLimitAskPrice));
		impl.setBestLimitAskSize(entry.bestLimitAskSize);
		impl.setBestLimitBidPrice(fmStruct(entry.bestLimitBidPrice));
		impl.setBestLimitBidSize(entry.bestLimitBidSize);
		impl.setBestPublicAskPrice(fmStruct(entry.bestPublicAskPrice));
		impl.setBestPublicAskSize(entry.bestPublicAskSize);
		impl.setBestPublicBidPrice(fmStruct(entry.bestPublicBidPrice));
		impl.setBestPublicBidSize(entry.bestPublicBidSize);
		impl.setLastSalePrice(fmStruct(entry.lastSalePrice));
		impl.setLastSaleVolume(entry.lastSaleVolume);
		impl.setUnderlyingLastSalePrice(fmStruct(entry.underlyingLastSalePrice));
		impl.setEopType(entry.eopType);
		impl.setImbalanceQuantity(entry.imbalanceQuantity);
		impl.setProductState(entry.productState);
		impl.setTickerPrefix(entry.tickerPrefix);
		impl.setSessionName(entry.sessionName);
		impl.setOverrideIndicator(entry.overrideIndicator);
		impl.setNbboAskPrice(fmStruct(entry.nbboAskPrice));
		impl.setNbboBidPrice(fmStruct(entry.nbboBidPrice));
		impl.setNBBOAskExchange(fmStruct(entry.nbboAskExchange));
		impl.setNBBOBidExchange(fmStruct(entry.nbboBidExchange));
		impl.setBotrAskPrice(fmStruct(entry.botrAskPrice));
		impl.setBotrBidPrice(fmStruct(entry.botrBidPrice));
		impl.setBOTRAskExchange(fmStruct(entry.botrAskExchange));
		impl.setBOTRBidExchange(fmStruct(entry.botrBidExchange));
		impl.setBestPublicAskSize(entry.bestPublicAskSize);
		impl.setBestPublicBidSize(entry.bestPublicBidSize);
		impl.setBestPublicCustomerAskSize(entry.bestPublicCustomerAskSize);
		impl.setBestPublicCustomerBidSize(entry.bestPublicCustomerBidSize);
		impl.setTradeThroughIndicator(entry.tradeThroughIndicator);
		impl.setExchangeIndicators(fmStruct(entry.exchangeIndicators), entry.productState);
		impl.setBroker(fmStruct(entry.broker));
		impl.setContra(fmStruct(entry.contra));
		impl.setPhysicalLocation(entry.physicalLocation);
		impl.setDayOfWeek(entry.dayOfWeek);
		impl.setTradeServerId(entry.tradeServerId);
		impl.setTradeID(entry.tradeID);
		impl.setDBId(entry.databaseIdentifier);

		return impl;
	}

	private ExchangeAcronymStruct[] fmStruct(ExchangeAcronymStruct[] struct)
	{
		return (struct == null) || (struct.length == 0) ? null : struct;
	}

	private ExchangeIndicatorStruct[] fmStruct(ExchangeIndicatorStruct[] struct)
	{
		return (struct == null) || (struct.length == 0) ? null : struct;
	}

	private ExchangeVolumeStruct[] fmStruct(ExchangeVolumeStruct[] struct)
	{
		return (struct == null) || (struct.length == 0) ? null : struct;
	}

	public MarketDataHistoryEntriesStruct toMarketDataHistoryEntriesStruct()
	{
		MarketDataHistoryEntriesStruct struct = new MarketDataHistoryEntriesStruct();

		switch (this.getEntryType())
		{
		case MarketDataHistoryEntryTypes.QUOTE_ENTRY:
			struct.quoteEntryList = new com.cboe.idl.internalBusinessServices.MarketDataHistoryQuoteEntryStruct[1];
			struct.quoteEntryList[0] = toMarketDataHistoryQuoteEntryStruct();
			break;
		case MarketDataHistoryEntryTypes.PRICE_REPORT_ENTRY:
			struct.priceReportEntryList = new com.cboe.idl.internalBusinessServices.MarketDataHistoryPriceReportStruct[1];
			struct.priceReportEntryList[0] = toMarketDataHistoryPriceReportStruct();
			break;

		case MarketDataHistoryEntryTypes.EXPECTED_OPEN_PRICE:
			struct.eopEntryList = new com.cboe.idl.internalBusinessServices.MarketDataHistoryExpectedOpeningPriceStruct[1];
			struct.eopEntryList[0] = toMarketDataHistoryExpectedOpeningPriceStruct();
			break;

		case MarketDataHistoryEntryTypes.MARKET_CONDITION_ENTRY:
			struct.marketConditionEntryList = new com.cboe.idl.internalBusinessServices.MarketDataHistoryMarketConditionEntryStruct[1];
			struct.marketConditionEntryList[0] = toMarketDataHistoryMarketConditionEntryStruct();
			break;

		case MarketDataHistoryEntryTypes.UNSIZED_QUOTE_ENTRY:
        case MarketDataHistoryEntryTypes.SHORT_SALE_TRIGGER_ON:
        case MarketDataHistoryEntryTypes.SHORT_SALE_TRIGGER_OFF:
		default:
			struct.historyEntryList = new com.cboe.idl.internalBusinessServices.MarketDataHistoryEntryStructV1[1];
			struct.historyEntryList[0] = toMarketDataHistoryEntryStructV1Struct();
			break;
		}

		return struct;
	}

	private MarketDataHistoryMarketConditionEntryStruct toMarketDataHistoryMarketConditionEntryStruct()
	{
		MarketDataHistoryEntryImpl entry = this;
		MarketDataHistoryMarketConditionEntryStruct struct = new MarketDataHistoryMarketConditionEntryStruct();

		struct.productKey = entry.getProductKey();
		struct.entryType = entry.getEntryType();
		struct.entryTime = entry.getEntryTime();
		struct.underlyingLastSalePrice = toStruct(entry.getUnderlyingLastSalePrice());
		struct.imbalanceQuantity = entry.getImbalanceQuantity();
		struct.productState = entry.getProductState();
		struct.sessionName = entry.getSessionName();
		struct.dayOfWeek = entry.getDayOfWeek();
		struct.tradeServerId = entry.getTradeServerId();
		struct.tradeID = entry.getTradeId();
		struct.databaseIdentifier = entry.getDBId();

		return struct;
	}

	private MarketDataHistoryExpectedOpeningPriceStruct toMarketDataHistoryExpectedOpeningPriceStruct()
	{
		MarketDataHistoryEntryImpl entry = this;
		MarketDataHistoryExpectedOpeningPriceStruct struct = new MarketDataHistoryExpectedOpeningPriceStruct();

		struct.productKey = entry.getProductKey();
		struct.entryType = entry.getEntryType();
		struct.entryTime = entry.getEntryTime();
		struct.lastSalePrice = toStruct (entry.getLastSalePrice());
		struct.underlyingLastSalePrice = toStruct(entry.getUnderlyingLastSalePrice());
		struct.eopType = entry.getEopType();
		struct.imbalanceQuantity = entry.getImbalanceQuantity();
		struct.productState = entry.getProductState();
		struct.sessionName = entry.getSessionName();
		struct.dayOfWeek = entry.getDayOfWeek();
		struct.tradeID = entry.getTradeId();
		struct.databaseIdentifier = entry.getDBId();
		struct.tradeServerId = entry.getTradeServerId();

		return struct;
	}

	private MarketDataHistoryPriceReportStruct toMarketDataHistoryPriceReportStruct()
	{
		MarketDataHistoryEntryImpl entry = this;
		MarketDataHistoryPriceReportStruct struct = new MarketDataHistoryPriceReportStruct();

		struct.productKey = entry.getProductKey();
		struct.entryType = entry.getEntryType();
		struct.entryTime = entry.getEntryTime();
		struct.bidPrice = toStruct(entry.getBidPrice());
		struct.bidSize = entry.getBidSize();
		struct.bestPublicBidPrice = toStruct(entry.getBestPublicBidPrice());
		struct.bestPublicAskPrice = toStruct(entry.getBestPublicAskPrice());
		struct.lastSalePrice = toStruct(entry.getLastSalePrice());
		struct.lastSaleVolume = entry.getLastSaleVolume();
		struct.underlyingLastSalePrice = toStruct(entry.getUnderlyingLastSalePrice());
		struct.productState = entry.getProductState();
		struct.tickerPrefix = entry.getTickerPrefix();
		struct.sessionName = entry.getSessionName();
		struct.overrideIndicator = entry.getOverrideIndicator();
		struct.nbboAskPrice = toStruct(entry.getNbboAskPrice());
		struct.nbboBidPrice = toStruct(entry.getNbboBidPrice());
		struct.nbboAskExchange = toStruct(entry.getNBBOAskExchange());
		struct.nbboBidExchange = toStruct(entry.getNBBOBidExchange());
		struct.botrAskPrice = toStruct(entry.getBotrAskPrice());
		struct.botrBidPrice = toStruct(entry.getBotrBidPrice());
		struct.botrAskExchange = toStruct(entry.getBOTRAskExchange());
		struct.botrBidExchange = toStruct(entry.getBOTRBidExchange());
		struct.bestPublicCustomerAskSize = entry.getBestPublicCustomerAskSize();
		struct.bestPublicCustomerBidSize = entry.getBestPublicCustomerBidSize();
		struct.tradeThroughIndicator = entry.getTradeThroughIndicatorAsChar();
		struct.exchangeIndicators = toStruct(entry.getExchangeIndicator());
		struct.broker = toStruct(entry.getBroker());
		struct.contra = toStruct(entry.getContra());
		struct.physicalLocation = entry.getPhysicalLocation();
		struct.dayOfWeek = entry.getDayOfWeek();
		struct.tradeID = entry.getTradeId();
		struct.databaseIdentifier = entry.getDBId();
		struct.tradeServerId = entry.getTradeServerId();
		return struct;
	}

	private MarketDataHistoryQuoteEntryStruct toMarketDataHistoryQuoteEntryStruct()
	{
		MarketDataHistoryEntryImpl entry = this;
		MarketDataHistoryQuoteEntryStruct struct = new MarketDataHistoryQuoteEntryStruct();

		struct.productKey = entry.getProductKey();
		struct.productState = entry.getProductState();
		struct.entryType = entry.getEntryType();
		struct.entryTime = entry.getEntryTime();
		struct.bidPrice = toStruct(entry.getBidPrice());
		struct.bidSize = entry.getBidSize();
		struct.askPrice = toStruct(entry.getAskPrice());
		struct.askSize = entry.getAskSize();
		struct.bestLimitBidPrice = toStruct(entry.getBestLimitBidPrice());
		struct.bestLimitBidSize = entry.getBestLimitBidSize();
		struct.bestLimitAskPrice = toStruct(entry.getBestLimitAskPrice());
		struct.bestLimitAskSize = entry.getBestLimitAskSize();
		struct.bestPublicBidPrice = toStruct(entry.getBestPublicBidPrice());
		struct.bestPublicBidSize = entry.getBestPublicBidSize();
		struct.bestPublicAskPrice = toStruct(entry.getBestPublicAskPrice());
		struct.bestPublicAskSize = entry.getBestPublicAskSize();
		struct.underlyingLastSalePrice = toStruct(entry.getUnderlyingLastSalePrice());
		struct.sessionName = entry.getSessionName();
		struct.nbboAskPrice = toStruct(entry.getNbboAskPrice());
		struct.nbboBidPrice = toStruct(entry.getNbboBidPrice());
		struct.nbboAskExchange = toStruct(entry.getNBBOAskExchange());
		struct.nbboBidExchange = toStruct(entry.getNBBOBidExchange());
		struct.botrAskPrice = toStruct(entry.getBotrAskPrice());
		struct.botrBidPrice = toStruct(entry.getBotrBidPrice());
		struct.botrAskExchange = toStruct(entry.getBOTRAskExchange());
		struct.botrBidExchange = toStruct(entry.getBOTRBidExchange());
		struct.bestPublicCustomerAskSize = entry.getBestPublicCustomerAskSize();
		struct.bestPublicCustomerBidSize = entry.getBestPublicCustomerBidSize();
		struct.exchangeIndicators = toStruct(entry.getExchangeIndicator());
		struct.physicalLocation = entry.getPhysicalLocation();
		struct.dayOfWeek = entry.getDayOfWeek();
		struct.databaseIdentifier = entry.getDBId();
		struct.tradeServerId = entry.getTradeServerId();
		return struct;
	}

	public MarketDataHistoryEntry fromCORBAStruct(MarketDataHistoryEntriesStruct struct)
	{
		if (struct.eopEntryList.length > 0)
		{
			fromCORBAStruct(struct.eopEntryList[0]);
		}
		else if (struct.historyEntryList.length > 0)
		{
			fromCORBAStruct(struct.historyEntryList[0]);
		}
		else if (struct.marketConditionEntryList.length > 0)
		{
			fromCORBAStruct(struct.marketConditionEntryList[0]);
		}
		else if (struct.priceReportEntryList.length > 0)
		{
			fromCORBAStruct(struct.priceReportEntryList[0]);
		}
		else if (struct.quoteEntryList.length > 0)
		{
			fromCORBAStruct(struct.quoteEntryList[0]);
		}

		return this;
	}

	private MarketDataHistoryEntry fromCORBAStruct(MarketDataHistoryQuoteEntryStruct entry)
	{
		MarketDataHistoryEntryImpl impl = this;

		impl.setBypassReflectionFlag(true);
		impl.setProductKey(entry.productKey);
		impl.setProductState(entry.productState);
		impl.setEntryType(entry.entryType);
		impl.setEntryTime(entry.entryTime);
		impl.setBidPrice(fmStruct(entry.bidPrice));
		impl.setBidSize(entry.bidSize);
		impl.setAskPrice(fmStruct(entry.askPrice));
		impl.setAskSize(entry.askSize);
		impl.setBestLimitAskPrice(fmStruct(entry.bestLimitAskPrice));
		impl.setBestLimitAskSize(entry.bestLimitAskSize);
		impl.setBestLimitBidPrice(fmStruct(entry.bestLimitBidPrice));
		impl.setBestLimitBidSize(entry.bestLimitBidSize);
		impl.setBestPublicAskPrice(fmStruct(entry.bestPublicAskPrice));
		impl.setBestPublicAskSize(entry.bestPublicAskSize);
		impl.setBestPublicBidPrice(fmStruct(entry.bestPublicBidPrice));
		impl.setBestPublicBidSize(entry.bestPublicBidSize);
		impl.setUnderlyingLastSalePrice(fmStruct(entry.underlyingLastSalePrice));
		impl.setSessionName(entry.sessionName);
		impl.setNbboAskPrice(fmStruct(entry.nbboAskPrice));
		impl.setNbboBidPrice(fmStruct(entry.nbboBidPrice));
		impl.setNBBOAskExchange(fmStruct(entry.nbboAskExchange));
		impl.setNBBOBidExchange(fmStruct(entry.nbboBidExchange));
		impl.setBotrAskPrice(fmStruct(entry.botrAskPrice));
		impl.setBotrBidPrice(fmStruct(entry.botrBidPrice));
		impl.setBOTRAskExchange(fmStruct(entry.botrAskExchange));
		impl.setBOTRBidExchange(fmStruct(entry.botrBidExchange));
		impl.setBestPublicAskSize(entry.bestPublicAskSize);
		impl.setBestPublicBidSize(entry.bestPublicBidSize);
		impl.setBestPublicCustomerAskSize(entry.bestPublicCustomerAskSize);
		impl.setBestPublicCustomerBidSize(entry.bestPublicCustomerBidSize);
		impl.setPhysicalLocation(entry.physicalLocation);
		impl.setDayOfWeek(entry.dayOfWeek);
		impl.setDBId(entry.databaseIdentifier);
		impl.setExchangeIndicators(entry.exchangeIndicators, entry.productState);
		impl.setTradeServerId(entry.tradeServerId);
		return impl;
	}

	private MarketDataHistoryEntry fromCORBAStruct(MarketDataHistoryPriceReportStruct entry)
	{
		MarketDataHistoryEntryImpl impl = this;

		impl.setBypassReflectionFlag(true);
		impl.setProductKey(entry.productKey);
		impl.setEntryType(entry.entryType);
		impl.setEntryTime(entry.entryTime);
		impl.setBidPrice(fmStruct(entry.bidPrice));
		impl.setBidSize(entry.bidSize);
		impl.setBestPublicAskPrice(fmStruct(entry.bestPublicAskPrice));
		impl.setBestPublicBidPrice(fmStruct(entry.bestPublicBidPrice));
		impl.setLastSalePrice(fmStruct(entry.lastSalePrice));
		impl.setLastSaleVolume(entry.lastSaleVolume);
		impl.setUnderlyingLastSalePrice(fmStruct(entry.underlyingLastSalePrice));
		impl.setProductState(entry.productState);
		impl.setTickerPrefix(entry.tickerPrefix);
		impl.setSessionName(entry.sessionName);
		impl.setOverrideIndicator(entry.overrideIndicator);
		impl.setNbboAskPrice(fmStruct(entry.nbboAskPrice));
		impl.setNbboBidPrice(fmStruct(entry.nbboBidPrice));
		impl.setNBBOAskExchange(fmStruct(entry.nbboAskExchange));
		impl.setNBBOBidExchange(fmStruct(entry.nbboBidExchange));
		impl.setBotrAskPrice(fmStruct(entry.botrAskPrice));
		impl.setBotrBidPrice(fmStruct(entry.botrBidPrice));
		impl.setBOTRAskExchange(fmStruct(entry.botrAskExchange));
		impl.setBOTRBidExchange(fmStruct(entry.botrBidExchange));
		impl.setBestPublicCustomerAskSize(entry.bestPublicCustomerAskSize);
		impl.setBestPublicCustomerBidSize(entry.bestPublicCustomerBidSize);
		impl.setTradeThroughIndicator(entry.tradeThroughIndicator);
		impl.setExchangeIndicators(fmStruct(entry.exchangeIndicators), entry.productState);
		impl.setBroker(fmStruct(entry.broker));
		impl.setContra(fmStruct(entry.contra));
		impl.setPhysicalLocation(entry.physicalLocation);
		impl.setDayOfWeek(entry.dayOfWeek);
		impl.setTradeID(entry.tradeID);
		impl.setDBId(entry.databaseIdentifier);
		impl.setTradeServerId(entry.tradeServerId);
		return impl;
	}

	private MarketDataHistoryEntry fromCORBAStruct(MarketDataHistoryMarketConditionEntryStruct entry)
	{
		MarketDataHistoryEntryImpl impl = this;

		impl.setBypassReflectionFlag(true);
		impl.setProductKey(entry.productKey);
		impl.setEntryType(entry.entryType);
		impl.setEntryTime(entry.entryTime);
		impl.setUnderlyingLastSalePrice(fmStruct(entry.underlyingLastSalePrice));
		impl.setImbalanceQuantity(entry.imbalanceQuantity);
		impl.setProductState(entry.productState);
		impl.setSessionName(entry.sessionName);
		impl.setDayOfWeek(entry.dayOfWeek);
		impl.setTradeID(entry.tradeID);
		impl.setDBId(entry.databaseIdentifier);
		impl.setTradeServerId(entry.tradeServerId);
		return impl;
	}

	private MarketDataHistoryEntry fromCORBAStruct(MarketDataHistoryExpectedOpeningPriceStruct entry)
	{
		MarketDataHistoryEntryImpl impl = this;

		impl.setBypassReflectionFlag(true);
		impl.setProductKey(entry.productKey);
		impl.setEntryType(entry.entryType);
		impl.setEntryTime(entry.entryTime);
		impl.setLastSalePrice(fmStruct(entry.lastSalePrice));
		impl.setUnderlyingLastSalePrice(fmStruct(entry.underlyingLastSalePrice));
		impl.setEopType(entry.eopType);
		impl.setImbalanceQuantity(entry.imbalanceQuantity);
		impl.setProductState(entry.productState);
		impl.setSessionName(entry.sessionName);
		impl.setDayOfWeek(entry.dayOfWeek);
		impl.setTradeID(entry.tradeID);
		impl.setDBId(entry.databaseIdentifier);
		impl.setTradeServerId(entry.tradeServerId);
		return impl;
	}
}
