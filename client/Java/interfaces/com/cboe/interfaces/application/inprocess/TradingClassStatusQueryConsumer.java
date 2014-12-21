package com.cboe.interfaces.application.inprocess;

public interface TradingClassStatusQueryConsumer {

	public void acceptTradingClassStatusUpdateforProductGroups(
			java.lang.String[] listOfProductGroups, short status);

	public void acceptTradingClassStatusUpdateforClasses(int[] listOfClasses,
			short status);
}
