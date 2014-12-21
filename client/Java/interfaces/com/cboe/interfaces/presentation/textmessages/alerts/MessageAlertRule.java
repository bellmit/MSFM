/**
 * 
 */
package com.cboe.interfaces.presentation.textmessages.alerts;

/**
 * Defines a rule that must be met for an incoming message
 * to be highlighted and generate an alert in the message center
 * window. A rule consists of a message field and the string that
 * must be present in that field to be considered a match.
 * 
 * @author Steve Beckle
 * @since 11/17/2010
 */
@SuppressWarnings({"UnnecessaryInterfaceModifier"})
public interface MessageAlertRule
{
    public void setField(MessageAlertFields messageField);
    public void setMatchingText(String text);
    public MessageAlertFields getField();
    public String getMatchingText();
}
