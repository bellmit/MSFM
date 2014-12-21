package com.cboe.common.utils;

import org.junit.Assert;
import org.junit.Test;

import com.cboe.common.utils.FSM.Result;

/**
 * make sure the FSM works as expected
 */
public class FSMImplTests
{

	private StartState start = new StartState();
	private MiddleState middle = new MiddleState();
	private EndState end = new EndState();

	private EnterRejectState enterReject = new EnterRejectState();
	private ExitRejectState exitReject = new ExitRejectState();

	@Test
	public void initialStateShouldBeFirst()
	{
		FSM fsm = buildStandardFSM();

		Assert.assertSame(start, fsm.getCurrentState());
	}

	/**
	 * Does a valid set of state transitions to make sure we can make it through all of them.
	 */
	@Test
	public void normalTransitions()
	{
		FSM fsm = buildStandardFSM();

		fsm.updateState(Event.StartToMiddle);
		Assert.assertSame(fsm.getCurrentState(), middle);

		fsm.updateState(Event.MiddleToEnd);
		Assert.assertSame(fsm.getCurrentState(), end);

		fsm.updateState(Event.EndToMiddle);
		Assert.assertSame(fsm.getCurrentState(), middle);

	}

	/**
	 * Does a transform that is not defined. Make sure that it's correct.
	 */
	@Test
	public void invalidTransform()
	{
		FSM fsm = buildStandardFSM();

		/* should not be possible */
		Result result = fsm.updateState(Event.MiddleToEnd);

		Assert.assertSame(result, Result.NoEdgeToTransform);
		Assert.assertSame(fsm.getCurrentState(), start);

	}

	@Test
	public void enterRejected()
	{
		FSM fsm = buildStandardFSM();

		/* should not be possible */
		Result result = fsm.updateState(Event.RejectEnter);

		Assert.assertSame(result, Result.RejectedTransform);
		Assert.assertSame(fsm.getCurrentState(), start);
	}

	@Test
	public void exitRejected()
	{
		FSM fsm = buildStandardFSM();

		/* should not be possible */
		Result result = fsm.updateState(Event.RejectLeave);

		Assert.assertSame(result, Result.Succesful);
		Assert.assertSame(fsm.getCurrentState(), exitReject);

		/* here's the tricky bit, we can't leave even though we have path back */
		result = fsm.updateState(Event.RejectLeaveStart);
		Assert.assertSame(result, Result.RejectedTransform);
		Assert.assertSame(fsm.getCurrentState(), exitReject);

	}

	/**
	 * Builds a nice test finite state machine we can use for our testing.
	 */
	private FSMImpl buildStandardFSM()
	{
		FSMImpl fsm = new FSMImpl();

		fsm.addState(start);
		fsm.addState(middle);
		fsm.addState(end);
		fsm.addState(enterReject);
		fsm.addState(exitReject);

		fsm.addTransition(start, middle, Event.StartToMiddle);
		fsm.addTransition(middle, end, Event.MiddleToEnd);
		fsm.addTransition(end, middle, Event.EndToMiddle);

		fsm.addTransition(start, enterReject, Event.RejectEnter);
		fsm.addTransition(start, exitReject, Event.RejectLeave);
		fsm.addTransition(exitReject, start, Event.RejectLeaveStart);

		return fsm;
	}

	/**
	 * Events we can translate
	 */
	enum Event
	{
		/* normal */
		StartToMiddle, MiddleToEnd, EndToMiddle,

		/* these will reject transforms */
		RejectEnter, RejectLeave, RejectLeaveStart;

	}

	/**
	 * A place to start
	 */
	public class StartState implements FSMState<Event>
	{
		public boolean enterState(FSM fsm, FSMState before, FSMState after, Event event)
		{
			System.out.println("pre" + getClass().getSimpleName());
			return true;
		}

		public boolean exitState(FSM fsm, FSMState before, FSMState Event, Event event)
		{
			System.out.println("post" + getClass().getSimpleName());
			return true;
		}
	}

	/**
	 * A place to transition to
	 */
	public class MiddleState implements FSMState<Event>
	{
		public boolean enterState(FSM fsm, FSMState before, FSMState after, Event event)
		{
			System.out.println("pre" + getClass().getSimpleName());
			return true;
		}

		public boolean exitState(FSM fsm, FSMState before, FSMState after, Event event)
		{
			System.out.println("post" + getClass().getSimpleName());
			return true;
		}
	}

	/**
	 * A place to end up
	 */
	public class EndState implements FSMState<Event>
	{
		public boolean enterState(FSM fsm, FSMState before, FSMState after, Event event)
		{
			System.out.println("pre" + getClass().getSimpleName());
			return true;
		}

		public boolean exitState(FSM fsm, FSMState before, FSMState after, Event event)
		{
			System.out.println("post" + getClass().getSimpleName());
			return true;
		}
	}

	/**
	 * This state will reject all attempts to transition into it
	 */
	public class EnterRejectState implements FSMState<Event>
	{

		@Override
		public boolean enterState(FSM fsm, FSMState before, FSMState after, Event event)
		{

			System.out.println("pre" + getClass().getSimpleName() + "REJECTING!");
			return false;
		}

		@Override
		public boolean exitState(FSM fsm, FSMState before, FSMState after, Event event)
		{
			System.out.println("post" + getClass().getSimpleName());
			return true;
		}
	}

	/**
	 * This state will reject all attempts to transition away from it (note for the test this will
	 * hold the state indefinitely)
	 */
	public class ExitRejectState implements FSMState<Event>
	{

		@Override
		public boolean enterState(FSM fsm, FSMState before, FSMState after, Event event)
		{

			System.out.println("pre" + getClass().getSimpleName());
			return true;
		}

		@Override
		public boolean exitState(FSM fsm, FSMState before, FSMState after, Event event)
		{
			System.out.println("post" + getClass().getSimpleName() + "REJECTING!");
			return false;
		}
	}

}
