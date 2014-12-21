package com.cboe.interfaces.domain.userLoadManager;
import com.cboe.interfaces.domain.RateMonitor;
import com.cboe.interfaces.events.UserSessionConsumer;

/**
 * This extends the CORBA Interface into a CBOE Common standard
 * @author David De La Vega
 * @author alin
 */
public interface UserLoadManager extends UserSessionConsumer
{
	public void addUser(Object key,RateMonitor rateMonitor);
	public void removeUser(String userId);
	public int getTotalLimit(short type);
	public void updateUserLimit(Object rateMonitorKey, int oldWindowSize, long oldWindowPeriod);
	public void registerServiceReference(String svcRouteKey, org.omg.CORBA.Object serviceReference);
}
