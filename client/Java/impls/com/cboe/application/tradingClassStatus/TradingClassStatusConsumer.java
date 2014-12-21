/**
 * 
 */
package com.cboe.application.tradingClassStatus;

import java.util.HashMap;

import com.cboe.application.tradingClassStatus.TradingClassStatusQueryServiceImpl.UserSubscription;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * @author Arun Ramachandran Mar 25, 2010
 *
 */
class TradingClassStatusConsumer {

	private HashMap<String, UserSubscription> userSubscriptions;
	private final String className;

	/**
	 * @param userSubscriptions
	 */
	public TradingClassStatusConsumer(
			HashMap<String, UserSubscription> userSubscriptions) {
		this.userSubscriptions = userSubscriptions;
		className = this.getClass().getSimpleName();
	}
	
	public void notifyUsers(String groupName, final short status){
		UserSubscription subscription = null;
		try {
			subscription = userSubscriptions.get(groupName);
			if (subscription!=null) {
				subscription.notifyUser(status);
				if(Log.isDebugOn()) {
					Log.debug(className+" notifyusers called for group "+groupName+" status "+status);
				}
			}
		} catch (Exception e) {
			Log.exception(className+">>> Error notifying user:"+subscription,e);
		}
	}
}
