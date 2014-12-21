package com.cboe.domain.messaging;

public class MessageCenterExample
{

	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable
	{
		MessageCenter messageCenter = MessageCenter.get();

		RegisteredMessageConsumer registeredConsumer = messageCenter.register(new HelloWorldMessageConsumer());

		final MyTopic topic = MyTopic.foo;

		registeredConsumer.subscribe(topic);

		MessagePublisher publisher = messageCenter.getPublisher(topic);
		publisher.publish(publisher.getMessage("Hello World."));

		registeredConsumer.receive(new TopicMessage(topic, "Hello World."));

		System.exit(0);
	}

	private static final class HelloWorldMessageConsumer implements MessageConsumer
	{

		@Override
		public void onMessage(Message message)
		{
			try
			{

				MyTopic topic = (MyTopic) message.getTopic();
				switch (topic)
				{
					case foo:
						Integer content = message.getContent();
						System.out.println(Integer.reverse(content));
				}
			}
			catch (Throwable t)
			{
				t.printStackTrace();
			}
		}

	}

	private enum MyTopic implements MessageTopic
	{
		foo
	}
}
