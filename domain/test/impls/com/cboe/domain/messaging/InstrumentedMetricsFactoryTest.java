package com.cboe.domain.messaging;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.Message;
import com.cboe.giver.core.Metrics;
import com.cboe.giver.core.MetricsFactory;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;

@RunWith(MockitoJUnitRunner.class)
public class InstrumentedMetricsFactoryTest
{
	@Mock
	private Actor actor;
	@Mock
	private Message message;

	@Before
	public void setup()
	{
		when(actor.toString()).thenReturn("Actor");
	}

	@Test
	public void metricsShouldRecordQueueInsrumentationForMessage()
	{
		final QueueInstrumentor qi = newQIMock();
		final MethodInstrumentor miSend = newMIMock();
		final MethodInstrumentor miAct = newMIMock();
		MetricsFactory metricsFactory = new InstrumentedMetricsFactory(newQIFactoryMock(qi), newMIFactoryMock(miSend, miAct));
		Metrics metrics = metricsFactory.buildMetricsFor(actor);

		Message m = metrics.preSend(message);
		metrics.postSend(m);
		m = metrics.preAct(m);
		metrics.postAct(m);

		assertThat(m, is(message));
		verify(qi, times(1)).incEnqueued(1);
		verify(qi, times(1)).incDequeued(1);
		verify(qi, times(2)).setCurrentSize(anyLong());
	}

	@Test
	public void metricsShouldRecordSendMethodInsrumentationForMessage()
	{
		final QueueInstrumentor qi = newQIMock();
		final MethodInstrumentor miSend = newMIMock();
		final MethodInstrumentor miAct = newMIMock();
		MetricsFactory metricsFactory = new InstrumentedMetricsFactory(newQIFactoryMock(qi), newMIFactoryMock(miSend, miAct));
		Metrics metrics = metricsFactory.buildMetricsFor(actor);

		Message m = metrics.preSend(message);
		metrics.postSend(m);
		m = metrics.preAct(m);
		metrics.postAct(m);

		verify(miSend, times(1)).incCalls(1);
		verify(miSend, times(1)).incMethodTime(anyLong());
	}

	@Test
	public void metricsShouldRecordActMethodInsrumentationForMessage()
	{
		final QueueInstrumentor qi = newQIMock();
		final MethodInstrumentor miSend = newMIMock();
		final MethodInstrumentor miAct = newMIMock();
		MetricsFactory metricsFactory = new InstrumentedMetricsFactory(newQIFactoryMock(qi), newMIFactoryMock(miSend, miAct));
		Metrics metrics = metricsFactory.buildMetricsFor(actor);

		Message m = metrics.preSend(message);
		metrics.postSend(m);
		m = metrics.preAct(m);
		metrics.postAct(m);

		verify(miAct, times(1)).beforeMethodCall();
		verify(miAct, times(1)).afterMethodCall();
		verify(miAct, times(1)).incCalls(1);
	}

	private MethodInstrumentor newMIMock()
	{
		MethodInstrumentor mi = mock(MethodInstrumentor.class);
		return mi;
	}

	private MethodInstrumentorFactory newMIFactoryMock(MethodInstrumentor miSend, MethodInstrumentor miAct)
	{
		MethodInstrumentorFactory miFactory = mock(MethodInstrumentorFactory.class);
		when(miFactory.getInstance(eq(actor.toString() + "_send"), anyObject())).thenReturn(miSend);
		when(miFactory.getInstance(eq(actor.toString() + "_act"), anyObject())).thenReturn(miAct);
		return miFactory;
	}

	private QueueInstrumentor newQIMock()
	{
		QueueInstrumentor qi = mock(QueueInstrumentor.class);
		return qi;
	}

	private QueueInstrumentorFactory newQIFactoryMock(QueueInstrumentor newQIMock)
	{
		QueueInstrumentorFactory qiFactory = mock(QueueInstrumentorFactory.class);
		when(qiFactory.getInstance(eq(actor.toString()), anyObject())).thenReturn(newQIMock);
		return qiFactory;
	}
}
