package com.cboe.application.tradingClassStatus;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.CommandCallbackService;
import com.cboe.interfaces.application.TCSProcessWatcherManager;
import com.cboe.interfaces.application.TCSProcessWatcherManagerHome;

/**
 * @author Arun Ramachandran
 */
public class TCSProcessWatcherManagerHomeImpl extends ClientBOHome implements
		TCSProcessWatcherManagerHome {
	private TCSProcessWatcherManager pwManager;

	public TCSProcessWatcherManagerHomeImpl() {
		super();
		setSmaType("GlobalTCSProcessWatcherManagerHome.TCSProcessWatcherManagerHomeImpl");
	}

	public String getHomeName() {
		return HOME_NAME;
	}

	public void clientInitialize() throws Exception {
		Log.debug(this, "SMA Type = " + this.getSmaType());
		find().registerWithProcessWatcher();
	}

	public TCSProcessWatcherManager create() throws Exception {
		if (pwManager == null) {
			TCSProcessWatcherManagerImpl bo = new TCSProcessWatcherManagerImpl();
			bo.create("TCSProcessWatcherManager");
			addToContainer(bo);
			pwManager = bo;
			registerCallbacks();
		}
		return pwManager;
	}

	public TCSProcessWatcherManager find() throws Exception {
		return create();
	}

	private void registerCallbacks() {
		CommandCallbackService commandCallbackService = FoundationFramework
				.getInstance().getCommandCallbackService();
		registerCallbackCommand(commandCallbackService,
				new String[] { String.class.getName() },
				new String[] { "true - Turn OFF TCS Notification\n\tfalse - Turn ON TCS Notifications\n\tshowList - List of BC down process(es) " }, "turnOffTCSNotifications",
				"turnOffTCSNotifications",
				"[Display / Control] TCS Notifications");
	}

	/**
	 * 
	 */
	private void registerCallbackCommand(
			CommandCallbackService commandCallbackService, String[] arguments,
			String[] argumentDescriptions, String externalName,
			String methodName, String commandDescription) {
		try {
			commandCallbackService.registerForCommandCallback(this,
			// Callback Object
					externalName,
					// External name
					methodName,
					// Method name
					commandDescription,
					// Method description
					arguments, argumentDescriptions);
		} catch (Exception e) {
			Log.exception(this,
					"Error when registering for admin callback - MethodName:<"
							+ methodName + "> Description:<"
							+ commandDescription + ">", e);
		}
	}
	
	public String turnOffTCSNotifications(String[] turnOff){
		return pwManager.turnOffTCSNotifications(turnOff);
	}

}
