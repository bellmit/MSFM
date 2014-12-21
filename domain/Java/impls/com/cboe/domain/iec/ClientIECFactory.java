package com.cboe.domain.iec;


import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.util.ThreadPool;

public class ClientIECFactory 
{
	public static final int DEFAULT_POOL_SIZE = 70;
	
	public static final int CAS_IEC = 1;
	public static final int FIX2CAS_IEC = 2;
	public static final int APPIA2FIX_IEC = 3;
	public static final int FIX2CAS_CONC_ORDER_IEC = 4;
	public static final int FIX2CAS_CONC_QUOTE_IEC = 5;
	public static final int MDX_IEC = 7;
	public static final int MARKETDATA_IEC = 6;
	public static final int CAS_INSTRUMENTED_IEC = 11;
	public static final int FIX2CAS_INSTRUMENTED_IEC = 12;
	public static final int APPIA2FIX_INSTRUMENTED_IEC = 13;
	public static final int FIX2CAS_CONC_ORDER_INSTRUMENTED_IEC = 14;
	public static final int FIX2CAS_CONC_QUOTE_INSTRUMENTED_IEC = 15;
	public static final int MDX_INSTRUMENTED_IEC = 17;
	public static final int MARKETDATA_INSTRUMENTED_IEC = 16;

	private static volatile ConcurrentEventChannelAdapter cas_iec = null;
	private static volatile ConcurrentEventChannelAdapter fix2cas_iec = null;
	private static volatile ConcurrentEventChannelAdapter appia2fix_iec = null;
	private static volatile ConcurrentEventChannelAdapter fix2cas_conc_order_iec = null;
	private static volatile ConcurrentEventChannelAdapter fix2cas_conc_quote_iec = null;
	private static volatile ConcurrentEventChannelAdapter mdx_iec = null;
	private static volatile ConcurrentEventChannelAdapter marketdata_iec = null;
	private static volatile InstrumentedConcurrentEventChannelAdapter cas_instrumented_iec = null;
	private static volatile InstrumentedConcurrentEventChannelAdapter fix2cas_instrumented_iec = null;
	private static volatile InstrumentedConcurrentEventChannelAdapter appia2fix_instrumented_iec = null;
	private static volatile InstrumentedConcurrentEventChannelAdapter fix2cas_conc_order_instrumented_iec = null;
	private static volatile InstrumentedConcurrentEventChannelAdapter fix2cas_conc_quote_instrumented_iec = null;
	private static volatile InstrumentedConcurrentEventChannelAdapter mdx_instrumented_iec = null;
	private static volatile InstrumentedConcurrentEventChannelAdapter marketdata_instrumented_iec = null;

	// TODO: change these to initialize these from properties.
	public static final int CAS_IEC_THREADPOOL_SIZE = 70;
	public static final int FIX2CAS_IEC_THREADPOOL_SIZE = 200;
	public static final int APPIA2FIX_IEC_THREADPOOL_SIZE = 70;
	public static final int FIX2CAS_CONC_ORDER_IEC_THREADPOOL_SIZE = 200;
	public static final int FIX2CAS_CONC_QUOTE_IEC_THREADPOOL_SIZE = 200;
	public static final int MARKETDATA_IEC_THREADPOOL_SIZE = 70;
	public static final int CAS_INSTRUMENTED_IEC_THREADPOOL_SIZE = 70;
	public static final int FIX2CAS_INSTRUMENTED_IEC_THREADPOOL_SIZE = 70;
	public static final int APPIA2FIX_INSTRUMENTED_IEC_THREADPOOL_SIZE = 70;
	public static final int FIX2CAS_CONC_ORDER_INSTRUMENTED_IEC_THREADPOOL_SIZE = 200;
	public static final int FIX2CAS_CONC_QUOTE_INSTRUMENTED_IEC_THREADPOOL_SIZE = 200;
	public static final int MDX_IEC_THREADPOOL_SIZE = 70;
	public static final int MDX_INSTRUMENTED_IEC_THREADPOOL_SIZE = 70;
	public static final int MARKETDATA_INSTRUMENTED_IEC_THREADPOOL_SIZE = 70;


	private static synchronized ConcurrentEventChannelAdapter tryCreateCasIEC() 
	{
	     if (cas_iec == null) {
	    	 cas_iec = new ConcurrentEventChannelAdapter(new ConcurrentQueue(), ChannelEventCacheFactory.createChannelEventCache(), 
					new ThreadPool(CAS_IEC_THREADPOOL_SIZE, "Cas_IEC_WT"));
	     }
	     return cas_iec;
	}
	 
	public static ConcurrentEventChannelAdapter getCasIEC() 
	{
		ConcurrentEventChannelAdapter s = cas_iec;
		if (s == null) {
		    s = tryCreateCasIEC();
		}
		return s;
	}

	private static synchronized ConcurrentEventChannelAdapter tryCreateFIx2CasIEC() 
	{
	     if (fix2cas_iec == null) {
	    	 fix2cas_iec = new ConcurrentEventChannelAdapter(new ConcurrentQueue(), ChannelEventCacheFactory.createChannelEventCache(), 
					new ThreadPool(FIX2CAS_IEC_THREADPOOL_SIZE, "Fix2Cas_IEC_WT"));
	     }
	     return fix2cas_iec;
	}
	 
	public static ConcurrentEventChannelAdapter getFix2CasIEC() 
	{
		ConcurrentEventChannelAdapter s = fix2cas_iec;
		if (s == null) {
		    s = tryCreateFIx2CasIEC();
		}
		return s;
	}

	private static synchronized ConcurrentEventChannelAdapter tryCreateAppia2FixIEC() 
	{
	     if (appia2fix_iec == null) {
	    	 appia2fix_iec = new ConcurrentEventChannelAdapter(new ConcurrentQueue(), ChannelEventCacheFactory.createChannelEventCache(), 
					new ThreadPool(APPIA2FIX_IEC_THREADPOOL_SIZE, "Appia2Fix_IEC_WT"));
	     }
	     return appia2fix_iec;
	}
	 
	public static ConcurrentEventChannelAdapter getAppia2FixIEC() 
	{
		ConcurrentEventChannelAdapter s = appia2fix_iec;
		if (s == null) {
		    s = tryCreateAppia2FixIEC();
		}
		return s;
	}

	private static synchronized ConcurrentEventChannelAdapter tryCreateFix2CasConcOrderIEC() 
	{
	     if (fix2cas_conc_order_iec == null) {
	    	 fix2cas_conc_order_iec = new ConcurrentEventChannelAdapter(new ConcurrentQueue(), ChannelEventCacheFactory.createChannelEventCache(), 
					new ThreadPool(FIX2CAS_CONC_ORDER_IEC_THREADPOOL_SIZE, "Fix2CasConcOrder_IEC_WT"));
	     }
	     return fix2cas_conc_order_iec;
	}
	 
	public static ConcurrentEventChannelAdapter getFix2CasConcOrderIEC() 
	{
		ConcurrentEventChannelAdapter s = cas_iec;
		if (s == null) {
		    s = tryCreateFix2CasConcOrderIEC();
		}
		return s;
	}

	private static synchronized ConcurrentEventChannelAdapter tryCreateFix2CasConcQuoteIEC() 
	{
	     if (fix2cas_conc_quote_iec == null) {
	    	 fix2cas_conc_quote_iec = new ConcurrentEventChannelAdapter(new ConcurrentQueue(), ChannelEventCacheFactory.createChannelEventCache(), 
					new ThreadPool(FIX2CAS_CONC_QUOTE_IEC_THREADPOOL_SIZE, "Fix2CasConcQuote_IEC_WT"));
	     }
	     return fix2cas_conc_quote_iec;
	}
	 
	public static ConcurrentEventChannelAdapter getFix2CasConcQuoteIEC() 
	{
		ConcurrentEventChannelAdapter s = fix2cas_conc_quote_iec;
		if (s == null) {
		    s = tryCreateFix2CasConcQuoteIEC();
		}
		return s;
	}

	private static synchronized ConcurrentEventChannelAdapter tryCreateMdxIEC() 
	{
	     if (mdx_iec == null) {
	    	 mdx_iec = new ConcurrentEventChannelAdapter(new ConcurrentQueue(), ChannelEventCacheFactory.createChannelEventCache(), 
					new ThreadPool(MDX_IEC_THREADPOOL_SIZE, "MDX_IEC_WT"));
	     }
	     return mdx_iec;
	}
	 
	public static ConcurrentEventChannelAdapter getMdxIEC() 
	{
		ConcurrentEventChannelAdapter s = mdx_iec;
		if (s == null) {
		    s = tryCreateMdxIEC();
		}
		return s;
	}

	private static synchronized ConcurrentEventChannelAdapter tryCreateMarketdataIEC()
	{
		if (marketdata_iec == null) {
		marketdata_iec = new ConcurrentEventChannelAdapter(new ConcurrentQueue(), ChannelEventCacheFactory.createChannelEventCache(),
		                  new ThreadPool(MARKETDATA_IEC_THREADPOOL_SIZE, "Marketdata_IEC_WT"));
		}
		return marketdata_iec;
	}

	private static ConcurrentEventChannelAdapter getMarketdataIEC()
	{
		ConcurrentEventChannelAdapter s = marketdata_iec;
		if (s == null) {
			s = tryCreateMarketdataIEC();
		}
		return s;
	}

	private static synchronized InstrumentedConcurrentEventChannelAdapter tryCreateCasInstrumentedIEC() 
	{
	     if (cas_instrumented_iec == null) {
	    	 cas_instrumented_iec = new InstrumentedConcurrentEventChannelAdapter(new InstrumentedConcurrentQueue("CasInstrumented_IEC"), ChannelEventCacheFactory.createChannelEventCache(), 
					new InstrumentedThreadPool(CAS_INSTRUMENTED_IEC_THREADPOOL_SIZE, "CasInstrumented_IEC_WT"));
	     }
	     return cas_instrumented_iec;
	}
	 
	public static InstrumentedConcurrentEventChannelAdapter getCasInstrumentedIEC() 
	{
		InstrumentedConcurrentEventChannelAdapter s = cas_instrumented_iec;
		if (s == null) {
		    s = tryCreateCasInstrumentedIEC();
		}
		return s;
	}

	private static synchronized InstrumentedConcurrentEventChannelAdapter tryCreateFix2CasInstrumentedIEC() 
	{
	     if (fix2cas_instrumented_iec == null) {
	    	 fix2cas_instrumented_iec = new InstrumentedConcurrentEventChannelAdapter(new InstrumentedConcurrentQueue("Fix2CasInstrumented_IEC"), ChannelEventCacheFactory.createChannelEventCache(), 
					new InstrumentedThreadPool(FIX2CAS_INSTRUMENTED_IEC_THREADPOOL_SIZE, "Fix2CasInstrumented_IEC_WT"));
	     }
	     return fix2cas_instrumented_iec;
	}
	 
	public static InstrumentedConcurrentEventChannelAdapter getFix2CasInstrumentedIEC() 
	{
		InstrumentedConcurrentEventChannelAdapter s = fix2cas_instrumented_iec;
		if (s == null) {
		    s = tryCreateFix2CasInstrumentedIEC();
		}
		return s;
	}

	private static synchronized InstrumentedConcurrentEventChannelAdapter tryCreateAppia2FixInstrumentedIEC() 
	{
	     if (appia2fix_instrumented_iec == null) {
	    	 appia2fix_instrumented_iec = new InstrumentedConcurrentEventChannelAdapter(new InstrumentedConcurrentQueue("Appia2FixInstrumented_IEC"), ChannelEventCacheFactory.createChannelEventCache(), 
					new InstrumentedThreadPool(APPIA2FIX_INSTRUMENTED_IEC_THREADPOOL_SIZE, "Appia2FixInstrumented_IEC_WT"));
	     }
	     return appia2fix_instrumented_iec;
	}
	 
	public static InstrumentedConcurrentEventChannelAdapter getAppia2FixInstrumentedIEC() 
	{
		InstrumentedConcurrentEventChannelAdapter s = appia2fix_instrumented_iec;
		if (s == null) {
		    s = tryCreateAppia2FixInstrumentedIEC();
		}
		return s;
	}

	private static synchronized InstrumentedConcurrentEventChannelAdapter tryCreateFix2CasConcOrderInstrumentedIEC() 
	{
	     if (fix2cas_conc_order_instrumented_iec == null) {
	    	 fix2cas_conc_order_instrumented_iec = new InstrumentedConcurrentEventChannelAdapter(new InstrumentedConcurrentQueue("Fix2CasConcOrderInstrumented_IEC"), ChannelEventCacheFactory.createChannelEventCache(), 
					new InstrumentedThreadPool(FIX2CAS_CONC_ORDER_INSTRUMENTED_IEC_THREADPOOL_SIZE, "Fix2CasConcOrderInstrumented_IEC_WT"));
	     }
	     return fix2cas_conc_order_instrumented_iec;
	}
	 
	public static InstrumentedConcurrentEventChannelAdapter getFix2CasConcOrderInstrumentedIEC() 
	{
		InstrumentedConcurrentEventChannelAdapter s = fix2cas_conc_order_instrumented_iec;
		if (s == null) {
		    s = tryCreateFix2CasConcOrderInstrumentedIEC();
		}
		return s;
	}

	private static synchronized InstrumentedConcurrentEventChannelAdapter tryCreateFix2CasConcQuoteInstrumentedIEC() 
	{
	     if (fix2cas_conc_quote_instrumented_iec == null) {
	    	 fix2cas_conc_quote_instrumented_iec = new InstrumentedConcurrentEventChannelAdapter(new InstrumentedConcurrentQueue("Fix2CasConcQuoteInstrumented_IEC"), ChannelEventCacheFactory.createChannelEventCache(), 
					new InstrumentedThreadPool(FIX2CAS_CONC_QUOTE_INSTRUMENTED_IEC_THREADPOOL_SIZE, "Fix2CasConcQuoteInstrumented_IEC_WT"));
	     }
	     return fix2cas_conc_quote_instrumented_iec;
	}
	 
	public static InstrumentedConcurrentEventChannelAdapter getFix2CasConcQuoteInstrumentedIEC() 
	{
		InstrumentedConcurrentEventChannelAdapter s = fix2cas_conc_quote_instrumented_iec;
		if (s == null) {
		    s = tryCreateFix2CasConcQuoteInstrumentedIEC();
		}
		return s;
	}

	private static synchronized ConcurrentEventChannelAdapter tryCreateMdxInstrumentedIEC() 
	{
	     if (mdx_instrumented_iec == null) {
	    	 mdx_instrumented_iec = new InstrumentedConcurrentEventChannelAdapter(new InstrumentedConcurrentQueue("MdxInstrumented_IEC"), ChannelEventCacheFactory.createChannelEventCache(), 
					new InstrumentedThreadPool(MDX_INSTRUMENTED_IEC_THREADPOOL_SIZE, "MdxInstrumented_IEC_WT"));
	     }
	     return mdx_instrumented_iec;
	}
	 
	public static ConcurrentEventChannelAdapter getMdxInstrumentedIEC() 
	{
		ConcurrentEventChannelAdapter s = mdx_instrumented_iec;
		if (s == null) {
		    s = tryCreateMdxInstrumentedIEC();
		}
		return s;
	}

	private static synchronized InstrumentedConcurrentEventChannelAdapter tryCreateMarketdataInstrumentedIEC()
	{
		if (marketdata_instrumented_iec == null) {
			marketdata_instrumented_iec = new InstrumentedConcurrentEventChannelAdapter(new InstrumentedConcurrentQueue("MarketdataInstrumented_IEC"), ChannelEventCacheFactory.createChannelEventCache(),
			                 new InstrumentedThreadPool(MARKETDATA_INSTRUMENTED_IEC_THREADPOOL_SIZE, "Marketdata_IEC_WT"));
		}
		return marketdata_instrumented_iec;
	}

	private static InstrumentedConcurrentEventChannelAdapter getMarketdataInstrumentedIEC()
	{
		InstrumentedConcurrentEventChannelAdapter s = marketdata_instrumented_iec;
		if (s == null) {
			s = tryCreateMarketdataInstrumentedIEC();
		}
		return s;
	}

	
	public static ConcurrentEventChannelAdapter findConcurrentEventChannelAdapter(int iecType) throws Exception
	{		
		switch (iecType)
		{
			case CAS_IEC:
				return getCasIEC();
			
			case FIX2CAS_IEC:
				return getFix2CasIEC();
			
			case APPIA2FIX_IEC:
				return getAppia2FixIEC();
			
			case FIX2CAS_CONC_ORDER_IEC:
				return getFix2CasConcOrderIEC();
			
			case FIX2CAS_CONC_QUOTE_IEC:
				return getFix2CasConcQuoteIEC();
			
			case MDX_IEC:
				return getMdxIEC();
			
			case MARKETDATA_IEC:
				return getMarketdataIEC();

			case CAS_INSTRUMENTED_IEC:
				return getCasInstrumentedIEC();
			
			case FIX2CAS_INSTRUMENTED_IEC:
				return getFix2CasInstrumentedIEC();
			
			case APPIA2FIX_INSTRUMENTED_IEC:
				return getAppia2FixInstrumentedIEC();
			
			case FIX2CAS_CONC_ORDER_INSTRUMENTED_IEC:
				return getFix2CasConcOrderInstrumentedIEC();
			
			case FIX2CAS_CONC_QUOTE_INSTRUMENTED_IEC:
				return getFix2CasConcQuoteInstrumentedIEC();
			
			case MDX_INSTRUMENTED_IEC:
				return getMdxInstrumentedIEC();
			
			case MARKETDATA_INSTRUMENTED_IEC:
				return getMarketdataInstrumentedIEC();

			default:
				throw new Exception("Unknown IEC type: " + iecType);
		}
	}


}
