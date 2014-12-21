package com.cboe.interfaces.domain.session;

import java.util.Date;
import com.cboe.idl.cmiUtil.KeyValueStruct;

/**
 * Per-TradingSessionClient registration for a trading session client.
 * 
 * @see TradingSessionRegistrationMapHome
 * @see TradingSessionEventHistoryDetail
 * @author Steven Sinclair
 */
public interface TradingSessionRegistration
{
    String getSessionName();
    String getClientName();
    String getGroupName();

    /**
     *  The group type of the client.
     */
    int getPCSGroupType();

    /**
     *  The current history detail associated with this registered client
     */
    TradingSessionEventHistoryDetail getHistoryDetail();

    Date getCreationTime();
    Date getLastModifiedTime();
    long getCreationTimeMillis();
    long getLastModifiedTimeMillis();
    KeyValueStruct[] getServerDetails();

    boolean isActive();

    void setSessionName(String value);
    void setGroupName(String value);
    void setClientName(String value);
    void setPCSGroupType(int value);
    void setHistoryDetail(TradingSessionEventHistoryDetail value);
    void setCreationTime(Date value);
    void setLastModifiedTime(Date value);
    void setIsActive(boolean value);
    void setServerDetails(KeyValueStruct[] value);
}
