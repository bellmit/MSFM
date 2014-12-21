package com.cboe.interfaces.domain;

public interface IndexHybridAutoExQtyHolder
{
	public boolean bobAutoExProcessingMightBeNeeded();
	
	/**
	 * Max quantity of incoming order which could be traded without violating auto ex rule.
	 * @return
	 */
	public int getMaxAutoExQty();
    
    /*
     * Max customer quantity available
     */
    public int getAutoExSizeForClass();
    
    /*
     * Max customer quantity available
     */
    public int getCustomerQuantity();
    
    /*
     * Max Non customer quantity available
     */
    public int getNonCustomerQuantity();
    public void setMaxAutoExQty(int desiredAutoExQty);
    
    /*
     * Max Customer with Contingency quantity available
     */
    public int getCustomerWithContingencyQty();
    
    /*
     * Max Available quantity for trade [customerQuantity + nonCustomerQty + customerWithContingencyQty]
     */
    public int getMaxAvailableQtyToTrade();
    
    /*
     * Is available Non Customer Qty more than AUTO_EX
     */
    public boolean availableNonCustQtyMoreThanAutoEx();
    
    public void setAvailableNonCustQtyMoreThanAutoEx(boolean availableNonCustQtyMoreThanAutoEx); 
    
    /*
     * Added Customer with Contingency Qty to desired qty.
     */
    public int getCustomerWithContingencyAtBetterPriceQty();
}
