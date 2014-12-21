package com.cboe.interfaces.domain;




public interface ManualQuote extends Tradable
{
    public void resetManualQuote(Price p_price, int p_quantity, String p_location, 
                                    String p_ipAddress, String p_parId);
	public String getLocation();
	public String getIPAddress();
	public String getPARId();
	public boolean isOverrideIndicator();
	public String getSource();	
	
	public void setCancelReason(char reason);
	
	public char getCancelReason();

	/**
	 * Manual quote fields for display purpose.
	 */
	public String toString();
}
