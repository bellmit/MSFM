package com.cboe.interfaces.application;

import com.cboe.idl.cmiIntermarketMessages.OrderReminderStruct;
import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;
import com.cboe.idl.cmiIntermarketMessages.AdminStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.interfaces.domain.session.SessionBasedCollector;

/**
 *
 * @author Emily Huang
 *
 */
public interface NBBOAgentAdminCollector extends SessionBasedCollector {
    public void acceptForcedOut(String reason, int classKey, String session );

    public void acceptReminder(OrderReminderStruct reminder, int classKey, String session);

    public void acceptSatisfactionAlert(SatisfactionAlertStruct alert);

    public void acceptIntermarketAdminMessage(String sessionName, String srcExchange, ProductKeysStruct productKeys, AdminStruct adminMessage);

    public void acceptBroadcastIntermarketAdminMessage(String sessionName, String srcExchange, AdminStruct adminMessage);
}
