/**
 * 
 */
package com.cboe.interfaces.presentation.textmessages.alerts;

/**
 * Defines the fields within the message that can be used to
 * define rules for a message alert.
 * 
 * @author Steve Beckle
 * @since 11/17/2010
 */
public enum MessageAlertFields
{
    NONE(" "),
    FROM("From"),
    SUBJECT("Subject"),
    MESSAGE_BODY("Message Body");

    private String text;

    private MessageAlertFields(String text)
    {
        this.text = text;
    }

    public String toString()
    {
        return text;
    }

}
