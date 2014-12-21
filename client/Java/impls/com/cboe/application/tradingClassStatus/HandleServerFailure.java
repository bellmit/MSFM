/**
 * 
 */
package com.cboe.application.tradingClassStatus;

import java.util.HashMap;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * @author Arun Ramachandran May 10, 2010
 * 
 */
public final class HandleServerFailure {

	private static HandleServerFailure singletonInstance = null;
	private HashMap<String, GroupAlreadyNotified> alreadyNotified;
	//private TradingClassStatusSubscriptionsContainer subscriptions; 

	private int DO_NOT_NOTIFY_INTERVAL;

	private HandleServerFailure() {
		alreadyNotified = new HashMap<String, GroupAlreadyNotified>();
		
		DO_NOT_NOTIFY_INTERVAL = Integer.parseInt(System.getProperty(
				"ServerFailureNotificationInterval", "30000"));
		
	}

	public static synchronized HandleServerFailure getInstance() {
		if (singletonInstance == null) {
			return singletonInstance = new HandleServerFailure();
		}
		return singletonInstance;
	}

	public void notifyUsers(int group, short activityReason) {
		String grpName = ProductConfigurationQueryHelper.getInstance().getGroupNameForGroupKey(group);
		if (grpName != null) {
			GroupAlreadyNotified notified = alreadyNotified.get(grpName);
			if (notified != null) {
				if ((System.currentTimeMillis() - notified.getTimestamp()) > DO_NOT_NOTIFY_INTERVAL) {
					TradingClassStatusSubscriptionsContainer.getInstance().notifyUsers(grpName, activityReason);
					notified.setTimestamp(System.currentTimeMillis());
				}
			} else {
				TradingClassStatusSubscriptionsContainer.getInstance().notifyUsers(grpName, activityReason);
				notified = new GroupAlreadyNotified(grpName, System
						.currentTimeMillis());
				alreadyNotified.put(grpName, notified);
			}
		} else {
			Log
					.alarm("HandleServerFailure ->> Failed notifying users, GroupName not found for GroupKey :<"
							+ group + ">");
		}
	}

	private static class GroupAlreadyNotified {
		String groupname;
		long timestamp;

		GroupAlreadyNotified(String grpName, long timestamp) {
			this.groupname = grpName;
			this.timestamp = timestamp;
		}

		public String getGroupname() {
			return groupname;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		public String toString() {
			return "<" + groupname + " | " + timestamp + ">";
		}

	}
}
