//Source file: d:/develop/com/cboe/presentation/rfq/RFQCacheImpl.java

package com.cboe.presentation.api;

import com.cboe.domain.util.DateWrapper;
import com.cboe.idl.cmiQuote.RFQStruct;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.ExceptionDetails;
import com.cboe.interfaces.presentation.rfq.RFQCache;
import com.cboe.interfaces.presentation.rfq.RFQ;
import com.cboe.presentation.api.RFQFactory;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;

import com.cboe.presentation.common.time.TimeSyncWrapper;

import java.util.Map;
import java.util.HashMap;
import java.util.Observer;
import java.util.Observable;

/**
   RFQCacheImpl is an implementation of RFQCache which maintains a cache of RFQ objects.
   The state of the cache is maintained through ChannelEvents received over the IEC.
   @author Will McNabb
 */
public class RFQCacheImpl implements Observer, RFQCache {
    protected Map rfqByClass;
    protected Map rfqByProduct;

    public RFQCacheImpl()
    {
        rfqByClass = new HashMap();
        rfqByProduct = new HashMap();
    }

    public void update(Observable o, Object arg)
    {
        RFQ rfq = (RFQ) arg;
        if (rfq.getState() == rfq.DELETED)
        {
            removeRFQ(rfq);
        }
    }

    private void addRFQByClass(RFQ rfq)
    {
        Integer classKey = rfq.getClassKey();
        Integer productKey = rfq.getProductKey();

        Map rfqsForClass = (Map)rfqByClass.get(classKey);
        if (rfqsForClass == null)
        {
            rfqsForClass = new HashMap();
            rfqByClass.put(classKey, rfqsForClass);
        }
        rfqsForClass.put(productKey, rfq);
    }

    private void addRFQByProduct(RFQ rfq)
    {
        Integer productKey = rfq.getProductKey();
        rfqByProduct.put(productKey, rfq);
    }

    private void removeRFQByClass(RFQ rfq)
    {
        Integer classKey = rfq.getClassKey();

        Map rfqsForClass = (Map)rfqByClass.get(classKey);
        if (rfqsForClass != null)
        {
            rfqsForClass.remove(rfq.getProductKey());
        }
    }

    private void removeRFQByProduct(RFQ rfq)
    {
        rfqByProduct.remove(rfq.getProductKey());
    }

    public boolean doRFQsExist()
    {
        return (getRFQCount() > 0) ? true : false;
    }

    public int getRFQCount()
    {
        if(rfqByProduct != null)
        {
            return rfqByProduct.size();
        }
        else
        {
            return 0;
        }
    }

    public synchronized void addRFQ(RFQStruct rfqStruct)
    {
        RFQ rfq = RFQFactory.find(this, rfqStruct);
        addRFQByClass(rfq);
        addRFQByProduct(rfq);
    }

    public void addRFQs(RFQStruct[] rfqs)
    {
        for (int i = 0; i < rfqs.length; i++)
        {
            addRFQ(rfqs[i]);
        }
    }

    public synchronized void removeRFQ(RFQ rfq)
    {
        removeRFQByClass(rfq);
        removeRFQByProduct(rfq);
    }

    public void removeRFQs(RFQ[] rfqs)
    {
        for (int i = 0; i < rfqs.length; i++)
        {
            removeRFQ(rfqs[i]);
        }
    }

    public synchronized RFQ[] getRFQsForClass(int classKey)
    {
        RFQ[] rfqs = new RFQ[0];

        Map rfqsForClass = (Map)rfqByClass.get(new Integer(classKey));
        if (rfqsForClass != null)
        {
            rfqs = new RFQ[rfqsForClass.size()];
            rfqs = (RFQ[]) rfqsForClass.values().toArray(rfqs);
        }

        if(GUILoggerHome.find().isDebugOn())
        {
            RFQStruct[] structs = new RFQStruct[rfqs.length];
            for(int i=0; i<rfqs.length; i++)
            {
                structs[i] = rfqs[i].getRFQStruct();
            }
            GUILoggerHome.find().debug(TraderAPIImpl.TRANSLATOR_NAME+": RFQCacheImpl.getRFQsForClass(classKey="+classKey+")",GUILoggerBusinessProperty.RFQ,structs);
        }
        return rfqs;
    }

    /* Will return null if RFQ not found */
    public synchronized RFQ getRFQForProduct(int productKey)
    {
        RFQ rfq = (RFQ)rfqByProduct.get(new Integer(productKey));
//        if (rfq == null)
//        {
//            DateWrapper currentDateTime = new DateWrapper(System.currentTimeMillis());
//            DateWrapper currentDateTime = new DateWrapper(TimeSyncWrapper.getCorrectedTimeMillis());
//            ExceptionDetails details = new ExceptionDetails("RFQ not found for product " + productKey, currentDateTime.format(), (short)0, 0);
//
//            NotFoundException e = new NotFoundException(details);
//            throw e;
//        }
        return rfq;
    }

    public synchronized RFQ[] getAllRFQs()
    {
        RFQ[] rfqArray = new RFQ[rfqByProduct.size()];
        RFQ[] rfqs = (RFQ[])rfqByProduct.values().toArray(rfqArray);
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TraderAPIImpl.TRANSLATOR_NAME+": RFQCacheImpl.getAllRFQs()",GUILoggerBusinessProperty.RFQ,rfqs);
        }
        return rfqs;
    }

    protected void processQuotes(QuoteDetailStruct[] quotes)
    {
        RFQ rfq;

        for (int i = 0; i < quotes.length; i++)
        {
            RFQFactory.remove(quotes[i].productKeys.productKey);
        }
    }

    public synchronized void channelUpdate(ChannelEvent event)
    {
        try
        {
            ChannelKey channel = (ChannelKey)event.getChannel();
            if (channel.channelType == ChannelType.RFQ)
            {
                RFQStruct rfq = (RFQStruct)event.getEventData();
                if(GUILoggerHome.find().isDebugOn())
                {
                    GUILoggerHome.find().debug(TraderAPIImpl.TRANSLATOR_NAME+": RFQCacheImpl.channelUpdate() -- ChannelType.RFQ event",GUILoggerBusinessProperty.RFQ, rfq);
                }
                addRFQ(rfq);
            } else if (channel.channelType == ChannelType.CB_ALL_QUOTES)
            {
                QuoteDetailStruct[] quotes = (QuoteDetailStruct[])event.getEventData();
                if(GUILoggerHome.find().isDebugOn())
                {
                    GUILoggerHome.find().debug(TraderAPIImpl.TRANSLATOR_NAME+": RFQCacheImpl.channelUpdate() -- ChannelType.CB_ALL_QUOTES event",GUILoggerBusinessProperty.RFQ, quotes);
                }
                processQuotes(quotes);
            } else
            {
                // shouldn't be possible to get here but it is a good check for bad code
                System.out.println("RFQCacheImpl -> Bad channel event " + channel.channelType);
            }
        } catch (Exception e)
        {
            GUILoggerHome.find().exception("Exception in RFQCacheImpl" , e);
        }
    }
}
