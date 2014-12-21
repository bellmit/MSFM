/**
 * 
 */
package com.cboe.application.tradingClassStatus;

import java.util.HashMap;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallbackV5.CMITradingClassStatusQueryConsumer;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.TradingClassStatusQueryService;
import com.cboe.interfaces.application.inprocess.TradingClassStatusQueryConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ExceptionBuilder;

/**
 * @author Arun Ramachandran Nov 16, 2009
 *
 */
public class TradingClassStatusQueryServiceImpl extends BObject implements
		TradingClassStatusQueryService {

	private static final String OPEN_BRACKET = ":<";
	private static final String CLOSE_BRACKET = ">";
	private static String INVALID_GRP = "Invalid group(s)";

	protected BaseSessionManager sessionManager;
	protected HashMap<String, UserSubscription> userSubscriptions;
	private TradingClassStatusConsumer consumer;

	public TradingClassStatusQueryServiceImpl(SessionManager sessionManager) {
		super();
		userSubscriptions = new HashMap<String, UserSubscription>();
		consumer = new TradingClassStatusConsumer(userSubscriptions);
		this.sessionManager = sessionManager;
	}

	public void create(String name) {
		super.create(name);
	}

	/////////////// IDL exported methods ////////////////////////////////////

	public int[] getClassesForProductGroup(String productGroupName)
			throws SystemException, CommunicationException,
			DataValidationException, NotFoundException, AuthorizationException {
		return ProductConfigurationQueryHelper.getInstance()
				.getClassesForProductGroup(productGroupName);
	}

	public String[] getProductGroups() throws SystemException,
			CommunicationException, DataValidationException, NotFoundException,
			AuthorizationException {
		if (Log.isDebugOn()) {
			Log
					.debug(this,
							"TradingClassStatusQueryServiceImpl - in getProductGroups() method");
		}

		return ProductConfigurationQueryHelper.getInstance().getProductGroups();
	}

	public void subscribeTradingClassStatusForClasses(String tradingSession,
			int[] listOfClasses,
			CMITradingClassStatusQueryConsumer tradingClassStatusCallback)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		if (Log.isDebugOn()) {
			Log.debug(this,
					"calling subscribeTradingClassStatusForClasses for classKeys "
							+ TradingClassStatusUtil
									.intArrayToString(listOfClasses));
		}

		boolean exception = false;
		try {
			if (tradingClassStatusCallback == null) {
				Log
						.alarm(
								this,
								"null CMITradingClassStatusQueryConsumer in subscribeTradingClassStatusForClasses for session:"
										+ sessionManager);
				return;
			}
			validateClassesAndRegisterSubscription(tradingSession,
					listOfClasses, new TCSCallBackHolder(tradingClassStatusCallback));
		} catch (Exception e) {
			exception = true;
			Log.exception(this, "ERROR PROCESSING CLASS BASED SUBSCRIPTIONS <"
					+ listOfClasses.length + ">", e);
		}

		if (!exception) {
			String smgr = sessionManager.toString();
			String listenerClass = tradingClassStatusCallback.getClass()
					.getName();
			StringBuilder suboid = new StringBuilder(smgr.length()
					+ listenerClass.length() + 45);
			// not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
			suboid.append("Sub:oid for ").append(smgr).append(" ").append(
					listenerClass).append("@").append(
					Integer.toHexString(tradingClassStatusCallback.hashCode()))
					.append(" classKeys:").append(
							TradingClassStatusUtil
									.intArrayToString(listOfClasses));
			Log.information(this, suboid.toString());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.cboe.interfaces.application.TradingClassStatusQueryService#subscribeTradingClassStatusForClasses(java.lang.String, int[], com.cboe.interfaces.application.inprocess.TradingClassStatusQueryConsumer)
	 */
	public void subscribeTradingClassStatusForClasses(String sessionName,
			int[] classKeys, TradingClassStatusQueryConsumer clientListener)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {
		if (Log.isDebugOn()) {
			Log.debug(this,
					"calling subscribeTradingClassStatusForClasses FIX Interface for classKeys "
							+ TradingClassStatusUtil
									.intArrayToString(classKeys));
		}

		boolean exception = false;
		try {
			if (clientListener == null) {
				Log
						.alarm(
								this,
								"null TradingClassStatusQueryConsumer in subscribeTradingClassStatusForClasses for session:"
										+ sessionManager);
				return;
			}
			validateClassesAndRegisterSubscription(sessionName,
					classKeys, new TCSCallBackHolder(clientListener));
		} catch (Exception e) {
			exception = true;
			Log.exception(this, "ERROR PROCESSING CLASS BASED SUBSCRIPTIONS <"
					+ classKeys.length + ">", e);
		}

		if (!exception) {
			String smgr = sessionManager.toString();
			String listenerClass = clientListener.getClass()
					.getName();
			StringBuilder suboid = new StringBuilder(smgr.length()
					+ listenerClass.length() + 45);
			// not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
			suboid.append("Sub:oid for ").append(smgr).append(" ").append(
					listenerClass).append("@").append(
					Integer.toHexString(clientListener.hashCode()))
					.append(" classKeys:").append(
							TradingClassStatusUtil
									.intArrayToString(classKeys));
			Log.information(this, suboid.toString());
		}
		
	}
	

	private void validateClassesAndRegisterSubscription(String tradingSession,
			int[] listOfClasses,
			TCSCallBackHolder tradingClassStatusCallback) {
		for (int i = 0; i < listOfClasses.length; i++) {
			// Get the associated group for the class
			String groupName = ProductConfigurationQueryHelper.getInstance()
					.getGroupNameForClass(listOfClasses[i], tradingSession);
			if (groupName != null) {
				// If the group already exist then update the list of classes
				UserSubscription tempSubscription = userSubscriptions
						.get(groupName);
				if (tempSubscription != null) {
					tempSubscription.addClasskey(listOfClasses[i]);
				} else {
					tempSubscription = new UserSubscription(
							tradingClassStatusCallback, listOfClasses[i],
							groupName);
					userSubscriptions.put(groupName, tempSubscription);
					addSubscriptionToContainer(groupName);
				}
			}
		}
	}

	private void addSubscriptionToContainer(String groupName) {
		TradingClassStatusSubscriptionsContainer.getInstance().addSubscritpion(
				groupName, consumer);
	}

	public void subscribeTradingClassStatusForProductGroup(
			String tradingSession, String[] groups,
			CMITradingClassStatusQueryConsumer tradingClassStatusCallback)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException {

		if (Log.isDebugOn()) {
			Log.debug(this,
					"calling subscribeTradingClassStatusForProductGroup for group "
							+ TradingClassStatusUtil.arrayToString(groups));
		}

		if (tradingClassStatusCallback == null) {
			Log
					.alarm(
							this,
							"null CMITradingClassStatusQueryConsumer in subscribeTradingClassStatusForProductGroup for session:"
									+ sessionManager);
			return;
		}
		validateGroupAndRegisterSubscription(tradingSession, groups,
				tradingClassStatusCallback);

		String smgr = sessionManager.toString();
		String listenerClass = tradingClassStatusCallback.getClass().getName();
		StringBuilder suboid = new StringBuilder(smgr.length()
				+ listenerClass.length() + 45);
		// not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
		suboid.append("Sub:oid for ").append(smgr).append(" ").append(
				listenerClass).append("@").append(
				Integer.toHexString(tradingClassStatusCallback.hashCode()))
				.append(" classKeys:").append(
						TradingClassStatusUtil.arrayToString(groups));
		Log.information(this, suboid.toString());
	}

	private void validateGroupAndRegisterSubscription(String tradingSession,
			String[] groups,
			CMITradingClassStatusQueryConsumer tradingClassStatusCallback)
			throws DataValidationException {
		boolean exception = false;
		String invalidGroup = "";
		for (int i = 0; i < groups.length; i++) {
			try {
				String group = groups[i];
				ProductConfigurationQueryHelper.getInstance().isValidGroup(
						group);
				if (userSubscriptions.get(group) == null) {
					userSubscriptions.put(group, new UserSubscription(
							new TCSCallBackHolder(tradingClassStatusCallback), group));
					addSubscriptionToContainer(groups[i]);
				} else {
					userSubscriptions.get(group).addGroupCallback(
							new TCSCallBackHolder(tradingClassStatusCallback));
				}
			} catch (Exception e) {
				exception = true;
				invalidGroup = invalidGroup + " " + groups[i];
			}
		}
		if (exception) {
			throw ExceptionBuilder.dataValidationException(
					formatStringInAngleBrackets(INVALID_GRP, invalidGroup), -1);
		}
	}

	private static String formatStringInAngleBrackets(
			String outsideAngleBrackets, String insideAngleBrackets) {
		return outsideAngleBrackets + OPEN_BRACKET + insideAngleBrackets
				+ CLOSE_BRACKET;
	}

	public class UserSubscription {
		private int[] classes;
		private String group;
		private String[] groups;
		private TCSCallBackHolder classCallback;
		private TCSCallBackHolder groupCallback;

		public UserSubscription(TCSCallBackHolder callback,
				String group) {
			this.groupCallback = callback;
			this.group = group;
			this.groups = new String[] { group };
			this.classes = null;
		}

		public void addClasskey(int classKey) {
			if (classes != null) {
				if (!isDublicate(classKey)) {
					int[] tempIntArray = new int[classes.length + 1];
					for (int i = 0; i < classes.length; i++) {
						tempIntArray[i] = classes[i];
					}
					tempIntArray[classes.length] = classKey;
					classes = tempIntArray;
				}
			} else {
				classes = new int[] { classKey };
			}
		}

		public UserSubscription(
				TCSCallBackHolder tradingClassStatusCallback,
				int classKey, String groupName) {
			classes = new int[] { classKey };
			this.group = groupName;
			this.classCallback = tradingClassStatusCallback;
		}

		public void addGroupCallback(TCSCallBackHolder callback) {
			this.groupCallback = callback;
			this.groups = new String[] { group };
		}

		public void notifyUser(short status) {
			try {
				if (classes != null) {
					if(classCallback.isCmi()) {
						classCallback.getCmiCallback().acceptTradingClassStatusUpdateforClasses(
								classes, status);
					}else {
						classCallback.getFixCallback().acceptTradingClassStatusUpdateforClasses(
								classes, status);	
					}
					
				}
				if (groups != null) {
					if(groupCallback.isCmi()) {
						groupCallback.getCmiCallback().acceptTradingClassStatusUpdateforProductGroups(groups, status);
					}else {
						groupCallback.getFixCallback().acceptTradingClassStatusUpdateforProductGroups(
								groups, status);
					}
							
				}
				if (Log.isDebugOn()) {
					Log.debug("All TCS-Subscriptions for User:"
							+ sessionManager.toString() + "\n"
							+ userSubscriptions);
				}
			} catch (Exception e) {
				Log.exception("Error while updating status for user "
						+ sessionManager.toString(), e);
			}
		}

		private boolean isDublicate(int classKey) {
			for (int i = 0; i < classes.length; i++) {
				if (classes[i] == classKey) {
					return true;
				}
				;
			}
			return false;
		}

		private String subscriptionType() {
			if (classCallback != null && groupCallback != null) {
				return "BOTH";
			} else if (classCallback != null) {
				return "CLASS";
			} else {
				return "GROUP";
			}
		}

		public String toString() {
			return sessionManager.toString()+" <" + this.group + ">|<"
					+ TradingClassStatusUtil.intArrayToString(classes)
					+ ">|<subcritionType:" + subscriptionType() + ">";
		}
	}
	
	private static class TCSCallBackHolder{
		private final CMITradingClassStatusQueryConsumer cmiCallback;
		private final TradingClassStatusQueryConsumer fixCallback;
		private boolean isCmi;
		
		public TCSCallBackHolder(CMITradingClassStatusQueryConsumer cmiCallback) {
			this.cmiCallback = cmiCallback;
			this.fixCallback = null;
			isCmi = true;
		}
		
		public TCSCallBackHolder(TradingClassStatusQueryConsumer fixCallback) {
			this.fixCallback = fixCallback;
			this.cmiCallback = null;
			isCmi = false;
		}
		
		public boolean isCmi() {
			return isCmi;
		}
		
		public CMITradingClassStatusQueryConsumer getCmiCallback() {
			return cmiCallback;
		}
		
		public TradingClassStatusQueryConsumer getFixCallback() {
			return fixCallback;
		}
		
	} 

}
