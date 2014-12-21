/**
 * @author Jing Chen
 */
package com.cboe.interfaces.application.inprocess;

import com.cboe.idl.cmiAdmin.MessageStruct;

public interface UserSessionAdminConsumer
{
    public void acceptLogout(String s);
    public void acceptTextMessage(MessageStruct messageStruct);
}
