package com.cboe.common.utils;

/**
 * Represents a state that a {@link FSM} can be in
 */
public interface FSMState <T>
{
	/**
	 * Called prior to changing from <code>before</code> to <code>after</code> due to
	 * <code>event</code>
	 * 
	 * @return true will continue applying the state, false will reject the state change
	 */
	public boolean enterState(FSM<T> fsm, FSMState before, FSMState after, T event);

	/**
	 * Called after changing from <code>before</code> to <code>after</code> due to
	 * <code>event</code>. 
	 * 
	 * @return true will continue applying the state change
	 */
	public boolean exitState(FSM<T> fsm, FSMState before, FSMState after, T event);
}
