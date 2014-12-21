package com.cboe.domain.util;

public class LogoutMessage extends Object {
    private String message;
    private int sessionKey;

    /**
      * Sets the internal fields to the passed values
      */
    public LogoutMessage(int key, String message) {
		this.message = message;
		this.sessionKey = key;
    }
    public String getMessage()
    {
        return message;
    }

    public int getSessionKey()
    {
        return sessionKey;
    }

    public int hashCode()
    {
        return sessionKey;
    }

    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof LogoutMessage))
        {
            String message = ((LogoutMessage)obj).getMessage();
            int key = ((LogoutMessage)obj).getSessionKey();
            return (this.message.equals(message)
                    &&  (key == sessionKey)
                    );
        }
        return false;
    }
}
