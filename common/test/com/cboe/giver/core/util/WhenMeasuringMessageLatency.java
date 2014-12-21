/*
Copyright (c) 2010 Ryan Eccles

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
package com.cboe.giver.core.util;

import org.junit.Assert;
import org.junit.Test;

import com.cboe.giver.core.DummyMessage;

public class WhenMeasuringMessageLatency {

	@Test
	public void defaultsShouldBeSetToOne() {
		MessageLatency latency = new MessageLatency(DummyMessage.class, false);
		latency.updateLatencyNanos(1000);

		Assert.assertEquals(1, latency.getMicrosecondsElapsed());
		Assert.assertEquals(1, latency.getCount());
		Assert.assertEquals(1, latency.getTotalCallCount());

	}

	@Test
	public void resetShouldResetCountersInLatencyObject() {
		MessageLatency latency = new MessageLatency(DummyMessage.class, false);

		latency.updateLatencyNanos(1000000);
		Assert.assertEquals(1, latency.getCount());
		Assert.assertEquals(1, latency.getTotalCallCount());
		latency.reset();
		Assert.assertEquals(0, latency.getCount());
		Assert.assertEquals(1, latency.getTotalCallCount());

	}

	@Test
	public void latencyObjectShouldMaintainCorrectUpdateCounts() {
		MessageLatency latency = new MessageLatency(DummyMessage.class, false);
		final int COUNT = 65;
		final int NANO_UPDATE = 100000;

		for (int i = 0; i < COUNT; i++) {
			latency.updateLatencyNanos(NANO_UPDATE);
		}

		// avg will be indpendent of the number
		Assert.assertEquals(NANO_UPDATE / 1000,
				latency.getMicrosecondsElapsed());
		Assert.assertEquals(COUNT, latency.getCount());
	}

	@Test
	public void latencyObjectShouldRecordAndRetrieveIfContainedMessage() {
		MessageLatency latency = new MessageLatency(DummyMessage.class, false);
		Assert.assertFalse(latency.isContainedInOtherMessage() == false);
	}

	@Test
	public void latencyObjectShouldRecordAndRetrieveMessageType() {
		MessageLatency latency = new MessageLatency(DummyMessage.class, false);
		Assert.assertSame(DummyMessage.class, latency.getMessageType());
	}

}
