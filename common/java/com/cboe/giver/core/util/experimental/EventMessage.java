package com.cboe.giver.core.util.experimental;

import com.cboe.giver.core.Message;

/**
 * Define a message for this event system
 */
class EventMessage implements Message {
	private final int value;
	private final EventSystem parent;

	/** */
	public EventMessage(int value, EventSystem parent) {
		this.value = value;
		this.parent = parent;
	}

	public EventSystem getEventSystem() {
		return parent;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

}