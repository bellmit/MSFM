package com.cboe.common.log;

import java.util.concurrent.atomic.AtomicBoolean;

import com.cboe.common.utils.Usage;

/**
 * Represents a serious known problem that requires the attention of operations. This object should
 * be used sparingly. Alarms also have the characteristic of being "dismissable" so they will be
 * resolved by the machine from the operators perspective.
 * 
 * This uses a builder pattern to construct the alarms.
 * 
 * 
 * To use this object first build an alarm with appropriate parameters and "raise" it to the
 * attention of ops:
 * 
 * <pre>
 * <code>
 * 	Alarm alarm = new Alarm.Builder()
 * 			.description("There is a huge problem!")
 * 			.operationsRequiredAction("Call infra support immediately")
 * 			.consequence("The server will not be functional until resolved")
 * 			.build();
 * 	alarm.raiseAlarm();
 * </code>
 * </pre>
 * 
 * Once raised it can be resolved in a similar method:
 * 
 * <pre>
 * <code>
 * Resolution resolution = new Resolution(alarm)
 * 			.operationsRequiredAction("Inform infra support that the server has corrected itself")
 * 			.consequence("The server will transmit all information correctly");
 * resolution.raiseResolution();
 * </code>
 * </pre>
 * 
 */
public final class Alarm
{
	private static AlarmEventHandler eventHandler;

	protected static final String ALARM_EVENT_HANDLER = "Logging.Alarm.EventHandler";
	private static final AlarmEventHandler DEFAULT_HANDLER;
	/**
	 * if we have a -D bound then we will default to that instead of our simple logging version.
	 * 
	 */
	static
	{
		AlarmEventHandler candidate = null;
		try
		{
			String typeName = System.getProperty(ALARM_EVENT_HANDLER);
			Class<AlarmEventHandler> type = (Class<AlarmEventHandler>) Class.forName(typeName);
			candidate = type.newInstance();
		}
		catch (Throwable t)
		{
			// noop, just assign the default
		}
		if (candidate == null)
		{
			DEFAULT_HANDLER = new LoggerRaiseEventHandler();
		}
		else
		{
			DEFAULT_HANDLER = candidate;
		}
	}

	/**
	 * return a handler that will be used for sending alarms and resolutions to operations
	 * 
	 * Always returns a non-null handler
	 */
	protected synchronized static AlarmEventHandler getDefaultHandler()
	{
		if (eventHandler == null)
		{
			return DEFAULT_HANDLER;
		}

		return eventHandler;
	}

	/**
	 * Define an alternative event handler. This is a way of overriding what ever is bound in.
	 * 
	 * @param handler
	 */
	public synchronized static void setDefaultHandler(AlarmEventHandler handler)
	{
		if (handler == null)
		{
			return;
		}

		eventHandler = handler;
	}

	/**
	 * This builder is based on Josh Bloch's Builder pattern in "Effective Java", it is responsible
	 * for constructing Alarm objects tied into the backend infrastructure.
	 */
	public static class Builder
	{
		private String description = null;
		private String opsRequired = null;
		private String consequence = null;
		private AlarmEventHandler eventHandler = null;

		public Builder description(String value)
		{
			this.description = value;
			return this;
		}

		public Builder operationsRequiredAction(String value)
		{
			this.opsRequired = value;
			return this;
		}

		/**
		 * Protected because only should be called from test classes
		 */
		protected Builder eventHandler(AlarmEventHandler eventHandler)
		{
			this.eventHandler = eventHandler;
			return this;
		}

		public Builder consequence(String value)
		{
			this.consequence = value;
			return this;
		}

		public Alarm build()
		{
			if (description == null)
			{
				description = "";
			}
			if (opsRequired == null)
			{
				opsRequired = "";
			}
			if (consequence == null)
			{
				consequence = "";
			}
			if (eventHandler == null)
			{
				eventHandler = getDefaultHandler();
			}

			return new Alarm(this);
		}

	}

	private final String description;
	private final String operationsRequiredAction;
	private final String consequence;
	private final AlarmEventHandler handler;

	private final AtomicBoolean isRaised;
	private final AtomicBoolean isResolved;
	
	/**
	 * The builder is the only valid way of making an alarm since it knows what appropriate event
	 * handler to use.
	 */
	private Alarm(Builder builder)
	{
		this.handler = builder.eventHandler;
		this.description = builder.description;
		this.operationsRequiredAction = builder.opsRequired;
		this.consequence = builder.consequence;

		isRaised = new AtomicBoolean();
		isResolved = new AtomicBoolean();
	}


	protected final AlarmEventHandler getHandler()
	{
		return handler;
	}

	public String consequence()
	{
		return consequence;
	}

	public String description()
	{
		return description;
	}

	public String operationsRequiredAction()
	{
		return operationsRequiredAction;
	}

	/**
	 * Send the alarm out to whoever cares about such things.
	 */
	@Usage.Concurrency.ThreadSafe
	@Usage.SideEffect("resolutions can now be raised")
	public void broadcast()
	{
		if (isRaised.compareAndSet(false, true))
		{
			// we have not called this yet
			handler.handle(this);
		}

	}

	/**
	 * Alarm owns the resolution, and so also owns the forwarding of resolutions for handling.
	 */
	@Usage.Concurrency.ThreadSafe
	void handle(Resolution resolution)
	{
		// if we haven't raised the alarm there is no point in resolving it!
		if (isRaised() == false)
		{
			return;
		}
		// don't allow resolutions unless we are associated with them
		if (resolution.sourceAlarm() != this)
		{
			return;
		}

		// we only allow one resolution to an alarm
		if (isResolved.compareAndSet(false, true))
		{
			handler.handle(resolution);
		}
	}

	public boolean isRaised()
	{
		return isRaised.get();
	}

	/**
	 * Builds a resolution attached to this alarm.
	 */
	public Resolution.Builder resolutionBuilder()
	{
		return new Resolution.Builder(this);
	}

	@Override
	public String toString()
	{
		return String.format("description=%s consequences=%s ops=%s",  description(), consequence(), operationsRequiredAction());
	}

}
