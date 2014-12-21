package com.cboe.common.utils;

/**
 * An implementation of a finite state machine
 */
public interface FSM <T>
{

	public enum Result
	{
		/**
		 * State change worked
		 */
		Succesful,

		/**
		 * No idea what state this is
		 */
		UnknownState,

		/**
		 * Either the current or new State rejected the transform
		 */
		RejectedTransform,

		/**
		 * There is no path between the current state and the target state given the provided event
		 */
		NoEdgeToTransform;

	}

	/**
	 * The current applied state, will never be null.
	 */
	public FSMState<T> getCurrentState();
	
	/**
	 * Do not inform states of change. Cram in the state we want.
	 */
	public void setCurrentState(FSMState<T> state);
	

	/**
	 * Updates the state due to the event
	 * 
	 * @param event
	 * @return
	 */
	public Result updateState(T event);
}
