package com.cboe.domain.bestQuote;

import com.cboe.domain.util.MarketDataStructBuilder;
import com.cboe.domain.util.PriceFactory;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.quote.ExternalQuoteSideStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.Side;
import com.cboe.interfaces.domain.bestQuote.BestQuote;



/**
 * A persistent implementation of best quote.
 *
 * @author Anil Kalra
 */
public final class NbboBotrImpl implements BestQuote
{
    
	static { System.out.println("Using new impl"); }
	
    private static final Price noPrice = PriceFactory.getNoPrice();
    private static final ExchangeVolumeStruct[] EMPTY_VOLS = new ExchangeVolumeStruct[0];

    private static final class SideBestQuote
    {
        private Price price = noPrice;
        private ExchangeVolumeStruct[] exchangeVolumes;
        private int numVols = 0;
        
        public Price getPrice() { return price; }
        public void setPrice(Price p_val) { this.price = p_val; }

        
        public SideBestQuote()
        {
            exchangeVolumes = new ExchangeVolumeStruct[16];
            for (int i=0; i < 6; i++)
            {
                exchangeVolumes[i] = new ExchangeVolumeStruct("",0);
            }
        }

        public SideBestQuote(String exchange)
        {
            exchangeVolumes = new ExchangeVolumeStruct[1];
            exchangeVolumes[0] = new ExchangeVolumeStruct(exchange,0);
        }
        
        
        public void addExchangeVolume(String p_exchAcronym, int p_volume)
        {           
            if (numVols + 1 == exchangeVolumes.length)  // unlikely, given the initial size of 16.
            {
                ExchangeVolumeStruct[] newVols = new ExchangeVolumeStruct[exchangeVolumes.length*2];
                System.arraycopy(exchangeVolumes, 0, newVols, 0, exchangeVolumes.length);
                exchangeVolumes = newVols;
            }
            ExchangeVolumeStruct exchVol = exchangeVolumes[numVols];
        	if (exchVol == null)
            {
                exchVol = new ExchangeVolumeStruct();
                exchangeVolumes[numVols] = exchVol;
            }
        	exchVol.exchange = p_exchAcronym;
        	exchVol.volume = p_volume;
            numVols++;
        }
        
        // copy exch/vols directly from another SideBestQuote
        public void setExchangeVolumes(SideBestQuote quote) 
        {
        	numVols = 0;
        	for (int i=0; i < quote.numVols; i++)
        	{
        		addExchangeVolume(quote.exchangeVolumes[i].exchange, quote.exchangeVolumes[i].volume);
        	}
        }
        
        public void setExchangeVolumes(final ExchangeVolumeStruct[] p_newVals)
        {
            if (p_newVals.length > exchangeVolumes.length)  // unlikely, given the initial size of 16.
            {
                exchangeVolumes = new ExchangeVolumeStruct[Math.max(p_newVals.length, exchangeVolumes.length*2)];
            }
            for (int i = 0; i < p_newVals.length; i++) 
            {
                ExchangeVolumeStruct exchVol = exchangeVolumes[i];
                if (exchVol == null) // this would be unusual, given that 6 vols are bootstrapped
                {
                    //exchangeVolumes[i] = new ExchangeVolumeStruct(p_newVals[i].exchange, p_newVals[i].volume);
                    exchangeVolumes[i] = MarketDataStructBuilder.getExchangeVolumeStruct(p_newVals[i].exchange, p_newVals[i].volume);

                }
                else
                {
                	exchVol.exchange = getExchangeString(p_newVals[i].exchange);
                	exchVol.volume = p_newVals[i].volume;
                }
            }
            numVols = p_newVals.length;
        }
        
        public ExchangeVolumeStruct[] getExchangeVolumes()
        {
            if (numVols==0)
                return EMPTY_VOLS;
            
            // Create a new array on every access to guarantee that the values won't mutate.
            // This might be replaceable with a thread-local, but that might be impractical.
            final ExchangeVolumeStruct[] result = new ExchangeVolumeStruct[numVols];
            for (int i = 0; i < result.length; i++) 
            {
                //result[i] = new ExchangeVolumeStruct(exchangeVolumes[i].exchange, exchangeVolumes[i].volume);
                result[i] = MarketDataStructBuilder.getExchangeVolumeStruct(exchangeVolumes[i].exchange, exchangeVolumes[i].volume);

            }
            return result;
        }
        public PriceStruct getPriceStruct()
        {
            return price.toStruct();
        }
    }
    
    /**
     * Gets the total volume by the side, it loops through all the exchanges and accumulate the
     * total volume to return (Mostly used in HAL Helper).
     */
    public int getBotrQuantityBySide(Side side)
    {
        int botrSideVolume = 0;
        
        if (side.isBuySide())
        {
            ExchangeVolumeStruct[] bidExchangeVolumes = this.bidSide.getReadOnlyQuote().exchangeVolumes;
            if(bidExchangeVolumes != null)
            {
                for(int i = 0; i < bidExchangeVolumes.length; i++)
                {
                    if(bidExchangeVolumes[i] != null)
                    {
                        botrSideVolume += bidExchangeVolumes[i].volume;
                    }
                }
            }
        }
        else
        {
            ExchangeVolumeStruct[] askExchangeVolumes = this.askSide.getReadOnlyQuote().exchangeVolumes;
            if(askExchangeVolumes != null)
            {
                for(int i = 0; i < askExchangeVolumes.length; i++)
                {
                    if(askExchangeVolumes[i] != null)
                    {
                        botrSideVolume += askExchangeVolumes[i].volume;
                    }
                }
            }
        }
        
        return botrSideVolume;
    }
    

    private final class SideBestQuoteUpdater
    {
        SideBestQuote quote = null;
        SideBestQuote dirtyQuote = null;
        boolean isDirty = false;
        final char side;

        public SideBestQuoteUpdater(String exchange, char p_side)
        {
            this.quote = new SideBestQuote(exchange);
            this.dirtyQuote = new SideBestQuote(exchange);
            isDirty = false;
	    side = p_side;
        }
        
        public SideBestQuoteUpdater(char p_side)
        {
            this.quote = new SideBestQuote();
            this.dirtyQuote = new SideBestQuote();
            isDirty = false;
	    side = p_side;
        }

        synchronized SideBestQuote getReadOnlyQuote()
        {
            swapIfDirty();
            return quote;
        }

        private void swapIfDirty()
        {
            if (isDirty)
            {
                final SideBestQuote newVal = dirtyQuote;
                dirtyQuote = quote;
                quote = newVal;
                isDirty = false;
            }
        }

        /**
         * <hr><b>NOTE:</b>
         * The state-based synchronization points assumes only one writer of
         * quote updates.  If there are multiple writers <i>(update: there are)</i>, then the call to updateQuote
         * must be done from within a synchronized(SideBestQuoteUpdater) { ... } block <i>(update: it is)</i>.
         * <hr>
         * 
         * @param p_price
         *      New price for quote side. May be null for a volume-only update.  NoPrice and 0 vol will result in a price&vol update
         * @param p_vols
         *      Array of volumes and exchanges to populate. May be null or empty for a price-only update.
         * @param p_extraVol
         *      An extra volume to append to p_vols.  Ignored if null or 0.  Used to efficiently update local market.
         */
        public synchronized void updateQuote(final Price p_price, final ExchangeVolumeStruct[] p_vols, 
                final ExchangeVolumeStruct p_extraVol)
        {
        	if (p_price == null && p_vols == null && p_extraVol==null)
    		{
    			return; // null change: nothing to update.
    		}

            swapIfDirty(); // multiple updates before reading: update read side, get a 'dirty' available for update.
            
            // If price is null, then we're updating vols only: copy "live" price from clean->dirty so we can update vols
            dirtyQuote.setPrice(p_price==null ? quote.getPrice() :  p_price);

            if ( (p_vols == null || p_vols.length==0) ) // no volumes in array
            {
            	if (p_extraVol==null || p_extraVol.volume==0) // no volumes at all!
            	{
            		if (p_price.isNoPrice()) // special case: no price implies no volume
            			dirtyQuote.setExchangeVolumes(EMPTY_VOLS); // we're clearing this entry since price is NoPrice.
            		else
            			dirtyQuote.setExchangeVolumes(quote); // Updating price only: copy "live" vols from clean->dirty
            	}
            	else
            	{
            		// Updating volumes to extra vol only
            		dirtyQuote.setExchangeVolumes(EMPTY_VOLS);
                    dirtyQuote.addExchangeVolume(p_extraVol.exchange, p_extraVol.volume);
            	}
            }
            else
            {
            	// Updating volumes array, and maybe an extra one.
                dirtyQuote.setExchangeVolumes(p_vols);
                if (p_extraVol != null && p_extraVol.volume != 0 )
                    dirtyQuote.addExchangeVolume(p_extraVol.exchange, p_extraVol.volume);                  
            }
            
            isDirty = true; // (already in synch method, no need to synch(this))
        }
    }
        
    private final SideBestQuoteUpdater bidSide;
    private final SideBestQuoteUpdater askSide;
    private final TimeStruct sentTime = new TimeStruct();

    private static final String[] COMMON_PREFIX_STRINGS = new String[26*26*26]; // cache based on 1st,2nd,last char.  Array len is about 17k.
    
    /**
     * Return a string guaranteed to be equal to p_str, but which may be a constant string.
     * @param p_str
     * @return String equal to p_str.  May be a reference to p_str itself.
     */
    public static String getExchangeString(final String p_str)
    {
        final int len = p_str.length();
        if (len < 2)
            return p_str;
        final int idx = (p_str.charAt(0)-'A')*(26*26) + (p_str.charAt(1)-'A')*26 + (p_str.charAt(len-1)-'A');
        if (idx < 0 || idx >= COMMON_PREFIX_STRINGS.length)
            return p_str;
        if (COMMON_PREFIX_STRINGS[idx] == null)
        {
            COMMON_PREFIX_STRINGS[idx] = p_str;
            return p_str;
        }
        else
        {
            final String cached = COMMON_PREFIX_STRINGS[idx];
            if (cached != null && cached.length() == len)
            {
                // We're usually looking for very short strings.  We can cut some corners on the 
                // compare, since we've already matched the 1st, 2nd, and last chars:
                if (len < 4)
                    return cached; // the 3-char lookup identified all characters.
                if (len == 4)
                    return p_str.charAt(2) == cached.charAt(2) ? cached : p_str;
                return cached.equals(p_str) ? cached : p_str;
            }
        }
        return p_str;
    }

    /**
     * Creates an instance.
     */
    public NbboBotrImpl()
    {
        this.bidSide = new SideBestQuoteUpdater(Sides.BID);
        this.askSide = new SideBestQuoteUpdater(Sides.ASK);
    }

    /**
     * Creates an instance.
     */
    public NbboBotrImpl(String exchange)
    {
        this.bidSide = new SideBestQuoteUpdater(exchange, Sides.BID);
        this.askSide = new SideBestQuoteUpdater(exchange, Sides.ASK);
        
    }

    
    /**
     * define a copy constructor for cloning purpose.
     */
    public NbboBotrImpl(NbboBotrImpl cloneObject) {
        this.bidSide = new SideBestQuoteUpdater(cloneObject.getBidExchangeVolumes()[0].exchange, Sides.BID);
        this.askSide = new SideBestQuoteUpdater(cloneObject.getAskExchangeVolumes()[0].exchange, Sides.ASK);
        this.bidSide.quote.setExchangeVolumes(cloneObject.getBidExchangeVolumes());
        this.askSide.quote.setExchangeVolumes(cloneObject.getAskExchangeVolumes());
        this.bidSide.quote.setPrice(cloneObject.getBidPrice());
        this.askSide.quote.setPrice(cloneObject.getAskPrice());
        this.bidSide.quote.numVols = cloneObject.bidSide.quote.numVols;
        this.askSide.quote.numVols = cloneObject.askSide.quote.numVols;
        updateSentTime(cloneObject.sentTime);    
    }
    
    // instantiate a new NbboBotrImpl with a given exchange name.
    public void create(String exchange) {
        this.bidSide.quote.addExchangeVolume(exchange, 0);
        this.askSide.quote.addExchangeVolume(exchange, 0);
    }
    
    /**
     * Getter for ask exchange volumes
     */
    public ExchangeVolumeStruct[] getAskExchangeVolumes()
    {
        return this.askSide.getReadOnlyQuote().getExchangeVolumes();
    }
    /**
     * Getter for ask price.
     */
    public Price getAskPrice()
    {
        return this.askSide.getReadOnlyQuote().getPrice();
    }
    /**
5     * Getter for bid exchange volumes
     */
    public ExchangeVolumeStruct[] getBidExchangeVolumes()
    {
        return this.bidSide.getReadOnlyQuote().getExchangeVolumes();
    }

    /**
     * Getter for bid price.
     */
    public Price getBidPrice()
    {
        return this.bidSide.getReadOnlyQuote().getPrice();
    }

    /**
     * NOTE: productKey and sessionName are not assigned in the struct returned by this method.
     */
    public ExternalQuoteSideStruct toStruct(char side)
    {
    	return (side == Sides.BID) ? toStruct(bidSide) : toStruct(askSide);
    }
    
    private ExternalQuoteSideStruct toStruct(final SideBestQuoteUpdater p_side)
    {
    	final ExternalQuoteSideStruct struct = new ExternalQuoteSideStruct();
    	struct.side = p_side.side;
    	synchronized (p_side)
    	{
    		final SideBestQuote quote = p_side.getReadOnlyQuote();
    		struct.exchangeVolume = quote.getExchangeVolumes();
    		struct.price = quote.getPriceStruct();
    	}
    	struct.sentTime = this.sentTime;
    	return struct;
    }

    /**
     * NOTE: this one converts everything
     */
 
    public synchronized ExternalQuoteSideStruct toCompleteStruct(char side, String sessionName, ProductKeysStruct productKeys)
    {
        ExternalQuoteSideStruct struct = new ExternalQuoteSideStruct();

        struct.sessionName = sessionName;
        struct.productKeys = productKeys;
        struct.side = side;
        if (side == Sides.BID)
        {
            struct.price = this.getBidPrice().toStruct();
            struct.exchangeVolume = getBidExchangeVolumes();
        }
        else
        {
            struct.price = this.getAskPrice().toStruct();
            struct.exchangeVolume = getAskExchangeVolumes();
        }
        struct.sentTime = this.sentTime;
        return struct;
    }    


    /**
     * @see BestQuote#toNBBOStruct This method returns shallow copy
     * Only used within MarketDataService
     */
    public NBBOStruct toNBBOStruct()
    {
        NBBOStruct struct = new NBBOStruct();
        synchronized (bidSide)
        {
            final SideBestQuote bidQuote = bidSide.getReadOnlyQuote();
            struct.bidExchangeVolume = bidQuote.getExchangeVolumes();
            struct.bidPrice = bidQuote.getPriceStruct();
            synchronized (askSide) // nested synchronization: need to be careful to ALWAYS lock bid then ask!!!!
            {
                final SideBestQuote askQuote = askSide.getReadOnlyQuote();
                struct.askExchangeVolume = askQuote.getExchangeVolumes();
                struct.askPrice = askQuote.getPriceStruct();
                struct.sentTime = this.sentTime;
            }
        }
        return struct;
    }
    
    /**
     * @see BestQuote#update
     *
     * @param newQuote ExternalQuoteSideStruct
     */
    public void update(ExternalQuoteSideStruct newQuote)
    {
        Price price = PriceFactory.create(newQuote.price);
        if (price.isValuedPrice() && price.toLong() == 0L)
        {
           price = PriceFactory.getNoPrice();
        }
        if (newQuote.side == Sides.BID)
        {
            bidSide.updateQuote(price, newQuote.exchangeVolume, null);
        }
        else
        {
            askSide.updateQuote(price, newQuote.exchangeVolume, null);
        }
        updateSentTime(newQuote.sentTime);
    }
    
    private void updateSentTime(TimeStruct p_t)
    {
        this.sentTime.hour = p_t.hour;
        this.sentTime.minute = p_t.minute;
        this.sentTime.second = p_t.second;
        this.sentTime.fraction = p_t.fraction;
    }

    public void update(NBBOStruct newQuote)
    {
        synchronized (bidSide)
        {
            System.out.println("update " + Thread.currentThread());
            bidSide.updateQuote(PriceFactory.create(newQuote.bidPrice), newQuote.bidExchangeVolume, null);
            synchronized (askSide) // nested synchronization: need to be careful to ALWAYS lock bid then ask!!!!
            {
                askSide.updateQuote(PriceFactory.create(newQuote.askPrice), newQuote.askExchangeVolume, null);
                updateSentTime(newQuote.sentTime);
            }
        }
    }
    
    public void updateBidSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume)
    {
        bidSide.updateQuote( newPrice, newExchangeVolume, null);
    }
    
    public void updateAskSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume)
    {
        askSide.updateQuote( newPrice, newExchangeVolume, null);
    }

    // (this is the high-volume update (fed by XTP))
    public void updateBidSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume, ExchangeVolumeStruct extraVol)
    {
        bidSide.updateQuote( newPrice, newExchangeVolume, extraVol);
    }

    public void updateAskSide(Price newPrice, ExchangeVolumeStruct[] newExchangeVolume, ExchangeVolumeStruct extraVol)
    {
        askSide.updateQuote( newPrice, newExchangeVolume, extraVol);
    }

    public void updateBidSide(PriceStruct newPriceStruct, ExchangeVolumeStruct[] newExchangeVolume)
    {
        bidSide.updateQuote(PriceFactory.create(newPriceStruct), newExchangeVolume, null);
    }

    public void updateAskSide(PriceStruct newPriceStruct, ExchangeVolumeStruct[] newExchangeVolume)
    {
        askSide.updateQuote(PriceFactory.create(newPriceStruct), newExchangeVolume, null);
    }
   

/**
 * @see BestQuote#isCrossed
 */
public boolean isCrossed()
{
    Price bid = getBidPrice();
    Price ask = getAskPrice();
    //if neither bid or ask is no price
        return (bid != null && !bid.isNoPrice() && ask != null && !ask.isNoPrice())
                ? ask.lessThan(bid) 
                : false; //if either or both are no price, then the market is not crossed.
    }

public boolean isAskDirty()
{
    // TODO Auto-generated method stub
    return askSide.isDirty;
}

public boolean isBidDirty()
{
    // TODO Auto-generated method stub
    return bidSide.isDirty;
}

public void setAskDirty(boolean p_askDirty)
{
    this.askSide.isDirty = p_askDirty;
    
}

public void setBidDirty(boolean p_bidDirty)
{
    this.bidSide.isDirty = p_bidDirty;
    
}
}
