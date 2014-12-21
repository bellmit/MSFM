package com.cboe.application.eventChannel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.cboe.application.marketData.ClientMarketUpdateImpl;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.businessServices.marketDataService.marketUpdate.MarketUpdateHelper;
import com.cboe.externalIntegrationServices.msgCodec.DataBufferBlock;
import com.cboe.idl.cmiConstants.CurrentMarketViewTypes;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.CurrentMarketStructV2;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.MarketUpdate;
import com.cboe.interfaces.events.CurrentMarketConsumer;
import com.cboe.interfaces.events.IECCurrentMarketConsumerHome;
import com.cboe.interfaces.events.MarketBufferConsumer;
import com.cboe.interfaces.internalBusinessServices.ProductConfigurationService;
import com.cboe.server.marketBuffer.MarketDataBuffer;
import com.cboe.server.marketBuffer.codec.MarketUpdateCodec;
import com.cboe.server.marketBuffer.codec.MarketUpdateCodecHelper;
import com.cboe.server.marketBuffer.codec.MarketUpdateEOPCodec;

/**
 * MarketBuffer listener object listens on the CBOE event channel.
 * There are multiple MarketBuffer listener objects per client, one per channel.
 */
public class MarketBufferConsumerIECImpl extends BObject implements MarketBufferConsumer
{
    private MarketUpdateMarketBufferProcessingThread theMktUpdateProcessingThread;

    //private final DataBufferBlock blockHandler = new DataBufferBlock(128);
    // Changed the above variable to thread local....Arun Oct 11 2010
    private static final ThreadLocal<DataBufferBlock> blockHandlerTL = new ThreadLocal<DataBufferBlock>() {
		@Override
		public DataBufferBlock initialValue() {
			return new DataBufferBlock(128);
		}
	};

// private int channelIdx;
    private boolean logMessages;

    private IECCurrentMarketConsumerHome theCurrentMarketConsumerHome;
    private CurrentMarketConsumer theCurrentMarketConsumer; 

    private static final NBBOStruct[] EMPTY_NBBO = new NBBOStruct[0];
    private static final CurrentMarketStructV2[] EMPTY_MKTS = new CurrentMarketStructV2[0];

    protected ProductConfigurationService productConfigurationService;

    MarketBufferConsumerIECImpl()
    {
        //this.channelIdx = p_channelIdx;
        logMessages = false;
        theCurrentMarketConsumerHome = ServicesHelper.getCurrentMarketConsumerHome();
        initThread();
    }
    protected ProductConfigurationService getProductConfigurationService()
    {
        if (productConfigurationService == null )
        {
            productConfigurationService = com.cboe.application.shared.ServicesHelper.getProductConfigurationService();
        }
        return productConfigurationService;
    }

    private void initThread()
    {
        this.theMktUpdateProcessingThread =
                new MarketUpdateMarketBufferProcessingThread(new LinkedBlockingQueue());
        this.theMktUpdateProcessingThread.start();
    }

    public void acceptMarketBuffer(int p_groupKey, int p_subIdentifier, byte[] p_buffer)
    {
        // Read MarketDataBuffers from p_buffer block and places them in bufferList array list.
        // NOTE: this method not thread safe so it should not be used concurrently.
        ArrayList<MarketDataBuffer> bufferList = new ArrayList<MarketDataBuffer>(16);
        getReadBuffersFromBlock(p_buffer, p_buffer.length, null, bufferList);

        if (logMessages)
        {
            String hexBuffer = convertToHex(p_buffer);
            StringBuffer mybuffer = new StringBuffer(83+hexBuffer.length());
            mybuffer.append("acceptMarketBuffer groupKey:").
                    append(p_groupKey).
                    append(" subIdentifier:").
                    append(p_subIdentifier).
                    append(" buffer[").
                    append(p_buffer.length).
                    append("]:").
                    append(hexBuffer);
            Log.information(this, mybuffer.toString());
        }

        final int len = bufferList.size();
        if (len==0)
        {
            return; // No more processing necessary.
        }

        try
        {
            enqueue(bufferList);
        }
        catch (InterruptedException e)
        {
            Log.exception(this, e);
        }
    }

    /**
     * Read buffers from a block.  Note that this implementation uses instance fields
     * to manage parsing and state, therefore the user of this method must ensure that
     * it is not being used concurrently from multiple threads.
     */

    private void getReadBuffersFromBlock(final byte[] p_encodedBuffers, final int p_encodedBufferLen,
            final ArrayList<MarketDataBuffer> p_reuseBufferList,
            final ArrayList<MarketDataBuffer> p_outList)
    {
        // blockHandler.setStorage(p_encodedBuffers, p_encodedBufferLen);
        // change the above code to use a thread local instance.
        DataBufferBlock blockHandler = blockHandlerTL.get();
        blockHandler.setStorage(p_encodedBuffers, p_encodedBufferLen);
        short bufId;
        while ((bufId = blockHandler.nextBufferId()) != -1)
        {
            if (bufId == MarketDataBuffer.BUFFER_ID)
            {
                MarketDataBuffer newReadBuffer = (p_reuseBufferList != null && !p_reuseBufferList
                        .isEmpty())
                        ? p_reuseBufferList.remove(p_reuseBufferList.size() - 1)
                        : new MarketDataBuffer();
                newReadBuffer.reset();
                try
                {
                    blockHandler.read(newReadBuffer);
                    p_outList.add(newReadBuffer);
                }
                catch (IOException io)
                {
                    Log.alarm("MarketBufferConsumerIECImpl getReadBuffersFromBlock:MsgCodecIOException:" + io);
                    blockHandler.skip();
                }
                    
            }
            else
            {
                blockHandler.skip();
            }
        }
    }

    void enqueue(ArrayList<MarketDataBuffer> bufferList) throws InterruptedException
    {
        this.theMktUpdateProcessingThread.enqueue(bufferList);
    }

    private final class MarketUpdateMarketBufferProcessingThread extends Thread
    {
        final BlockingQueue<ArrayList<MarketDataBuffer>> queue;
        final MarketUpdateCodec fullCodec = new MarketUpdateCodec();
        final MarketUpdateEOPCodec eopCodec = new MarketUpdateEOPCodec();

        MarketUpdateMarketBufferProcessingThread(BlockingQueue<ArrayList<MarketDataBuffer>> p_queue)
        {
            this.queue = p_queue;
        }

        public void enqueue(ArrayList<MarketDataBuffer> bufferList) throws InterruptedException
        {
            this.queue.put(bufferList);
        }

        public void run()
        {
            while (true)
            {
                try
                {
                    processMarketDataBufferList(queue.take(), this);
                }
                catch (Exception e)
                {
                    Log.exception(e);
                }
            }
        }
    }

    void processMarketDataBufferList(final ArrayList<MarketDataBuffer> p_bufs, MarketUpdateMarketBufferProcessingThread pt)
    {
        final int ttl = p_bufs.size();
        for (int i = 0; i < ttl; i++)
        {
            final MarketDataBuffer buf = p_bufs.get(i);
            try
            {
                int codecId;
                int previousCodecId;

                MarketUpdate mktUpdate;
                ExpectedOpeningPriceStruct eop;

                ArrayList<MarketUpdate> myMktUpdateList = new ArrayList();
                ArrayList<ExpectedOpeningPriceStruct> myEOPList = new ArrayList();

                int myClassKey=buf.getClassKey();
                buf.rewind();

                //get the first codec in marketbuffer
                //Do I want to make this switch block into a method? it looks nice but affect performance because put 
                //method arguemnt into stack takes time and memory.
                codecId = buf.nextCodecId();
                switch (codecId)
                {
                    case MarketUpdateCodec.CODEC_ID:
                        mktUpdate = decodeNext(buf, pt.fullCodec);
                        if (mktUpdate==null)
                        {
                            buf.skip();
                        }
                        myMktUpdateList.add(mktUpdate);
                        break;
                    case MarketUpdateEOPCodec.CODEC_ID:
                        eop = decodeNext(buf, pt.eopCodec);
                        if (eop==null)   // ignore empty eop
                        {
                            buf.skip();
                        }
                        eop.productKeys.classKey = myClassKey;                        
                        myEOPList.add(eop);
                        break;
                    case -1:
                        return;
                    default:
                        buf.skip();
                }
                previousCodecId = codecId;

                while ((codecId = buf.nextCodecId()) != -1)
                {
                    if(codecId==previousCodecId){//same codec, keep adding to the list
                        switch (codecId)
                        {
                            case MarketUpdateCodec.CODEC_ID:
                                mktUpdate = decodeNext(buf, pt.fullCodec);
                                if (mktUpdate==null)
                                {
                                    buf.skip();
                                }
                                myMktUpdateList.add(mktUpdate);
                                break;
                            case MarketUpdateEOPCodec.CODEC_ID:
                                eop = decodeNext(buf, pt.eopCodec);
                                if (eop==null)   // ignore empty eop
                                {
                                    buf.skip();
                                }
                                eop.productKeys.classKey = myClassKey;
                                myEOPList.add(eop);
                                break;
                            default:
                                buf.skip();
                                continue;
                        }
                    }
                    else{//different codec, send out the prevous built list first, then start to build a new list.
                        switch (previousCodecId)
                        {
                            case MarketUpdateCodec.CODEC_ID:
                                if(myMktUpdateList.size()>0){

                                    MarketUpdate myMKTUpdate = myMktUpdateList.get(0);
                                    processMarketUpdates(myClassKey, myMKTUpdate.getProdType(), myMktUpdateList, myMKTUpdate.getSessionName());
                                    myMktUpdateList.clear();
                                }
                                break;
                            case MarketUpdateEOPCodec.CODEC_ID:
                                if(myEOPList.size()>0){
                                    ExpectedOpeningPriceStruct myeop = myEOPList.get(0);
                                    processEOPs(myClassKey, myeop.productKeys.productType, myEOPList, myeop.sessionName);
                                    myEOPList.clear();
                                }
                                break;
                            default:
                                break;
                        }
                        previousCodecId = codecId;
                    }
                }
                //send out the last array
                switch (previousCodecId)
                {
                    case MarketUpdateCodec.CODEC_ID:
                        if(myMktUpdateList.size()>0){

                            MarketUpdate myMKTUpdate = myMktUpdateList.get(0);
                            processMarketUpdates(myClassKey, myMKTUpdate.getProdType(), myMktUpdateList, myMKTUpdate.getSessionName());
                            myMktUpdateList.clear();
                        }
                        break;
                    case MarketUpdateEOPCodec.CODEC_ID:
                        if(myEOPList.size()>0){
                            ExpectedOpeningPriceStruct myeop = myEOPList.get(0);
                            processEOPs(myClassKey, myeop.productKeys.productType, myEOPList, myeop.sessionName);
                            myEOPList.clear();
                        }
                        break;
                    default:
                        break;
                }
            }

            catch (Exception e)
            {
                Log.exception(e);
            }
        }
    }

    /**
     * Extracts (reads and decodes) a MarketUpdateImpl from a MarketUpdateCodec read from a MarketDataBuffer.
     *
     * @param p_buf: the MarketDataBuffer to read from.
     * @param p_codec: the MarketUpdateCodec to populate after reading and then decode.
     * @return the resulting MarketUpdateImpl after decoding.
     *
     */
    private MarketUpdate decodeNext(MarketDataBuffer p_buf, MarketUpdateCodec p_codec)
    {
        try
        {
            if (!p_buf.read(p_codec))
            {
                StringBuffer mybuffer = new StringBuffer(48);
                mybuffer.append("Can't read from the buffer for codec= ").append(
                        p_codec.getCodecId());
                Log.alarm(mybuffer.toString());
                return null;
            }
        }
        catch (IOException io)
        {
            Log.alarm("MarketBufferConsumerIECImpl MarketUpdate:MsgCodecIOException:" + io);
            return null;
        }

        final ClientMarketUpdateImpl mktUpdate = new ClientMarketUpdateImpl();
        MarketUpdateCodecHelper.copyFromMarketDataBufferAndCodec(mktUpdate, p_buf, p_codec);
        return mktUpdate;
    }
    /**
     *
     * Extracts (reads and decodes) a ExpectedOpeningPriceStruct from a MarketUpdateEOPCodec read from a MarketDataBuffer.
     *
     * @param p_buf: the MarketDataBuffer to read from.
     * @param p_codec: the MarketUpdateEOPCodec to populate after reading and then decode.
     * @return the resulting ExpectedOpeningPriceStruct after decoding and converting the MarketUpdateEOPCodec.
     *
     */
    private ExpectedOpeningPriceStruct decodeNext(MarketDataBuffer p_buf, MarketUpdateEOPCodec p_codec)
    {
        try
        {
            if (!p_buf.read(p_codec))
            {
                StringBuffer mybuffer = new StringBuffer(48);
                mybuffer.append("Can't read from the buffer for codec= ").append(
                        p_codec.getCodecId());
                Log.alarm(mybuffer.toString());
                return null;
            }
        }
        catch (IOException io)
        {
            Log.alarm("MarketBufferConsumerIECImpl ExpectedOpeningPriceStruct:MsgCodecIOException:" + io);
            return null;
        }

        final ExpectedOpeningPriceStruct eop = new ExpectedOpeningPriceStruct();
        p_codec.copyIntoStruct(eop);
        return eop;
    }


    /**
     * Differentiate the contents of the MarketUpdates by distinguishing market quotes and state changes.
     * Then calls the publishing methods accordingly.
     *
     * @param p_classKey: the int class key.
     * @param p_prodType: the short product type.
     * @param p_updatesToPublish: the ArrayList containing the MarketUpdates to process.
     *
     */

    public void processMarketUpdates(int p_classKey, short p_prodType, ArrayList<MarketUpdate> p_updatesToPublish, String aSessionName)
    {
        final int len = p_updatesToPublish.size();
        int numPubMkts = 0;
        int numPubMktsAtTop = 0;
        
        //Not used so commented
        //long entryTime = 0;

        for (int i = 0; i < len; i++)
        {
            final MarketUpdate upd = p_updatesToPublish.get(i);

            //TODO: Remove this if we get rid of pub mkts at top.
            if (upd.isPublicPriceBest())
                numPubMktsAtTop++;

            if (upd.hasPublicPrice())
                numPubMkts++;

          //  if(entryTime < upd.getSentTime())
          //      entryTime = upd.getSentTime();
        }

        //final int numMarketQuotes = len;
        //final int numMarketStructsNeeded = len*2 + numPubMkts + numPubMktsAtTop;
        if (len>0){
        	//MarketUpdatehelper returns a threadlocal list of currentMarketStructs which 
        	//has a potential of data corruption so commented the following code ....Arun - Oct 12 2010
           // final ArrayList<CurrentMarketStruct> list = MarketUpdateHelper.getCurrentMarketStructs(numMarketStructsNeeded);
           // publishMarketUpdates(p_classKey,p_prodType,p_updatesToPublish,numPubMkts,numPubMktsAtTop, numMarketQuotes,list, aSessionName);
        	publishMarketUpdates(p_classKey,p_prodType,p_updatesToPublish,numPubMkts,numPubMktsAtTop, len ,aSessionName);
        }
    }
    /**
     * Convert MarketUpdates into CurrentMarketStructs and publish these to the CurrentMarket event channel.
     *
     * @param p_classKey: the int class key.
     * @param p_prodType: the short product type.
     * @param p_updatesToPublish: the ArrayList containing the MarketUpdates to process.
     * @param p_numPubMkts: the int number of public markets.
     * @param p_numPubMktsAtTop: the int number of public markets at top.
     * @param p_numMarketQuotes: the int number of market quotes.
     * @param p_structsToUseList: an ArrayList containing empty CurrentMarketStructs to be populated and then published.
     *
     * @return int number of structs consumed from CurrentMarketStruct
     */
    /* 
    private int publishMarketUpdates(int p_classKey,
                                     short p_prodType,
                                     ArrayList<MarketUpdate> p_updatesToPublish,
                                     int p_numPubMkts,
                                     int p_numPubMktsAtTop,
                                     final int p_numMarketQuotes,
                                     //final ArrayList<CurrentMarketStruct> p_structsToUseList,
                                     String aSessionName)
    {
      try{
        int idx=0;
        int bestIdx=0;
        int pubIdx=0;
        int pubAtTopIdx=0;
        long entryTime = 0;
        /*
        CurrentMarketStruct[] bestMkts       = MarketUpdateHelper.getCurrMktArray(p_numMarketQuotes);^M
        CurrentMarketStruct[] bestLmtMkts    = MarketUpdateHelper.getCurrMktArray(p_numMarketQuotes);^M
        CurrentMarketStruct[] bestPubMkts    = MarketUpdateHelper.getCurrMktArray(p_numPubMkts);^M
        CurrentMarketStruct[] bestPubTopMkts = MarketUpdateHelper.getCurrMktArray(p_numPubMktsAtTop);^M
		
        // Commented the above code as the helper return thread local Array a potential problem
        // data corruption when publishing on IEC......Arun Oct 11 2010.
        CurrentMarketStruct[] bestMkts       = getCurrMktArray(p_numMarketQuotes);
        CurrentMarketStruct[] bestLmtMkts    = getCurrMktArray(p_numMarketQuotes);
        CurrentMarketStruct[] bestPubMkts    = getCurrMktArray(p_numPubMkts);
        CurrentMarketStruct[] bestPubTopMkts = getCurrMktArray(p_numPubMktsAtTop);

        final int len = p_updatesToPublish.size();
        for (int i = 0; i < len; i++)
        {
            final MarketUpdate upd = p_updatesToPublish.get(i);
            CurrentMarketStruct mkt;
            bestMkts[bestIdx] = mkt = p_structsToUseList.get(idx++);
            MarketUpdateHelper.copyIntoStruct(upd, mkt, CurrentMarketViewTypes.BEST_PRICE);
            mkt.sessionName = upd.getSessionName();
            bestLmtMkts[bestIdx++] = mkt = p_structsToUseList.get(idx++);
            MarketUpdateHelper.copyIntoStruct(upd, mkt, CurrentMarketViewTypes.BEST_LIMIT_PRICE);
            if (upd.hasPublicPrice())
            {
                bestPubMkts[pubIdx++] = mkt = p_structsToUseList.get(idx++);
                MarketUpdateHelper.copyIntoStruct(upd, mkt, CurrentMarketViewTypes.BEST_PUBLIC_ORDER_PRICE);
                if (upd.isPublicPriceBest())
                {
                    //TODO - XXX - SPS - Not sure: is the best pub the same as best mkts when @top?
                    //It'll be better if we can just junk this.
                    //Update: it's similar but, of course, different.  It represents the same tradables
                    //but with a cust/prof breakdown rather than limit/aon/oddlot.  Still better if
                    //we can junk it.
                    bestPubTopMkts[pubAtTopIdx++] = bestPubMkts[pubIdx-1];
                }
            }

            if(entryTime < upd.getSentTime()) entryTime = upd.getSentTime();
        }

        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(p_classKey);
        RoutingParameterStruct routingParams = new RoutingParameterStruct(groupKeys, aSessionName, p_classKey , (short)0);
        routingParams.productType = p_prodType;

        if ( theCurrentMarketConsumer== null){
             theCurrentMarketConsumer=theCurrentMarketConsumerHome.find();
        }
        this.theCurrentMarketConsumer.acceptCurrentMarketsForClass(routingParams,
                                                                 bestMkts,
                                                                 bestLmtMkts,
                                                                 EMPTY_NBBO,
                                                                 EMPTY_MKTS,
                                                                 bestPubMkts,
                                                                 bestPubTopMkts,
                                                                 new boolean[0]);
        return idx;
      }
      catch(com.cboe.exceptions.SystemException se){
        Log.exception(this, se);
        return -1;
      }
      catch(com.cboe.exceptions.DataValidationException de){
        Log.exception(this, de);
        return -1;
      }
      catch(com.cboe.exceptions.CommunicationException ce){
        Log.exception(this, ce);
        return -1;
      }
      catch(com.cboe.exceptions.AuthorizationException ae){
        Log.exception(this, ae);
        return -1;
      }
    }*/
    
    
    /**
     * Convert MarketUpdates into CurrentMarketStructs and publish these to the CurrentMarket event channel.
     *
     * @param p_classKey: the int class key.
     * @param p_prodType: the short product type.
     * @param p_updatesToPublish: the ArrayList containing the MarketUpdates to process.
     * @param p_numPubMkts: the int number of public markets.
     * @param p_numPubMktsAtTop: the int number of public markets at top.
     * @param p_numMarketQuotes: the int number of market quotes.
     * @param p_structsToUseList: an ArrayList containing empty CurrentMarketStructs to be populated and then published.
     *
     * @return int number of structs consumed from CurrentMarketStruct
     *
     */
    private int publishMarketUpdates(int p_classKey,
                                     short p_prodType,
                                     ArrayList<MarketUpdate> p_updatesToPublish,
                                     int p_numPubMkts,
                                     int p_numPubMktsAtTop,
                                     final int p_numMarketQuotes,
                                     String aSessionName)
    {
      try{
        int idx=0;
        int bestIdx=0;
        int pubIdx=0;
        int pubAtTopIdx=0;
        //Not Used - so commented
        //long entryTime = 0;
        /*
        CurrentMarketStruct[] bestMkts       = MarketUpdateHelper.getCurrMktArray(p_numMarketQuotes);^M
        CurrentMarketStruct[] bestLmtMkts    = MarketUpdateHelper.getCurrMktArray(p_numMarketQuotes);^M
        CurrentMarketStruct[] bestPubMkts    = MarketUpdateHelper.getCurrMktArray(p_numPubMkts);^M
        CurrentMarketStruct[] bestPubTopMkts = MarketUpdateHelper.getCurrMktArray(p_numPubMktsAtTop);^M
		*/ 
        // Commented the above code as the helper return thread local Array a potential problem
        // data corruption when publishing on IEC......Arun Oct 11 2010.
        CurrentMarketStruct[] structsToUseList = getCurrMktArray(p_numMarketQuotes*2+p_numPubMkts + p_numPubMktsAtTop);
        
        CurrentMarketStruct[] bestMkts       = new CurrentMarketStruct[p_numMarketQuotes];
        CurrentMarketStruct[] bestLmtMkts    = new CurrentMarketStruct[p_numMarketQuotes];
        CurrentMarketStruct[] bestPubMkts    = new CurrentMarketStruct[p_numPubMkts];
        CurrentMarketStruct[] bestPubTopMkts = new CurrentMarketStruct[p_numPubMktsAtTop];

        final int len = p_updatesToPublish.size();
        for (int i = 0; i < len; i++)
        {
            final MarketUpdate upd = p_updatesToPublish.get(i);
            CurrentMarketStruct mkt;
            bestMkts[bestIdx] = mkt = structsToUseList[idx++];
            MarketUpdateHelper.copyIntoStruct(upd, mkt, CurrentMarketViewTypes.BEST_PRICE);
            mkt.sessionName = upd.getSessionName();
            bestLmtMkts[bestIdx++] = mkt = structsToUseList[idx++];
            MarketUpdateHelper.copyIntoStruct(upd, mkt, CurrentMarketViewTypes.BEST_LIMIT_PRICE);
            if (upd.hasPublicPrice())
            {
                bestPubMkts[pubIdx++] = mkt = structsToUseList[idx++];
                MarketUpdateHelper.copyIntoStruct(upd, mkt, CurrentMarketViewTypes.BEST_PUBLIC_ORDER_PRICE);
                if (upd.isPublicPriceBest())
                {
                    //TODO - XXX - SPS - Not sure: is the best pub the same as best mkts when @top?
                    //It'll be better if we can just junk this.
                    //Update: it's similar but, of course, different.  It represents the same tradables
                    //but with a cust/prof breakdown rather than limit/aon/oddlot.  Still better if
                    //we can junk it.
                    bestPubTopMkts[pubAtTopIdx++] = bestPubMkts[pubIdx-1];
                }
            }

          //  if(entryTime < upd.getSentTime()) entryTime = upd.getSentTime();
        }

        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(p_classKey);
        RoutingParameterStruct routingParams = new RoutingParameterStruct(groupKeys, aSessionName, p_classKey , (short)0);
        routingParams.productType = p_prodType;

        if ( theCurrentMarketConsumer== null){
             theCurrentMarketConsumer=theCurrentMarketConsumerHome.find();
        }
        this.theCurrentMarketConsumer.acceptCurrentMarketsForClass(routingParams,
                                                                 bestMkts,
                                                                 bestLmtMkts,
                                                                 EMPTY_NBBO,
                                                                 EMPTY_MKTS,
                                                                 bestPubMkts,
                                                                 bestPubTopMkts,
								 new boolean[0]);
        return idx;
      }
      catch(com.cboe.exceptions.SystemException se){
        Log.exception(this, se);
        return -1;
      }
      catch(com.cboe.exceptions.DataValidationException de){
        Log.exception(this, de);
        return -1;
      }
      catch(com.cboe.exceptions.CommunicationException ce){
        Log.exception(this, ce);
        return -1;
      }
      catch(com.cboe.exceptions.AuthorizationException ae){
        Log.exception(this, ae);
        return -1;
      }
    }

    private CurrentMarketStruct[] getCurrMktArray(int marketQuotes) {
    	CurrentMarketStruct[] currArray = new CurrentMarketStruct[marketQuotes];
    	for(int i=0; i<currArray.length;i++) {
    		currArray[i] = new CurrentMarketStruct();
    		currArray[i].productKeys = new com.cboe.idl.cmiProduct.ProductKeysStruct();
    	}
    	return currArray;
	}
	public void processEOPs(int p_classKey,
                            short p_prodType,
                            ArrayList<ExpectedOpeningPriceStruct> p_expectedOpeningPrices,
                            String aSessionName)
    {
        try{
            int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(p_classKey);
            RoutingParameterStruct routingParams = new RoutingParameterStruct(groupKeys, aSessionName, p_classKey , (short)0);
            routingParams.productType = p_prodType;

            int len = p_expectedOpeningPrices.size();
            ExpectedOpeningPriceStruct[] expectedOpeningPrices = new ExpectedOpeningPriceStruct[len];
            for(int i=0; i < len; i++)
            {
                expectedOpeningPrices[i] = p_expectedOpeningPrices.get(i);
            }

            if ( theCurrentMarketConsumer== null){
                 theCurrentMarketConsumer=theCurrentMarketConsumerHome.find();
            }

            this.theCurrentMarketConsumer.acceptExpectedOpeningPricesForClass (routingParams,
                                                                     expectedOpeningPrices);
        }
        catch(com.cboe.exceptions.SystemException se){
            Log.exception(this, se);
            return;
        }
        catch(com.cboe.exceptions.DataValidationException de){
          Log.exception(this, de);
          return;
        }
        catch(com.cboe.exceptions.CommunicationException ce){
          Log.exception(this, ce);
          return;
        }
        catch(com.cboe.exceptions.AuthorizationException ae){
          Log.exception(this, ae);
          return;
        }
    }

    /**
     * Turn MarketBuffer message logging on or off. When on, each incoming data buffer
     * will be logged using Log.information().
     * @param log true to log buffers coming into acceptMarketBuffer, false to not log.
     */
    public void logMessages(boolean log)
    {
        logMessages = log;
        StringBuffer mybuffer = new StringBuffer(24);
        mybuffer.append("Set logMessages to ").append(logMessages);
        Log.information(this, mybuffer.toString());
    }

    static private final char[] hexDigits = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    /**
     * Convert a byte buffer into a hexadecimal string.
     * @param buffer Input to convert
     * @return Hexadecimal character equivalent, or empty string.
     */
    private String convertToHex(byte[] buffer)
    {
        if (buffer == null || buffer.length == 0)
        {
            return "";
        }

        StringBuilder result = new StringBuilder(2*buffer.length);
        for (byte b : buffer)
        {
            int high = (b >> 4) & 0xF;
            int low = b & 0xF;
            result.append(hexDigits[high]).append(hexDigits[low]);
        }

        return result.toString();
    }
}
