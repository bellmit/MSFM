package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiIntermarketCallback.CMINBBOAgentSessionAdminPOA;
import com.cboe.idl.cmiIntermarketMessages.AdminStruct;
import com.cboe.idl.cmiIntermarketMessages.OrderReminderStruct;
import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;

public class NBBOAgentSessionAdmin extends CMINBBOAgentSessionAdminPOA
{
    public void acceptForcedOut(String reason, int classKey, String session)
    {
        Log.message("NBBOAgentSessionAdmin.acceptForcedOut"
                + " reason:" + reason + " classKey:" + classKey
                + " session:" + session);
    }

    public void acceptReminder(
            OrderReminderStruct reminder, int classKey, String session)
    {
        Log.message("NBBOAgentSessionAdmin.acceptReminder"
                + " reminder:{" + Struct.toString(reminder) + "}"
                + " classKey:" + classKey + " session:" + session);
    }

    public void acceptSatisfactionAlert(
            SatisfactionAlertStruct alert, int classKey, String session)
    {
        Log.message("NBBOAgentSessionAdmin.acceptSatisfactionAlert"
                + " alert:{" + Struct.toString(alert) + "}"
                + " classKey:" + classKey + " session:" + session);
    }

    public void acceptIntermarketAdminMessage(
            String session, String originatingExchange,
            ProductKeysStruct productKeys, AdminStruct adminMessage)
    {
        Log.message("NBBOAgentSessionAdmin.acceptIntermarketAdminMessage"
                + " session:" + session
                + " originatingExchange:" + originatingExchange
                + " productKeys:{" + Struct.toString(productKeys) + "}"
                + " adminMessage:{" + Struct.toString(adminMessage) + "}");
    }

    public void acceptBroadcastIntermarketAdminMessage(String session,
            String originatingExchange, AdminStruct adminMessage)
    {
        Log.message(
                "NBBOAgentSessionAdmin.acceptBroadcastIntermarketAdminMessage"
                + " session:" + session
                + " originatingExchange:" + originatingExchange
                + " adminMessage:{" + adminMessage + "}");
    }
}
