/**
 * 
 */
package com.cboe.interfaces.presentation.textmessages.alerts;


import java.awt.*;
import java.util.List;

/**
 * Defines the interface for an incoming message alert. An alert
 * consists of a list of rules that must be met, as well as
 * the audio sound and color to be applied to the message when the
 * alert conditions are met.
 * 
 * @author Steve Beckle
 * @since 11/17/2010
 */
@SuppressWarnings({"UnnecessaryInterfaceModifier", "BooleanMethodNameMustStartWithQuestion"})
public interface MessageCenterAlert
{
    public static final int MAX_RULES = 5;
    public static final String NO_SOUND_FILE = "None";
    //setters
    public void setAlertName(String name);
    public void setRules(List<MessageAlertRule> rules);
    public void setSoundFile(String soundFile);
    public void setBackgroundColor(Color backgroundColor);
    public void setTextColor(Color textColor);

    //getters
    public int getIndex();
    public String getAlertName();
    public List<MessageAlertRule> getRules();
    public String getSoundFile();
    public Color getBackgroundColor();
    public Color getTextColor();

    //helpers
    public boolean allFieldsMatch(MessageCenterAlert other);

}
