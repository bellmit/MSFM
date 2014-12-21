/**
 * 
 */
package com.cboe.application.tradingClassStatus;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * @author Arun Ramachandran Mar 25, 2010
 *
 */
public class TradingClassStatusSubscriptionsContainer {

	private static TradingClassStatusSubscriptionsContainer instance;
	private final ConcurrentHashMap<String, ArrayList<TradingClassStatusConsumer>> subscriptions; 
	
	private TradingClassStatusSubscriptionsContainer() {
		super();
		subscriptions = new ConcurrentHashMap<String, ArrayList<TradingClassStatusConsumer>>() ; 
	}
	
	public synchronized static TradingClassStatusSubscriptionsContainer getInstance() {
		if(instance == null) {
			instance = new TradingClassStatusSubscriptionsContainer();
		}
		return instance;
	}
	
	public void addSubscritpion(String groupName, TradingClassStatusConsumer consumer ){
		ArrayList<TradingClassStatusConsumer> groupSubscription = subscriptions.get(groupName);
		if(groupSubscription != null) {
			groupSubscription.add(consumer);
		}else {
			groupSubscription = new ArrayList<TradingClassStatusConsumer>();
			groupSubscription.add(consumer);
			subscriptions.put(groupName, groupSubscription);
		}
	}
	
	
	public void notifyUsers(String groupName, final short status){
		try {
			ArrayList<TradingClassStatusConsumer> groupSubscription = subscriptions.get(groupName);
			if (groupSubscription!=null) {
				for (int i = 0; i < groupSubscription.size(); i++) {
					TradingClassStatusConsumer userSubscription = groupSubscription.get(i);
					if(userSubscription !=null) {
						userSubscription.notifyUsers(groupName, status);
					}
				}
			}	
			if(Log.isDebugOn()) {
				Log.debug("Group Name : "+groupName+" with status : "+status+" called");
			}
		} catch (Exception e) {
			Log.exception(this.getClass().getSimpleName()+">>> Error while notifying all users subscribed for group "+groupName ,e);
		}
	}
}
