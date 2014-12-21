/**
 * Created by IntelliJ IDEA.
 * User: chenj
 * Date: Feb 20, 2003
 * Time: 4:34:47 PM
 * To change this template use Options | File Templates.
 */
package com.cboe.interfaces.cfix;

import com.cboe.idl.cmiAdmin.MessageStruct;

public interface CfixUserSessionAdminConsumer {
    void acceptLogout(String s);

    void acceptTextMessage(MessageStruct messageStruct);
}
