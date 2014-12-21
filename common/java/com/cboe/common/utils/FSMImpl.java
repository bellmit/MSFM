package com.cboe.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of the {@link FSM} interface
 */
@Usage.Concurrency.ThreadSafe
public class FSMImpl <T> implements FSM <T>
{

	@Usage.Concurrency.Locking("Should be accessed/changed by mutex")
	private FSMState currentState;
	private final Map<FSMState<T>, Transitions<T>> transitionMap;

	public FSMImpl()
	{
		/* O(1) for lookup */
		transitionMap = new HashMap<FSMState<T>, FSMImpl.Transitions<T>>();

	}

	@Override
	public synchronized FSMState<T> getCurrentState()
	{
		return currentState;
	}

	
	public synchronized void setCurrentState(FSMState<T> newState)
	{
		currentState = newState;
	}

	public FSMState<T> addState(FSMState<T> state)
	{
		Set<FSMState<T>> validStates = transitionMap.keySet();
		if (validStates.contains(state))
		{
			return state;
		}

		if (validStates.size() == 0)
		{
			setCurrentState(state);
		}
		transitionMap.put(state, new Transitions<T>());
		
		return state;

	}

	public void addTransition(FSMState<T> a, FSMState<T> b, T event) throws IllegalArgumentException
	{
		@Usage.Protocol("addState should ensure object defined in map")
		Transitions<T> trans = transitionMap.get(a);
		if (trans == null)
		{
			throw new IllegalArgumentException("State a not defined as a state. Must be added first");
		}
		if (transitionMap.containsKey(b) == false)
		{
			throw new IllegalArgumentException("State b not defined as a state. Must be added first");
		}

		trans.addTransition(event, b);
	}

	@Override
	public Result updateState(T event)
	{
		FSMState<T> currentState = getCurrentState();

		Transitions<T> trans = transitionMap.get(currentState);
		if (trans == null)
		{
			return Result.UnknownState;
		}

		FSMState<T> newState = trans.getTransition(event);
		if (newState == null)
		{
			return Result.NoEdgeToTransform;
		}

		/* tell the current state we are about to change */
		boolean oldStateIsOkayWithChange = currentState.exitState(this, currentState, newState, event);
		if (oldStateIsOkayWithChange == false)
		{
			return Result.RejectedTransform;
		}

		/* tell the current state we are about to enter */
		boolean newStateIsOkayWithChange = newState.enterState(this, currentState, newState, event);
		if (newStateIsOkayWithChange == false)
		{
			return Result.RejectedTransform;
		}

		/* finally update the state */
		setCurrentState(newState);

		return Result.Succesful;
	}

	/**
	 * Defines a mapping of events to states
	 */
	private static final class Transitions <T>
	{
		private final Map<T, FSMState> transitions;

		public Transitions()
		{
			this.transitions = new HashMap<T, FSMState>();
		}

		public FSMState getTransition(T event)
		{
			return transitions.get(event);
		}

		public void addTransition(T event, FSMState target)
		{
			transitions.put(event, target);
		}
	}

}
