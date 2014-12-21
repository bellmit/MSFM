package com.cboe.giver.core;

import junit.framework.Assert;

import org.junit.Test;

public class WhenBuildingAnActor {

	@Test
	public void shouldUseNullMailboxAsDefault() {
		Actor actor = new DummyActor();
		Assert.assertSame(NullMailbox.getInstance(), actor.getMailbox());

		actor.toString();
	}

	@Test
	public void shouldUseNullMetricsAsDefault() {
		Actor actor = new DummyActor();
		Assert.assertSame(NullMetrics.getInstance(), actor.getMetrics());
	}

	@Test
	public void shouldUseSystemExeceptionHandlerAsDefault() {
		Actor actor = new DummyActor();
		Assert.assertSame(SystemErrExceptionHandler.getInstance(),
				actor.getExceptionHandler());
	}

	@Test
	public void shouldUseProvidedMailbox() {
		Mailbox mailbox = new DummyMailbox();
		Actor actor = new DummyActor();
		actor.setMailbox(mailbox);

		Assert.assertSame(mailbox, actor.getMailbox());
	}

	@Test
	public void shouldUseProvidedMetrics() {
		Metrics metrics = new DummyMetrics();
		Actor actor = new DummyActor();
		actor.setMetrics(metrics);

		Assert.assertSame(metrics, actor.getMetrics());
	}

	@Test
	public void shouldUseProvidedExceptionHandler() {
		ExceptionHandler handler = new DummyExceptionHandler();
		Actor actor = new DummyActor();
		actor.setExceptionHandler(handler);

		Assert.assertSame(handler, actor.getExceptionHandler());
	}

}
