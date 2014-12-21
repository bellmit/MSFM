package com.cboe.common.utils;

/**
 * Defines a state that always allows transitions. This is a good class to base subclasses
 * that may only modifiy one of the states.
 */
public class SimpleState<T> implements FSMState <T>
{

	@Override
    public boolean enterState(FSM<T> fsm, FSMState before, FSMState after, T event)
    {
	    return true;
    }

	@Override
    public boolean exitState(FSM<T> fsm, FSMState before, FSMState after, T event)
    {
	    return true;
    }
	


}
