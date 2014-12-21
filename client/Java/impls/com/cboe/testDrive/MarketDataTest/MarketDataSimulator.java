package com.cboe.testDrive.MarketDataTest;


import com.cboe.domain.util.CurrentMarketContainerImpl;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.consumers.CurrentMarketConsumer;
import com.cboe.idl.consumers.RecapConsumer;
import com.cboe.idl.consumers.TickerConsumer;
import com.cboe.idl.events.CurrentMarketEventConsumerHelper;
import com.cboe.idl.events.RecapEventConsumerHelper;
import com.cboe.idl.events.TickerEventConsumerHelper;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationServiceFileImpl;
import com.cboe.interfaces.businessServices.TradingSessionService;
import com.cboe.interfaces.businessServices.TradingSessionServiceHome;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/***
 * @author Jing Chen
 ****/

public class MarketDataSimulator
{

    private ConfigurationService configurationService;
    private CurrentMarketConsumer currentMarketPublisher;
    private RecapConsumer recapPublisher;
    private TickerConsumer tickerPublisher;
    private Map productClasses;
    private ArrayList underlyingProducts;

    private Map currentMarketBlockPublishingRate;
    private Map currentMarketBlocks;
    private Map cmPublicMarketBlocks;
    private int publishCurrentMarketPerInterval;
    private double cmPublicMarketRate;
    private static String NUMBER_OF_BEST_MARKET_BLOCKS_PER_SECOND = "numberOfBestMarketBlocks";
    private static String NUMBER_OF_PUBLIC_MARKET_BLOCKS_PER_SECOND = "numberOfPublicMarketBlocks";
    private Calendar theCalendar;
    private NBBOStruct[] defaultNBBOs = new NBBOStruct[0];
    private CurrentMarketStruct[] defaultMarkets = new CurrentMarketStruct[0];
    private CurrentMarketStructV2[] defaultV2Markets = new CurrentMarketStructV2[0];
    private long cmPublishSleepInterval;

    private TradingSessionService tradingSessionService;

    private int publishUnderlyingRecapPerInterval;
    private long publishUnderlyingRecapSleepInterval;
    private ArrayList underlyingRecapStructs;

    private int publishUnderlyingTickerPerInterval;
    private long publishUnderlyingTickerSleepInterval;
    private ArrayList underlyingTickerStructs;
    private int cmOverlayBugFixMode = 0;

    private void initialize(String[] args) throws Exception
    {
        FoundationFramework ff = FoundationFramework.getInstance();
        configurationService = new ConfigurationServiceFileImpl();
        configurationService.initialize(args, 0);
        ff.initialize("MarketDataSimulator", configurationService);
        EventService eventService = FoundationFramework.getInstance().getEventService();

        String currentMarketChannelName = configurationService.getProperty("MarketDataSimulator.CurrentMarketChannelName");
        currentMarketPublisher = CurrentMarketEventConsumerHelper.narrow(eventService.getEventChannelSupplierStub(currentMarketChannelName, CurrentMarketEventConsumerHelper.id()));
        if ( currentMarketPublisher == null )
        {
            System.out.println( "Could not connecet to " + currentMarketChannelName + " event channel" );
        }

        String recapChannelName = configurationService.getProperty("MarketDataSimulator.RecapChannelName");
        recapPublisher = RecapEventConsumerHelper.narrow(eventService.getEventChannelSupplierStub(recapChannelName, RecapEventConsumerHelper.id()));
        if ( recapPublisher == null )
        {
            System.out.println( "Could not connecet to " + recapChannelName + " event channel" );
        }

        String tickerChannelName = configurationService.getProperty("MarketDataSimulator.TickerChannelName");
        tickerPublisher = TickerEventConsumerHelper.narrow(eventService.getEventChannelSupplierStub(tickerChannelName, TickerEventConsumerHelper.id()));
        if ( tickerPublisher == null )
        {
            System.out.println( "Could not connecet to " + tickerChannelName + " event channel" );
        }

        TradingSessionServiceHome tradingSessionServiceHome = (TradingSessionServiceHome) HomeFactory.getInstance().findHome(TradingSessionServiceHome.HOME_NAME);
        tradingSessionService = tradingSessionServiceHome.find();

        theCalendar = Calendar.getInstance();
        productClasses = new HashMap();
        currentMarketBlockPublishingRate = new HashMap();
        currentMarketBlocks = new HashMap();
        cmPublicMarketBlocks = new HashMap();

        underlyingRecapStructs = new ArrayList();
        underlyingTickerStructs = new ArrayList();
        underlyingProducts = new ArrayList();

        initializeProductClasses(configurationService.getProperty("MarketDataSimulator.ProductFileName"));
    }

    public void initCM() throws Exception
    {
        initializeCMPublishingParams();
        initializeCMStructs();
    }

    public void initUnderlyingRecaps() throws Exception
    {
        initializeUnderlyingRecapPublishingParams();
        initializeUnderlyingRecapStructs();
    }

    public void initUnderlyingTickers() throws Exception
    {
        initializeUnderlyingTickerPublishingParams();
        initializeUnderlyingTickerStructs();
    }

    public static void main( String[] args )
    {
        MarketDataSimulator marketDataSimulator = new MarketDataSimulator();
        try
        {
            marketDataSimulator.initialize(args);
            if (args[1].equals("-CM"))
            {
                marketDataSimulator.initCM();
                System.out.print("Press <enter> to start");
                System.in.read();
                marketDataSimulator.publishCM();
            }
            else if (args[1].equals("-UR"))
            {
                marketDataSimulator.initUnderlyingRecaps();
                System.out.print("Press <enter> to start");
                System.in.read();
                marketDataSimulator.publishUnderlyingRecap();
            }
            else if (args[1].equals("-UT"))
            {
                marketDataSimulator.initUnderlyingTickers();
                System.out.print("Press <enter> to start");
                System.in.read();
                marketDataSimulator.publishUnderlyingTicker();
            }
            else
            {
                System.out.println("Unknown switch.");
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    private void initializeProductClasses(String aProductsFile) throws Exception
    {
        FileReader aFileReader;
        BufferedReader aBufferedReader;
        StringTokenizer aStringTokenizer;
        String line;
        int classKey;
        int productKey;
        String sessionName;

        aFileReader = new FileReader(aProductsFile);
        aBufferedReader = new BufferedReader(aFileReader);
        while (( line = aBufferedReader.readLine()) != null)
        {
            aStringTokenizer = new StringTokenizer(line, ",");
            if (aStringTokenizer.hasMoreTokens())
            {
               classKey = Integer.parseInt(aStringTokenizer.nextToken());
            }
            else
            {
               throw new Exception("missing classKey: " + aProductsFile);
            }
            if (aStringTokenizer.hasMoreTokens())
            {
               productKey = Integer.parseInt(aStringTokenizer.nextToken());
            }
            else
            {
               throw new Exception("missing a productKey: " + aProductsFile);
            }
            if (aStringTokenizer.hasMoreTokens())
            {
               sessionName = aStringTokenizer.nextToken();
            }
            else
            {
               throw new Exception("missing a sessionName: " + aProductsFile);
            }
            System.out.println(classKey + " " + productKey + " " + sessionName);
            getProductListForClass(new SessionKeyContainer(sessionName, classKey)).add(new ProductKeysStruct(productKey, classKey, (short)7, 11111));
        }

        Iterator i = productClasses.keySet().iterator();
        while(i.hasNext())
        {
            SessionKeyContainer sessionKey = (SessionKeyContainer)i.next();
            ProductKeysStruct underlyingProduct = (tradingSessionService.getClassBySessionForKey(sessionKey.getSessionName(), sessionKey.getKey())).classStruct.underlyingProduct.productKeys;
            underlyingProducts.add(underlyingProduct);
        }
    }

    private ArrayList getProductListForClass(SessionKeyContainer sessionKey)
    {
        ArrayList productList = (ArrayList)productClasses.get(sessionKey);
        if(productList == null)
        {
            productList = new ArrayList(11);
            productClasses.put(sessionKey, productList);
        }
        return productList;
    }

    private void initializeUnderlyingTickerPublishingParams() throws Exception
    {
        publishUnderlyingTickerPerInterval = Integer.parseInt(configurationService.getProperty("MarketDataSimulator.UnderlyingTicker.PublishTickerPerInterval"));
        publishUnderlyingTickerSleepInterval = Integer.parseInt(configurationService.getProperty("MarketDataSimulator.UnderlyingTicker.PublishTickerInterval"));
    }


    private void initializeUnderlyingRecapPublishingParams() throws Exception
    {
        publishUnderlyingRecapPerInterval = Integer.parseInt(configurationService.getProperty("MarketDataSimulator.UnderlyingRecap.PublishRecapPerInterval"));
        publishUnderlyingRecapSleepInterval = Long.parseLong(configurationService.getProperty("MarketDataSimulator.UnderlyingRecap.PublishRecapInterval"));
    }

    private void initializeCMPublishingParams() throws Exception
    {
        publishCurrentMarketPerInterval = Integer.parseInt(configurationService.getProperty("MarketDataSimulator.CM.PublishCurrentMarketPerInterval"));
        cmPublicMarketRate = Double.parseDouble(configurationService.getProperty("MarketDataSimulator.CM.PublicMarketRate"));
        cmPublishSleepInterval = Long.parseLong(configurationService.getProperty("MarketDataSimulator.CM.PublishInterval"));
        try 
        {
        	cmOverlayBugFixMode = Integer.parseInt(configurationService.getProperty("MarketDataSimulator.CM.OverlayBugFixMode"));
        	System.out.println("MarketDataSimulator.CM.OverlayBugFixMode = " + cmOverlayBugFixMode);
        }
        catch (Exception ex) 
        {
        	
        }
        if(cmPublicMarketRate > 1)
        {
            throw new Exception("public market data rate is not less than 1");
        }
        String cmBlockSizes = configurationService.getProperty("MarketDataSimulator.CM.CurrentMarketPublishBlockSizes");
        String cmPubDistributions = configurationService.getProperty("MarketDataSimulator.CM.CurrentMarketPubDistPerBlockSize");
        StringTokenizer blockSizeStringTokenizer = new StringTokenizer(cmBlockSizes, ",");
        StringTokenizer pubDistStringTokenizer = new StringTokenizer(cmPubDistributions, ",");
        while (blockSizeStringTokenizer.hasMoreTokens())
        {
            Integer blockSize = new Integer(blockSizeStringTokenizer.nextToken());
            double distribution;
            if(pubDistStringTokenizer.hasMoreTokens())
            {
                distribution = Double.parseDouble(pubDistStringTokenizer.nextToken());
            }
            else
            {
                throw new NullPointerException("no distribution rate matches the block size of "+blockSize.intValue());
            }
            //TODO:need to add error protection here and see if disbution is greater than 1.
            int numberOfBestMarketBlocksPerSec= (int)((publishCurrentMarketPerInterval * distribution / blockSize.intValue()) * (1-cmPublicMarketRate));
            System.out.println(numberOfBestMarketBlocksPerSec + " Best Market Blocks for block size of "+blockSize.intValue()+ " will be published per "+cmPublishSleepInterval+"ms");
            getMapForBlockSize(blockSize).put(NUMBER_OF_BEST_MARKET_BLOCKS_PER_SECOND, new Integer(numberOfBestMarketBlocksPerSec));
            int numberOfPublicMarketBlocksPerSec = (int)((publishCurrentMarketPerInterval * distribution / blockSize.intValue()) * cmPublicMarketRate);
            System.out.println(numberOfPublicMarketBlocksPerSec + " Public Market Blocks for block size of "+blockSize.intValue()+" will be published per " + cmPublishSleepInterval +"ms");
            getMapForBlockSize(blockSize).put(NUMBER_OF_PUBLIC_MARKET_BLOCKS_PER_SECOND, new Integer(numberOfPublicMarketBlocksPerSec));
        }
    }

    private Map getMapForBlockSize(Integer blockSize)
    {
        Map temp = (Map)currentMarketBlockPublishingRate.get(blockSize);
        if(temp == null)
        {
            temp = new HashMap();
            currentMarketBlockPublishingRate.put(blockSize,temp);
        }
        return temp;
    }

    private void initializeUnderlyingRecapStructs() throws Exception
    {
        Iterator i = underlyingProducts.iterator();
        ProductKeysStruct productKeys;
        String sessionName = "Underlying";
        int lastSaleVolume = 20;
        int totalVolume = 50;
        char tickDirection = 'B';
        char netChangeDirection = 'A';
        char bidDirection = 'B';
        int bidSize = 10;
        int askSize = 20;
        String recapPrefix = "RECAP";
        int openInterest = 1;
        boolean isOTC = true;
        PriceStruct price = new PriceStruct((short)1, 5, 10);
        DateStruct date = new DateStruct((byte)1,(byte)20,(short)2008);
        ProductNameStruct productName = new ProductNameStruct("AAA",price,date,'O',"AAA");
        TimeStruct time = new TimeStruct((byte)12, (byte)12, (byte)12, (byte)0);
        while (i.hasNext())
        {
            productKeys = (ProductKeysStruct)i.next();
            RecapStruct recap = new RecapStruct(productKeys, sessionName, productName, price, time, lastSaleVolume, totalVolume,
                    tickDirection, netChangeDirection, bidDirection, price, price, bidSize, time, price, askSize, time, recapPrefix,
                    price, price, price, price, price, openInterest, price, isOTC);
            underlyingRecapStructs.add(recap);
        }
    }

    private void initializeUnderlyingTickerStructs() throws Exception
    {
        Iterator i = underlyingProducts.iterator();
        ProductKeysStruct productKeys = null;
        String sessionName = "Underlying";
        String exchangeSymbol = "AAA";
        String salePrefix = "SALEPrefix";
        PriceStruct lastSalePrice = new PriceStruct((short)1, 3, 20);
        int lastSaleVolume = 10;
        String salePostfix = "SALEPostfix";
        while (i.hasNext())
        {
            productKeys = (ProductKeysStruct)i.next();
            TickerStruct ticker = new TickerStruct(productKeys,sessionName, exchangeSymbol, salePrefix, lastSalePrice, lastSaleVolume, salePostfix);
            underlyingTickerStructs.add(ticker);
        }
    }

    private void initializeCMStructs() throws Exception
    {
        Iterator i = productClasses.values().iterator();
        Iterator blockSizeIterator = currentMarketBlockPublishingRate.keySet().iterator();
        int classInterateCount = 0;
        MarketVolumeStruct marketVolumeStructs[] = new MarketVolumeStruct[1];
        marketVolumeStructs[0] = new MarketVolumeStruct((short) 1, 100, false);
        TimeStruct timeStruct = new TimeStruct((byte) 12, (byte) 12, (byte) 12, (byte) 0);
        MarketVolumeStruct publicMarketVolumeStructs[] = new MarketVolumeStruct[2];
        publicMarketVolumeStructs[0] = new MarketVolumeStruct((short) 6, 20, false);
        publicMarketVolumeStructs[1] = new MarketVolumeStruct((short) 7, 20, false);
        while (blockSizeIterator.hasNext())
        {
            int blockSize = ((Integer)blockSizeIterator.next()).intValue();
            classInterateCount = 0;
            boolean productsRemaining = true;
            while (productsRemaining)
            {
                i=productClasses.values().iterator();
                classInterateCount++;
                while(i.hasNext())
                {
                    ArrayList products = (ArrayList)i.next();
                    if(blockSize*classInterateCount <= products.size())
                    {
                        CurrentMarketStruct[] currentMarketStructs = new CurrentMarketStruct[blockSize];
                        int k = 0;
                        for (int j=blockSize*classInterateCount-1; j>=blockSize*(classInterateCount-1); j--)
                        {
                            currentMarketStructs[k] = new CurrentMarketStruct((ProductKeysStruct)products.get(j), "W_MAIN",
                                    "CBOE", PriceFactory.create(1.00).toStruct(),marketVolumeStructs, true, PriceFactory.create(1.20).toStruct(),
                                    marketVolumeStructs, true, timeStruct, true);
 			                k++;
                        }
                        CurrentMarketStruct[] publicMarketStructs = new CurrentMarketStruct[blockSize];
                        k = 0;
                        for (int j=blockSize*classInterateCount-1; j>=blockSize*(classInterateCount-1); j--)
                        {
                            publicMarketStructs[k] = new CurrentMarketStruct((ProductKeysStruct)products.get(j), "W_MAIN",
                                    "CBOE", PriceFactory.create(1.00).toStruct(),publicMarketVolumeStructs, true, PriceFactory.create(1.20).toStruct(),
                                    publicMarketVolumeStructs, true, timeStruct, true);
                            k++;
                        }
                        CurrentMarketContainerImpl currentMarketContainer = new CurrentMarketContainerImpl(currentMarketStructs, publicMarketStructs);
                        getCMBlocksForBlockSize(blockSize).add(currentMarketStructs);
                        getPublicCMBlocksForBlockSize(blockSize).add(currentMarketContainer);
                        productsRemaining = true;
                    }
                    else
                    {
                        productsRemaining = false;
                    }
                }
            }
        }
    }

    private ArrayList getCMBlocksForBlockSize(int blockSize)
    {
        ArrayList temp = (ArrayList)currentMarketBlocks.get(new Integer(blockSize));
        if(temp == null)
        {
            temp = new ArrayList(11);
            currentMarketBlocks.put(new Integer(blockSize), temp);
        }
        return temp;
    }

    private ArrayList getPublicCMBlocksForBlockSize(int blockSize)
    {
        ArrayList temp = (ArrayList)cmPublicMarketBlocks.get(new Integer(blockSize));
        if(temp == null)
        {
            temp = new ArrayList(11);
            cmPublicMarketBlocks.put(new Integer(blockSize), temp);
        }
        return temp;
    }

    public void publishUnderlyingRecap()
    {
        long startTime = System.currentTimeMillis() ;
        long currentTime = startTime;
        long wakeUpTime = 0;
        long sleepTime = 0;
        int count = 0;
        RecapStruct[] recaps = new RecapStruct[1];
        int j = 0;
        int size = recaps.length;
        while(true)
        {
            count = 0;
            while(count<publishUnderlyingRecapPerInterval)
            {
                j = count%size;
                RecapStruct recap = (RecapStruct)underlyingRecapStructs.get(j);
                recaps[0] = recap;
                recapPublisher.acceptRecapForClass(new RoutingParameterStruct(new int[0], recap.sessionName, recap.productKeys.classKey,(short)0),recaps);
                count++;
            }
            currentTime = System.currentTimeMillis();
            if (currentTime - startTime < publishUnderlyingRecapSleepInterval)
            {
                sleepTime = publishUnderlyingRecapSleepInterval - (currentTime - startTime);
                System.out.println("ready to sleep "+sleepTime+"ms for underlying recap");
                try
                {
                    Thread.currentThread().sleep(sleepTime);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                wakeUpTime = System.currentTimeMillis();
            }
            else
            {
                throw new RuntimeException("simulator could not keep up with the publish rate:"+publishUnderlyingRecapPerInterval);
            }
	        System.out.println("publish rate:"+(count*publishUnderlyingRecapSleepInterval)/(wakeUpTime-startTime)+"/"+publishUnderlyingRecapSleepInterval+"ms");
            startTime = System.currentTimeMillis();
        }
    }

    public void publishUnderlyingTicker()
    {
        long startTime = System.currentTimeMillis() ;
        long currentTime = startTime;
        long wakeUpTime = 0;
        long sleepTime = 0;
        int count = 0;
        TickerStruct[] tickers = new TickerStruct[1];
        TimeStruct[] times = new TimeStruct[1];
        int j = 0;
        int size = tickers.length;
        TimeStruct timeStruct = new TimeStruct((byte) 12, (byte) 12, (byte) 12, (byte) 0);
        times[0] = timeStruct;
        while(true)
        {
            count = 0;
            while(count<publishUnderlyingTickerPerInterval)
            {
                j = count%size;
                TickerStruct ticker = (TickerStruct)underlyingTickerStructs.get(j);
                tickers[0] = ticker;

                tickerPublisher.acceptTickerForClass(new RoutingParameterStruct(new int[0], ticker.sessionName, ticker.productKeys.classKey,(short)0),times, tickers);
                count++;
            }
            currentTime = System.currentTimeMillis();
            if (currentTime - startTime < publishUnderlyingTickerSleepInterval)
            {
                sleepTime = publishUnderlyingTickerSleepInterval - (currentTime - startTime);
                System.out.println("ready to sleep "+sleepTime+"ms for underlying ticker");
                try
                {
                    Thread.currentThread().sleep(sleepTime);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                wakeUpTime = System.currentTimeMillis();
            }
            else
            {
                throw new RuntimeException("simulator could not keep up with the publish rate:"+publishUnderlyingRecapPerInterval);
            }
	        System.out.println("publish rate:"+(count*publishUnderlyingTickerSleepInterval)/(wakeUpTime-startTime)+"/"+publishUnderlyingTickerSleepInterval+"ms");
            startTime = System.currentTimeMillis();
        }
    }

    private CurrentMarketStruct[] getPublicMarketsForOverlayBug(CurrentMarketStruct[] bestPublicMarketStructs) 
    {
    	int currentSize = bestPublicMarketStructs.length;
    	int newSize = 3;
    	if (currentSize < 5)
    		newSize = 1;
    	int toSkip = currentSize - newSize;
    	CurrentMarketStruct[] modifiedPublicMarkets = new CurrentMarketStruct[newSize];
    	int j=0;
    	for (int i=0; i < currentSize; ++i) {
    		if (i < toSkip)
    			continue;
    		modifiedPublicMarkets[j++] = bestPublicMarketStructs[i];

    	}
    	return modifiedPublicMarkets;
    }
    

    public void publishCM()
    {
        long startTime = System.currentTimeMillis() ;
        long currentTime = startTime;
        int counter = 0;
        long wakeUpTime = 0;
        long sleepTime = 0;
        while (true)
        {
            theCalendar.setTime(new Date(startTime));
            int totalCount = 0;
            Iterator iterator = currentMarketBlockPublishingRate.keySet().iterator();
            StringBuffer printOut;
            while(iterator.hasNext())
            {
                Integer blockSize = ((Integer)iterator.next());
                int blockLimit = ((Integer)getMapForBlockSize(blockSize).get(NUMBER_OF_BEST_MARKET_BLOCKS_PER_SECOND)).intValue();
                int count = 0;
                int j = 0;
                int blocks = 0;
                printOut = new StringBuffer("");
                
                printOut.append("publish block size:"+blockSize+"      # of CM blocks:"+blockLimit);
                while(count < blockLimit)
                {
                    blocks = (getCMBlocksForBlockSize(blockSize.intValue())).size();
		            j = count%blocks;
                    CurrentMarketStruct[] bestMarket = (CurrentMarketStruct[])getCMBlocksForBlockSize(blockSize.intValue()).get(j);
                    int classKey = bestMarket[0].productKeys.classKey;
                    currentMarketPublisher.acceptCurrentMarketsForClass(new RoutingParameterStruct(new int[0],
                            bestMarket[0].sessionName, classKey,(short)0),
                            bestMarket,defaultMarkets,defaultNBBOs,defaultV2Markets,defaultMarkets,defaultMarkets,new boolean[0]);
                    counter+=blockSize.intValue();
                    count++;
                    Collections.shuffle(getCMBlocksForBlockSize(blockSize.intValue()));
                }
                
                totalCount+=count;
                blockLimit = ((Integer)getMapForBlockSize(blockSize).get(NUMBER_OF_PUBLIC_MARKET_BLOCKS_PER_SECOND)).intValue();
                count = 0;
                j = 0;
                printOut.append("      # of Public CM blocks:"+blockLimit);
                while(count < blockLimit)
                {
                    j = count%getPublicCMBlocksForBlockSize(blockSize.intValue()).size();
                    CurrentMarketStruct[] bestMarket = ((CurrentMarketContainerImpl)getPublicCMBlocksForBlockSize(blockSize.intValue()).get(j)).getBestMarkets();                    
                    CurrentMarketStruct[] bestPublicMarket = ((CurrentMarketContainerImpl)getPublicCMBlocksForBlockSize(blockSize.intValue()).get(j)).getBestPublicMarketsAtTop();
                    if (cmOverlayBugFixMode == 0) // normal mode
                    {
	                    currentMarketPublisher.acceptCurrentMarketsForClass(new RoutingParameterStruct(new int[0],
	                            bestMarket[0].sessionName, bestMarket[0].productKeys.classKey,(short)0),
	                            bestMarket,defaultMarkets,defaultNBBOs,defaultV2Markets,defaultMarkets,bestPublicMarket,new boolean[0]);
                    }
                    else
                    {
                    	// for overlay bug fix testing.
	                    currentMarketPublisher.acceptCurrentMarketsForClass(new RoutingParameterStruct(new int[0],
	                            bestMarket[0].sessionName, bestMarket[0].productKeys.classKey,(short)0),
	                            bestMarket,defaultMarkets,defaultNBBOs,defaultV2Markets,defaultMarkets,
	                            getPublicMarketsForOverlayBug(bestPublicMarket),new boolean[0]);
                    }
                    counter+=blockSize.intValue();
                    count++;
                    Collections.shuffle(getPublicCMBlocksForBlockSize(blockSize.intValue()));
                }
                System.out.println(printOut);
                totalCount+=count;
            }
            System.out.println("# of market data published:"+counter);
            System.out.println("# of market data blocks published:"+totalCount);
            currentTime = System.currentTimeMillis();
            if (currentTime - startTime < cmPublishSleepInterval)
            {
                sleepTime = cmPublishSleepInterval - (currentTime - startTime);
                System.out.println("ready to sleep "+sleepTime+"ms.");
                try
                {
                    Thread.currentThread().sleep(sleepTime);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                wakeUpTime = System.currentTimeMillis();
            }
            else
            {
                throw new RuntimeException("simulator could not keep up with the publish rate:"+publishCurrentMarketPerInterval);
            }
            System.out.println("publish rate:"+(counter*cmPublishSleepInterval)/(wakeUpTime-startTime)+"/"+cmPublishSleepInterval+"ms");
            counter=0;
            startTime = System.currentTimeMillis();
        }
    }
}
