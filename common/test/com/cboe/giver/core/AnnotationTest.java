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

import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Test;

/**
 * Make sure annotations work to design
 */
public class AnnotationTest {

	@Test
	public void helperDetectsMissingInstrumentedActor() {
		Actor actor = mock(Actor.class);
		Assert.assertFalse(ActorAnnotationHelper.isInstrumented(actor));
	}

	@Test
	public void helperDetectsInstrumentedActor() {
		Actor actor = new IsInstrumented();
		Assert.assertTrue(ActorAnnotationHelper.isInstrumented(actor));
	}

	@Test
	public void helperDetectsInstrumentedMessage() {
		Message message = new IsMessageInstrumented();
		Assert.assertTrue(ActorAnnotationHelper.isInstrumented(message));
	}

	@Test
	public void helperDetectsMissingInstrumentedMessage() {
		Message message = mock(Message.class);
		Assert.assertFalse(ActorAnnotationHelper.isInstrumented(message));
	}

	@Test
	public void helperDetectsContainerWithInstrumentedAnnotation() {
		Message message = new IsMessageInstrumented();
		Container container = new Container(message);
		Assert.assertTrue(ActorAnnotationHelper.isInstrumented(container));
	}

	private class Container implements ContainerMessage {

		private Message original;

		public Container(Message original) {
			this.original = original;
		}

		@Override
		public Message getOriginalMessage() {
			// TODO Auto-generated method stub
			return original;
		}

	}

	@Instrumented
	private class IsMessageInstrumented implements Message {

	}

	@Instrumented
	private class IsInstrumented extends Actor {
		@Override
		protected void act(Message msg) {

		}
	}
}
