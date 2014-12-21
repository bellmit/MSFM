/*
 * Created by IntelliJ IDEA.
 * User: HUANGE
 * Date: Sep 12, 2002
 * Time: 9:58:37 AM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.interfaces.application;

public interface IntermarketSessionManagerHome {
    public final static String HOME_NAME = "IntermarketSessionManagerHome";
    public IntermarketUserSessionManager create(SessionManager sessionManager);
}
