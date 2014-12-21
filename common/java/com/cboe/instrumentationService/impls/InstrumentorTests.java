package com.cboe.instrumentationService.impls;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Run unit tests across expected values of instrumentors.
 */
public class InstrumentorTests extends TestCase
{
	public static Test suite()
	{
		return new TestSuite(InstrumentorTests.class);
	}

	public InstrumentorTests(String name)
	{
		super(name);
	}

	public void testNetworkConnectionInstrumentor()
	{

		long time = 100010000; // 1970/01/01 21:46:50

		String expectedString = "name,false,2,1,9,8,11,10,7,6,3,4,5,0,1970/01/01 21:46:50,1970/01/01 21:46:50,1970/01/01 21:46:50,1970/01/01 21:46:50,java.lang.Exception: Baddy,1,UserData";

		NetworkConnectionInstrumentorImpl inst = new NetworkConnectionInstrumentorImpl("Test", "UserData");
		inst.setBytesReceived(1);
		inst.setBytesSent(2);
		inst.setConnects(3);
		inst.setDisconnects(4);
		inst.setExceptions(5);
		inst.setGarbageBytesReceived(6);
		inst.setInvalidPacketsReceived(7);
		inst.setLastException(new Exception("Baddy"));
		inst.setLastConnectTime(time);
		inst.setLastDisconnectTime(time);
		inst.setLastExceptionTime(time);
		inst.setLastTimeReceived(time);
		inst.setMsgsReceived(8);
		inst.setMsgsSent(9);
		inst.setPacketsReceived(10);
		inst.setPacketsSent(11);
		inst.setStatus((short) 1);

		String value = inst.toString(true, true, "name");
		Assert.assertEquals(expectedString, value);

	}

	public void testQueueInstrumentor()
	{
		long time = 100010000; // 1970/01/01 21:46:50

		String expectedString = "name,false,5,2,10,13,11,1,0,12,1970/01/01 21:46:50,6,4,3,UserData";

		QueueInstrumentorImpl inst = new QueueInstrumentorImpl("Test", "UserData");
		inst.setCurrentSize(1);
		inst.setDequeued(2);
		inst.setDequeueTimeouts(3);
		inst.setDequeueWaits(4);
		inst.setEnqueued(5);
		inst.setEnqueueWaits(6);
		inst.setExceptions(7);
		inst.setFlips(8);
		inst.setFlipVolume(9);
		inst.setFlushed(10);
		inst.setHighWaterMark(11);
		inst.setLastException(new Exception("Baddy"));
		inst.setOverallHighWaterMark(12);
		inst.setOverallHighWaterMarkTime(time);
		inst.setOverlaid(13);

		String value = inst.toString(true, true, "name");
		Assert.assertEquals(expectedString, value);
	}

	/**
	 * Tests to make sure our formatter is correct
	 * @throws InterruptedException 
	 */
	public void testFormatterCorrectness() throws InterruptedException
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		long time = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++)
		{
			 
			// compare new tool against old way of doing things
			String expectedValue = format.format(new Date(time));
			
			Assert.assertEquals(InstrumentorTimeFormatter.format(time), expectedValue);
			time += 500; // simulated time
		}
	}

}
