package com.cboe.interfaces.application;

import com.cboe.idl.cmiOrder.OrderStruct;

public interface TCSProcessWatcherManager {
	public void registerWithProcessWatcher();
	public boolean addToDownList(String groupName);
	public boolean removeFromDownList(String groupName);
	public boolean isServerDownListEmpty();
	public boolean isProcessDown(OrderStruct order);
	public boolean isProcessDown(int classkey, String sessionName, boolean isOrder);
	public boolean isProcessDown(String groupName, String sessionName, boolean isOrder);
	public String turnOffTCSNotifications(String[] turnOff);
}
