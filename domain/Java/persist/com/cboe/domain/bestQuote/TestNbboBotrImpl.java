package com.cboe.domain.bestQuote;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.PriceFactory;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.quote.ExternalQuoteSideStruct;
import com.cboe.interfaces.domain.Price;

public class TestNbboBotrImpl extends TestCase 
{
	public TestNbboBotrImpl(String p_str) {
		super(p_str);
	}
	
	public void testNoUpdate()
	{
		NbboBotrImpl impl = new NbboBotrImpl();
		assertEquals("Bid vols",  "", str(impl.getBidExchangeVolumes()));
		assertEquals("Bid price", PriceFactory.getNoPrice(), impl.getBidPrice());
		assertEquals("Ask vols",  "", str(impl.getAskExchangeVolumes()));
		assertEquals("Ask price", PriceFactory.getNoPrice(), impl.getAskPrice());
	}
	
	public void testBidUpdate()
	{
		NbboBotrImpl impl = new NbboBotrImpl();
		impl.update(newSide(true, 1.00, 1000,1000));
		assertEquals("Bid vols",  "E0:1000,E1:1000", str(impl.getBidExchangeVolumes()));
		assertEquals("Bid price", PriceFactory.create(1.00), impl.getBidPrice());
		assertEquals("Ask vols",  "", str(impl.getAskExchangeVolumes()));
		assertEquals("Ask price", PriceFactory.getNoPrice(), impl.getAskPrice());
	}
	
	public void testAskUpdate()
	{
		NbboBotrImpl impl = new NbboBotrImpl();
		impl.update(newSide(false, 1.00, 1000,1000));
		assertEquals("Bid vols",  "", str(impl.getBidExchangeVolumes()));
		assertEquals("Bid price", PriceFactory.getNoPrice(), impl.getBidPrice());
		assertEquals("Ask vols",  "E0:1000,E1:1000", str(impl.getAskExchangeVolumes()));
		assertEquals("Ask price", PriceFactory.create(1.00), impl.getAskPrice());
	}
	
	public void testAskVolumeOnlyUpdate()
	{
		NbboBotrImpl impl = new NbboBotrImpl();
		impl.update(newSide(false, 1.00, 1000,1000));
		impl.updateAskSide((Price)null, exchVols(1001,1001));
		assertEquals("Ask vols",  "E0:1001,E1:1001", str(impl.getAskExchangeVolumes()));
		assertEquals("Ask price", PriceFactory.create(1.00), impl.getAskPrice());
	}
	
	public void testAskVolumeOnlyUpdateWithExtra()
	{
		NbboBotrImpl impl = new NbboBotrImpl();
		impl.update(newSide(false, 1.00, 1000,1000));
		impl.updateAskSide((Price)null, exchVols(1001), exchVol("X",1002));
		assertEquals("Ask vols",  "E0:1001,X:1002", str(impl.getAskExchangeVolumes()));
		assertEquals("Ask price", PriceFactory.create(1.00), impl.getAskPrice());
	}
	
	public void testAskVolumeOnlyUpdateOnlyExtra()
	{
		NbboBotrImpl impl = new NbboBotrImpl();
		impl.update(newSide(false, 1.00, 1000,1000));
		impl.updateAskSide((Price)null, exchVols()/*null not supported?*/, exchVol("X",1002));
		assertEquals("Ask vols",  "X:1002", str(impl.getAskExchangeVolumes()));
		assertEquals("Ask price", PriceFactory.create(1.00), impl.getAskPrice());
	}
	
	public void testAskPriceOnlyUpdate_2arg()
	{
		NbboBotrImpl impl = new NbboBotrImpl();
		impl.update(newSide(false, 1.00, 1000,1000));
		impl.updateAskSide(price(2), null);
		assertEquals("Ask vols",  "E0:1000,E1:1000", str(impl.getAskExchangeVolumes()));
		assertEquals("Ask price", PriceFactory.create(2.00), impl.getAskPrice());
	}
	
	public void testAskPriceOnlyUpdate_3arg()
	{
		NbboBotrImpl impl = new NbboBotrImpl();
		impl.update(newSide(false, 1.00, 1000,1000));
		impl.updateAskSide(price(2), null, null);
		assertEquals("Ask vols",  "E0:1000,E1:1000", str(impl.getAskExchangeVolumes()));
		assertEquals("Ask price", PriceFactory.create(2.00), impl.getAskPrice());
	}
	
	public void testDoubleAskUpdate()
	{
		NbboBotrImpl impl = new NbboBotrImpl();
		impl.update(newSide(false, 1.00, 1000,1000));
		impl.update(newSide(false, 2.00, 5000));
		assertEquals("Ask vols",  "E0:5000", str(impl.getAskExchangeVolumes()));
		assertEquals("Ask price", PriceFactory.create(2.00), impl.getAskPrice());
		
		//again:
		assertEquals("Ask vols",  "E0:5000", str(impl.getAskExchangeVolumes()));
		assertEquals("Ask price", PriceFactory.create(2.00), impl.getAskPrice());
	}
	
	public void testDoubleAskLocalMktUpdate()
	{
		NbboBotrImpl impl = new NbboBotrImpl();
		impl.update(newSide(false, 1.00, 500,500,500));
		impl.updateAskSide(price(1.00), exchVols(), exchVol("CBOE",1000));
		assertEquals("Ask vols",  "CBOE:1000", str(impl.getAskExchangeVolumes()));
		impl.updateAskSide(price(1.00), exchVols(), exchVol("CBOE",1000));
		impl.updateAskSide(price(1.00), exchVols(), exchVol("CBOE",900));
		assertEquals("Ask vols",  "CBOE:900", str(impl.getAskExchangeVolumes()));
		assertEquals("Ask vols",  "CBOE:900", str(impl.getAskExchangeVolumes()));
		assertEquals("Ask price", PriceFactory.create(1.00), impl.getAskPrice());
		impl.updateAskSide(price(1.10), exchVols(1000), exchVol("CBOE",900));
		
		//again:
		assertEquals("Ask vols",  "E0:1000,CBOE:900", str(impl.getAskExchangeVolumes()));
		assertEquals("Ask price", PriceFactory.create(1.10), impl.getAskPrice());
	}
	
	public void testRaceCondition() throws Exception
	{
		final NbboBotrImpl impl = new NbboBotrImpl();
		final int numCalls = 1000000;
		final AtomicInteger numFailed = new AtomicInteger(0);
		class ManyUpdates extends Thread
		{
			int price;
			ManyUpdates(int p_price) { price = p_price; }
			@Override public void run()
			{
				for (int i=0; i < numCalls; i++)
				{
					impl.updateAskSide(price(price), exchVols(price*1000));
					NBBOStruct struct = impl.toNBBOStruct();
					if (struct.askPrice.whole*1000 != struct.askExchangeVolume[0].volume)
					{
//						System.out.println("i="+i+",tPrice="+price+": ask.p="
//								+struct.askPrice.whole + ", v="+struct.askExchangeVolume[0].volume);
						numFailed.incrementAndGet();
					}
				}
			}
		}
		
		ManyUpdates[] tUpdates = new ManyUpdates[3];
		for (int i = 0; i < tUpdates.length; i++) 
		{
			tUpdates[i] = new ManyUpdates(i+1);
		}
		for (int i = 0; i < tUpdates.length; i++) 
		{
			tUpdates[i].start();
		}
		for (int i = 0; i < tUpdates.length; i++) 
		{
			tUpdates[i].join();
		}

		assertEquals("num failed cases", 0, numFailed.intValue());
	}
	
	private Price price(double p) 
	{
		return PriceFactory.create(p);
	}

	private String str(ExchangeVolumeStruct[] p_exchVols) 
	{
		if (p_exchVols==null)
			return "null";
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < p_exchVols.length; i++) 
		{
			buf.append(p_exchVols[i].exchange).append(':').append(p_exchVols[i].volume);
			if (i+1  < p_exchVols.length)
				buf.append(',');
		}
		return buf.toString();
	}

	private ExternalQuoteSideStruct newSide(boolean p_isBid, double p_price, int... p_vols) 
	{
		ExternalQuoteSideStruct struct = new ExternalQuoteSideStruct();
		struct.sentTime = DateWrapper.convertToTime(System.currentTimeMillis());
		struct.side = p_isBid ? 'B' : 'A';
		struct.price = PriceFactory.create(p_price).toStruct();
		ExchangeVolumeStruct[] exchangeVolume = exchVols(p_vols);
		struct.exchangeVolume = exchangeVolume;
		return struct;
	}

	private ExchangeVolumeStruct[] exchVols(int... p_vols) {
		ExchangeVolumeStruct[] exchangeVolume = new ExchangeVolumeStruct[p_vols.length];
		for (int i = 0; i < p_vols.length; i++) 
		{
			exchangeVolume[i] = exchVol("E"+i, p_vols[i]);
		}
		return exchangeVolume;
	}

	private ExchangeVolumeStruct exchVol(String p_exch, int p_vol)
	{
		return new ExchangeVolumeStruct(p_exch, p_vol);
	}
}
