package com.cboe.domain.messaging;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows consumers to use the {@link ForTopic} annotation to specify methods to be called for each
 * topic the consumer subscribes to. The method must be public void and it must have one parameter
 * of the type {@link Message}. If the consumer receives a message for a topic for which it has not
 * specified a method with the annotation an <tt>IllegalAccessError</tt> will be thrown.
 * 
 * @author morrow
 * 
 */
public class AnnotatedConsumerProxy implements MessageConsumer
{
	private final Map<Topic, Method> methodMap = new HashMap<Topic, Method>();
	private final Object annotatedConsumer;

	public AnnotatedConsumerProxy(Object annotatedConsumer)
	{
		this.annotatedConsumer = annotatedConsumer;
		Method[] methods = annotatedConsumer.getClass().getMethods();
		for (Method method : methods)
		{
			if (method.isAnnotationPresent(ForTopic.class))
			{
				ForTopic forTopic = method.getAnnotation(ForTopic.class);
				Topic[] topics = forTopic.value();
				for (Topic topic : topics)
				{
					methodMap.put(topic, method);
				}
			}
		}
	}

	@Override
	public void onMessage(Message message) throws Throwable
	{
		Method method = methodMap.get(message.getTopic());
		if (method == null)
			throw new IllegalAccessError("No method found on consumer:" + annotatedConsumer + " for Topic:" + message.getTopic());
		method.invoke(annotatedConsumer, message);
	}

}
