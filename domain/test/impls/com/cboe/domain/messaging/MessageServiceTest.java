package com.cboe.domain.messaging;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public abstract class MessageServiceTest
{
	private final MessageService service;

	public MessageServiceTest()
	{
		this.service = createMessageService();
	}

	protected abstract MessageService createMessageService();

	@Test
	public void consumerShouldReceiveMessagesForSubscribedTopic() throws Throwable
	{
		final TestTopic topic = TestTopic.TOPIC_1;
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		// create one consumer
		MessageConsumer consumer = mock(MessageConsumer.class);
		doAnswer(new CountDownAnswer(countDownLatch)).when(consumer).onMessage(any(Message.class));
		RegisteredMessageConsumer registeredConsumer = service.register(consumer);

		// subscribe one consumer for one topic
		registeredConsumer.subscribe(topic);

		// create one publisher for one topic
		MessagePublisher publisher = service.getPublisher(topic);

		// publish one message
		final Message message = mock(Message.class);
		publisher.publish(message);

		// wait for message to be delivered
		countDownLatch.await(10, TimeUnit.SECONDS);

		// verify the message was delivered to the consumer
		verify(consumer, times(1)).onMessage(message);
	}

	@Test
	public void consumerShouldNotReceiveMessagesForTopicsForWhichItHasNotSubscribed() throws Throwable
	{
		final TestTopic topic1 = TestTopic.TOPIC_1;
		final TestTopic topic2 = TestTopic.TOPIC_2;

		// create one consumer
		MessageConsumer consumer = mock(MessageConsumer.class);
		RegisteredMessageConsumer registeredConsumer = service.register(consumer);

		// subscribe one consumer for topic 1
		registeredConsumer.subscribe(topic1);

		// create one publisher for topic 2
		MessagePublisher publisher = service.getPublisher(topic2);

		// publish one message
		final Message message = mock(Message.class);
		publisher.publish(message);

		// wait for message delivery
		Thread.sleep(500);

		// verify the message was not delivered to the consumer
		verify(consumer, never()).onMessage(any(Message.class));
	}

	@Test
	public void consumerShouldReceiveManyMessagesWhenManyMessagesArePublishedForSameTopic() throws Throwable
	{
		final TestTopic topic = TestTopic.TOPIC_1;
		final int messageCount = 10;
		final CountDownLatch countDownLatch = new CountDownLatch(messageCount);

		// create one consumer
		MessageConsumer consumer = mock(MessageConsumer.class);
		doAnswer(new CountDownAnswer(countDownLatch)).when(consumer).onMessage(any(Message.class));
		RegisteredMessageConsumer registeredConsumer = service.register(consumer);

		// subscribe one consumer for one topic
		registeredConsumer.subscribe(topic);

		// create one publisher for one topic
		MessagePublisher publisher = service.getPublisher(topic);

		final List<Message> messages = new ArrayList<Message>();
		// publish 10 messages
		for (int i = 0; i < messageCount; i++)
		{
			final Message m = mock(Message.class);
			messages.add(m);
			publisher.publish(m);
		}

		// wait for message to be delivered
		countDownLatch.await(10, TimeUnit.SECONDS);

		// verify the message was delivered to the consumer
		ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
		verify(consumer, times(messageCount)).onMessage(captor.capture());

		final Object[] expecteds = messages.toArray();
		final Object[] actuals = captor.getAllValues().toArray();
		sort(expecteds, actuals);
		assertArrayEquals(expecteds, actuals);
	}

	@Test
	public void consumerShouldReceiveMessagesForAllTopicsWhichItSubscribed() throws Throwable
	{
		final TestTopic topic1 = TestTopic.TOPIC_1;
		final TestTopic topic2 = TestTopic.TOPIC_2;
		final int messageCount = 2;
		final CountDownLatch countDownLatch = new CountDownLatch(messageCount);

		// create one consumer
		MessageConsumer consumer = mock(MessageConsumer.class);
		doAnswer(new CountDownAnswer(countDownLatch)).when(consumer).onMessage(any(Message.class));
		RegisteredMessageConsumer registeredConsumer = service.register(consumer);

		// subscribe one consumer for two topics
		registeredConsumer.subscribe(topic1);
		registeredConsumer.subscribe(topic2);

		// create publishers for each topic
		MessagePublisher publisher1 = service.getPublisher(topic1);
		MessagePublisher publisher2 = service.getPublisher(topic2);

		final List<Message> messages = new ArrayList<Message>();
		// publish messages
		Message m = mock(Message.class);
		messages.add(m);
		publisher1.publish(m);

		m = mock(Message.class);
		messages.add(m);
		publisher2.publish(m);

		// wait for message to be delivered
		countDownLatch.await(10, TimeUnit.SECONDS);

		// verify the message was delivered to the consumer
		ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
		verify(consumer, times(messageCount)).onMessage(captor.capture());

		final Object[] expecteds = messages.toArray();
		final Object[] actuals = captor.getAllValues().toArray();
		sort(expecteds, actuals);
		assertArrayEquals(expecteds, actuals);
	}

	@Test
	public void consumerShouldNotReceiveMessagesForTopicsWhichItHasUnsubscribed() throws Throwable
	{
		final TestTopic topic1 = TestTopic.TOPIC_1;

		// create one consumer
		MessageConsumer consumer = mock(MessageConsumer.class);
		RegisteredMessageConsumer registeredConsumer = service.register(consumer);

		// subscribe one consumer for topic 1
		registeredConsumer.subscribe(topic1);

		// unsubscribe consumer for topic 1
		registeredConsumer.unsubscribe(topic1);

		// create one publisher for topic 1
		MessagePublisher publisher = service.getPublisher(topic1);

		// publish one message
		final Message message = mock(Message.class);
		publisher.publish(message);

		// wait for message delivery
		Thread.sleep(500);

		// verify the message was not delivered to the consumer
		verify(consumer, never()).onMessage(any(Message.class));
	}

	@Test
	public void registeredConsumerShouldReceiveMessagesPassedToItDirectlyViaReceive() throws Throwable
	{
		// create one consumer
		MessageConsumer consumer = mock(MessageConsumer.class);
		RegisteredMessageConsumer registeredConsumer = service.register(consumer);

		// send message directly to consumer
		final Message message = mock(Message.class);
		registeredConsumer.receive(message);

		// wait for message delivery
		Thread.sleep(500);

		// verify the message was not delivered to the consumer
		verify(consumer, times(1)).onMessage(message);
	}

	@Test(expected = IllegalStateException.class)
	public void registeredConsumerShouldThrowExceptionWhenSubscribeIsCalledAfterUnregister()
	{
		MessageConsumer consumer = mock(MessageConsumer.class);
		RegisteredMessageConsumer registeredConsumer = service.register(consumer);
		registeredConsumer.unregister();
		registeredConsumer.subscribe(TestTopic.TOPIC_1);
	}

	@Test(expected = IllegalStateException.class)
	public void registeredConsumerShouldThrowExceptionWhenUnsubscribeIsCalledAfterUnregister()
	{
		MessageConsumer consumer = mock(MessageConsumer.class);
		RegisteredMessageConsumer registeredConsumer = service.register(consumer);
		registeredConsumer.unregister();
		registeredConsumer.unsubscribe(TestTopic.TOPIC_1);
	}

	@Test(expected = IllegalStateException.class)
	public void registeredConsumerShouldThrowExceptionWhenReceiveIsCalledAfterUnregister() throws Throwable
	{
		MessageConsumer consumer = mock(MessageConsumer.class);
		RegisteredMessageConsumer registeredConsumer = service.register(consumer);
		registeredConsumer.unregister();
		registeredConsumer.receive(mock(Message.class));
	}

	@Test
	public void publisherShouldReturnTopicForWhichItWasCreated()
	{
		final TestTopic topic1 = TestTopic.TOPIC_1;

		// create one publisher for topic 1
		MessagePublisher publisher = service.getPublisher(topic1);

		assertThat((TestTopic) publisher.getTopic(), is(topic1));
	}

	private static void sort(final Object[]... arrays)
	{
		Comparator<Object> c = new Comparator<Object>()
		{
			@Override
			public int compare(Object o1, Object o2)
			{
				return o1.hashCode() == o2.hashCode() ? 0 : o1.hashCode() > o2.hashCode() ? 1 : -1;
			}
		};
		for (Object[] array : arrays)
		{
			Arrays.sort(array, c);
		}
	}

	private static final class CountDownAnswer implements Answer<Void>
	{
		private final CountDownLatch countDownLatch;

		private CountDownAnswer(CountDownLatch countDownLatch)
		{
			this.countDownLatch = countDownLatch;
		}

		@Override
		public Void answer(InvocationOnMock invocation) throws Throwable
		{
			countDownLatch.countDown();
			return null;
		}
	}

	private enum TestTopic implements MessageTopic
	{
		TOPIC_1,
		TOPIC_2;
	}
}
