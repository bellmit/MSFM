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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import junit.framework.Assert;

import org.junit.Test;

public class DirectorTest
{

	@Test
	public void shutdownShouldStopGeneralMailrooms()
	{
		Mailroom mailroom = mock(Mailroom.class);
		DummyActor actor = new DummyActor();

		Director director = new Director.Builder().overrideMailroom(DummyActor.class, mailroom).build();
		director.attach(actor);

		verify(mailroom).start();
		director.shutdown();
		verify(mailroom).stop();
	}

	@Test
	public void shutdownShouldStopSpecificMailrooms()
	{
		Mailroom mailroomB = mock(DummyMailroom.class);
		DummyActor actorB = new DummyActor();

		Director director = new Director.Builder().build();

		director.attach(actorB, mailroomB);

		verify(mailroomB).start();
		director.shutdown();
		verify(mailroomB).stop();
	}

	@Test
	public void defaultBuildShouldAlwaysProduceADirector()
	{
		Director director = new Director.Builder().build();
		Assert.assertNotNull(director);

		director.toString();
	}

	@Test
	public void actorAttachedToDirectorShouldBeProvidedDefaultExceptionHandlerMailboxAndMetrics()
	{
		Director director = new Director.Builder().build();
		Actor mock = mock(Actor.class);
		director.attach(mock);

		/* make sure all of the methods we want called are in fact called */
		verify(mock, times(1)).setExceptionHandler(director.getExceptionHandler());
		verify(mock, times(1)).setMailbox(any(Mailbox.class));
		verify(mock, times(1)).setMetrics(any(Metrics.class));

	}

	@Test
	public void directorShouldAttachSpecifiedMailboxToActorWhenDefined()
	{
		DummyMailroom mailroom = new DummyMailroom();
		DummyMailbox expectedMailbox = DummyMailroom.mailbox;

		Director director = new Director.Builder().build();
		Actor mock = new DummyActor();
		director.attach(mock, mailroom);
		Assert.assertSame(expectedMailbox, mock.getMailbox());
	}

	/*
	 * it is legal to call stop on something not started.
	 */
	@Test
	public void detachingAnActorShouldInstallNullImplementationsOfMailboxMetricsAndExceptionHandler()
	{
		Director director = new Director.Builder().build();
		Actor actor = mock(Actor.class);
		director.detach(actor);
		verify(actor).setMailbox(NullMailbox.getInstance());
		verify(actor).setMetrics(NullMetrics.getInstance());
		verify(actor).setExceptionHandler(SystemErrExceptionHandler.getInstance());
	}

	@Test
	public void mailroomsShouldBeStartedAfterDirectorBuild()
	{
		Mailroom mailroom = mock(Mailroom.class);
		new Director.Builder().defaultMailroom(mailroom).build();
		verify(mailroom).start();
	}

	@Test
	public void mailroomsShouldBeShutdownAfterDirectorShutdown()
	{
		Mailroom mailroom = mock(Mailroom.class);
		Director director = new Director.Builder().defaultMailroom(mailroom).build();
		director.shutdown();
		verify(mailroom).stop();
	}

	@Test
	public void actorShouldTakeProvidedMailboxWhenSpecifiedByClass()
	{
		Mailroom defaultMailroom = mock(Mailroom.class);
		Mailroom overrideMailroom = mock(Mailroom.class);

		Actor defaultActor = mock(Actor.class);
		TestActor extendedActor = mock(TestActor.class);

		Director director = new Director.Builder().defaultMailroom(defaultMailroom).overrideMailroom(TestActor.class, overrideMailroom).build();

		director.attach(defaultActor);
		director.attach(extendedActor);

		/*
		 * now we would expect the default actor to get assigned our default mailroom, and our other
		 * dude to get the overridden one Since the mailrooms are actually just factories we will
		 * just test to make sure they were called with the appropriate actor
		 */
		verify(defaultMailroom).buildMailbox(defaultActor);
		verify(overrideMailroom).buildMailbox(extendedActor);
	}

	@Test
	public void actorShouldReceiveNewMetricsFactoryOnAttachToDirector()
	{
		MetricsFactory metricsFactory = mock(MetricsFactory.class);

		Director director = new Director.Builder().metricsFactory(metricsFactory).build();

		Actor actor = mock(Actor.class);
		director.attach(actor);

		verify(metricsFactory).buildMetricsFor(actor);
	}

	@Test
	public void builderShouldNotFailForAllChainedBuilderCalls()
	{
		Mailroom defaultMailroom = mock(Mailroom.class);
		Mailroom overrideMailroom = mock(Mailroom.class);
		ExceptionHandler exception = mock(ExceptionHandler.class);
		MetricsFactory metricsFactory = mock(MetricsFactory.class);

		new Director.Builder().metricsFactory(metricsFactory).defaultMailroom(defaultMailroom).exceptionHandler(exception)
		        .overrideMailroom(TestActor.class, overrideMailroom).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void builderShouldFailForNullDefaultMailroom()
	{
		new Director.Builder().defaultMailroom(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void builderShouldFailForNullMetricsFactory()
	{
		new Director.Builder().metricsFactory(null).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void builderShouldFailForNullMailboxOverride()
	{
		new Director.Builder().overrideMailroom(Actor.class, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void builderShouldFailforNullExceptionHandler()
	{
		new Director.Builder().exceptionHandler(null).build();
	}

	@Test(expected = NullPointerException.class)
	public void directorShouldFailForAttachingNullActor()
	{
		Director d = new Director.Builder().build();
		d.attach(null);
	}

	@Test(expected = NullPointerException.class)
	public void directorShouldFailforStoppingNullActor()
	{
		Director d = new Director.Builder().build();
		d.detach(null);
	}

	@Test
	public void directorShouldOnlyAllowActorToAttachOnce()
	{
		Director d = new Director.Builder().build();

		Actor actor = mock(Actor.class);

		d.attach(actor);
		d.attach(actor);

		verify(actor, times(1)).setMailbox(any(Mailbox.class));
	}

	@Test
	public void directorShouldAllowAttachDetachThenAttachAgain()
	{
		Director d = new Director.Builder().build();

		Actor actor = mock(Actor.class);

		d.attach(actor);
		d.detach(actor);
		d.attach(actor);

		verify(actor, times(3)).setMailbox(any(Mailbox.class));
	}

	/**
	 * The override test requires we have a different class signature. mocking just won't cut it
	 * alone
	 */
	private static class TestActor extends Actor
	{
		@Override
		protected void act(Message msg)
		{
		}

	}

}
