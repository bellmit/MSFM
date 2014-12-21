package com.cboe.interfaces.domain.session;

import java.util.concurrent.ThreadPoolExecutor;

public interface TradingSessionThreadPoolHome {
	public static String HOME_NAME="TradingSessionThreadPoolHome";
	
	public ThreadPoolExecutor findTradingSessionThreadPool();
	public ThreadPoolExecutor findSessionTemplateThreadPool();
}
