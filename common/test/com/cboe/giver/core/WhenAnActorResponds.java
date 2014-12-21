package com.cboe.giver.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

public class WhenAnActorResponds {
	/*
	 * can't do in order because mockito can't handle the behavior of the base
	 * class
	 */
	@Test
	public void preActAndPostActShouldBeCalledWithinRespond() {
		Actor actor = new DummyActor();
		Metrics metrics = mock(Metrics.class);
		actor.setMetrics(metrics);
		Message msg = null;

		actor.respond(msg);

		verify(metrics).preAct(msg);
		verify(metrics).postAct(msg);
	}

	@Test
	public void actShouldBeCalledViaRespond() throws Throwable {
		Actor actor = mock(Actor.class);
		Metrics metrics = new DummyMetrics();
		actor.setMetrics(metrics);
		Message msg = null;

		actor.respond(msg);

		verify(actor).act(msg);
	}

	@Test
	public void uncaughtExceptionsShouldBeSentToHandler() {
		final Throwable ex = new NullPointerException("BLARGAL!");

		Actor actor = new DummyActor() {
			@Override
			protected void act(Message msg) throws Throwable {
				throw ex;
			}
		};
		ExceptionHandler handler = mock(ExceptionHandler.class);
		actor.setExceptionHandler(handler);
		Message msg = null;

		actor.respond(msg);

		verify(handler).handleUncaught(actor, ex);
	}

}
