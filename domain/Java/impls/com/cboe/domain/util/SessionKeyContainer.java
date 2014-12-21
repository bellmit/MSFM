package com.cboe.domain.util;

import com.cboe.interfaces.domain.SessionKeyWrapper;

public class SessionKeyContainer extends Object implements SessionKeyWrapper{
    private String sessionName;
    private int key;
    private String displayString;

    /**
      * Sets the internal fields to the passed values
      */
    public SessionKeyContainer(String sessionName, int key) {
		this.sessionName = sessionName;
		this.key = key;
        this.displayString = null;
    }
    public String getSessionName()
    {
        return sessionName;
    }

    public int getKey()
    {
        return key;
    }

    public int hashCode()
    {
        return key;
    }

    public boolean equals(Object obj)
    {
        boolean result = false;
        if ((obj != null) && (obj instanceof SessionKeyContainer))
        {
            SessionKeyContainer otherContainer = (SessionKeyContainer) obj;
            String sessionName = otherContainer.getSessionName();
            int otherKey = otherContainer.getKey();
            if (this.key == otherKey && this.sessionName.equals(sessionName))
            {
                result = true;
            }
        }
        return result;
    }

    public String toString()
    {
        if (displayString == null)
        {
            displayString = this.sessionName + ':' + this.key;
        }
        return displayString;
    }
}
