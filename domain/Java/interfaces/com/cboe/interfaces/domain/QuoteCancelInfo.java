package com.cboe.interfaces.domain;

/**
 * @author pyatetsk
 *
 * This is Quote Cancel information object interface 
 * 
 */
public interface QuoteCancelInfo{
	
	
	/**
	 * @return Returns the cancelReason.
	 */
	public short getCancelReason() ;
	/**
	 * @param cancelReason The cancelReason to set.
	 */
	public void setCancelReason(short cancelReason);
	/**
	 * @return Returns the cancelScope.
	 */
	public short getCancelScope();
	/**
	 * @param cancelScope The cancelScope to set.
	 */
	public void setCancelScope(short cancelScope);
    
	/**
     * @return Returns the cancelWithBatchReporting.
     */
    public boolean isCancelWithBatchReporting();
    
    /**
     * @param cancelWithBatchReporting The cancelWithBatchReporting to set.
     */
    public void setCancelWithBatchReporting(boolean cancelWithBatchReporting);
	
	/**
	 * @param quote The quote to update with cancel info.
	 */
	public void updateQuote(Quote quote);
}


