package com.cboe.domain.util;

import com.cboe.interfaces.domain.TradingFirmGroupWrapper;

import java.util.List;

/**
 * Author: mahoney
 * Date: Apr 21, 2008
 * A simple container which holds a trading firm user and an array of users
 * associated to that user.  Created for Drop Copy Enhancements.
 */
public class TradingFirmGroupContainer implements TradingFirmGroupWrapper
{
    private String tradingFirmId;
    private List<String> groupUsers;
    private String displayString;
    private int hashCode;

    public TradingFirmGroupContainer(String id, List<String> users)
    {
        this.tradingFirmId = id;
        this.groupUsers = users;
        hashCode = tradingFirmId.hashCode() + groupUsers.hashCode();
    }

    public String getTradingFirmId()
    {
        return tradingFirmId;
    }

    public List<String> getUsers()
    {
        return groupUsers;
    }

    public int hashCode()
    {
        return hashCode;
    }

    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof TradingFirmGroupContainer))
        {
            String userId = ((TradingFirmGroupContainer)obj).getTradingFirmId();
            List users = ((TradingFirmGroupContainer)obj).getUsers();
            return (this.tradingFirmId.equals(userId)
                    &&  this.groupUsers.equals(users));
        }
        return false;
    }

    public String toString()
    {
        if (displayString == null)
        {
            StringBuilder buff = new StringBuilder();
            for(int i = 0; i < groupUsers.size(); i++)
            {
                String user = groupUsers.get(i);
                if(i == groupUsers.size() - 1)
                    buff.append(groupUsers.get(i));
                else
                    buff.append(groupUsers.get(i)).append(',');
            }
            displayString = this.tradingFirmId + ':' + buff.toString();
        }
        return displayString;
    }
}
