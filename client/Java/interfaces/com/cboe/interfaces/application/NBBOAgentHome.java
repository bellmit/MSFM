/*
 * Created by IntelliJ IDEA.
 * User: HUANGE
 * Date: Sep 11, 2002
 * Time: 3:16:38 PM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.interfaces.application;

public interface NBBOAgentHome {
    public final static String HOME_NAME = "NBBOAgentHome";
    public NBBOAgent create(SessionManager sessionManager);
}
