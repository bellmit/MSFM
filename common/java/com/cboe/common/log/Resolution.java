package com.cboe.common.log;

import com.cboe.common.utils.Usage;

/**
 * A resolution is associated with an alarm. It is produced from an alarm when a previously created
 * alarm is resolved as complete.
 */
public final class Resolution
{
	private final Alarm parentAlarm;

	private final String action;

	private final String consequence;

	/**
	 * This builder is based on Josh Bloch's Builder pattern in "Effective Java", it is responsible
	 * for constructing Resolution object for a particular alarm
	 */
	public static class Builder
	{
		private final Alarm parentAlarm;
		private String action;
		private String consequence;

		protected Builder(Alarm parentAlarm)
		{
			if (parentAlarm == null)
			{
				throw new NullPointerException("Cannot build resolution for Null alarm");
			}
			this.parentAlarm = parentAlarm;
		}

		public Builder consequence(String value)
		{
			this.consequence = value;
			return this;
		}

		public Builder operationsRequiredAction(String value)
		{
			this.action = value;
			return this;
		}

		public Resolution build()
		{
			/*
			 * make sure we have decent defaults
			 */
			if (consequence == null)
			{
				consequence = "";
			}
			if (action == null)
			{
				action = "";
			}

			return new Resolution(this);
		}
	}

	private Resolution(Builder builder)
	{
		this.parentAlarm = builder.parentAlarm;
		this.action = builder.action;
		this.consequence = builder.consequence;

	}

	/**
	 * Send the resolution out to whoever cares about such things.
	 */
	@Usage.Concurrency.ThreadSafe
	@Usage.Protocol("Associated alarm must be raised prior to call")
	public void broadcast()
	{
		/*
		 * a bit wacky that we route this around, but it makes the code look correct from the users
		 * perspective
		 */
		sourceAlarm().handle(this);
	}

	/**
	 * who made who?
	 */
	public Alarm sourceAlarm()
	{
		return parentAlarm;
	}

	public String consequence()
	{
		return consequence;
	}

	public String operationsRequiredAction()
	{
		return action;
	}

	@Override
	public String toString()
	{
		return String.format("ALARM CLEARED action=%s,consequence=%s alarm[%s]", operationsRequiredAction(), consequence(), sourceAlarm().toString());
	}
}
