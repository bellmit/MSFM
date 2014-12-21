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
package com.cboe.giver.core;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.mockito.InOrder;


public class WhenCallingAnActor {

	@Test
	public void metricsPresendShouldBeCalledBeforeSend() {
		Message message = null;
		Actor actor = new DummyActor();
		Metrics mockMetrics = mock(Metrics.class);
		Mailbox mockMailbox = mock(Mailbox.class);
		actor.setMetrics(mockMetrics);
		actor.setMailbox(mockMailbox);

		actor.send(message);

		InOrder inOrder = inOrder(mockMetrics, mockMailbox);
		inOrder.verify(mockMetrics).preSend(message);
		inOrder.verify(mockMailbox).send(message);
	}

	@Test
	public void metricsPostSendShouldBeCalledAfterSend() {
		Message message = null;
		Actor actor = new DummyActor();
		Metrics mockMetrics = mock(Metrics.class);
		Mailbox mockMailbox = mock(Mailbox.class);
		actor.setMetrics(mockMetrics);
		actor.setMailbox(mockMailbox);

		actor.send(message);

		InOrder inOrder = inOrder(mockMailbox, mockMetrics);
		inOrder.verify(mockMailbox).send(message);
		inOrder.verify(mockMetrics).postSend(message);
	}


}
