package com.cboe.delegates.application;
import com.cboe.idl.cmiTradeMaintenanceService.POA_UserAccessTMS_tie;
import com.cboe.interfaces.application.UserAccessTMS;

public class UserAccessTMSDelegate extends POA_UserAccessTMS_tie {
	public UserAccessTMSDelegate(UserAccessTMS delegate) {
		super(delegate);
	}
}
