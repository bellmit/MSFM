package com.cboe.interfaces.domain;

import java.util.List;

/**
 * This interface is introduced to handle the new requirement that different kind of trades will
 * require different allocation strategy.
 */
public interface AllocationTradeContext {

    public static final String NO_PREFERRED_FIRM_SPECIFIED = "";
    /**
     * return the allocation trade type which will require which a distictive set of
     * allocation strategy
     */
    public int getAllocationTradeType();
    public void setAllocationTradeType(short allocationTradeType);

    /**
     * Return a boolean to indicate if quantity will be only allocated to NonQ tradables.
     * This constraint could be applied to different type of trades.
     */
    public boolean isNonQAllocation();
    public void setNonQAllocation(boolean nonQOnly);

    /**
     * Return a boolean to indicate if quantity will be only allocated to Q tradables.
     * This constraint could be applied to different type of trades.
     */
    public boolean isQAllocation();
    public void setQAllocation(boolean qOnly);
    /**
     * Return a boolean to indicate if quantity will be only allocated to non contingent tradables.
     * This constraint could be applied to different type of trades.
     */
    public boolean isNonContingencyAllocation();
    public void setNonContingencyAllocation(boolean nonContingencyOnly);

    /**
     * Return a firmId to indicate which firm the order should be considered preferred to, or NO_PREFERRED_FIRM_SPECIFIED to indicate no firm was preferred
     *
     */
    public List getPreferredFirm();
    public void setPreferredFirm(List preferredFirm);
    
    /**
     * Return a boolean to indicate if quantity will be only allocated to Treated Like Customer tradables.
     * This constraint could be applied to different type of trades.
     */
    public boolean isTreatedLikeCustomerAllocation();
    public void setTreatedLikeCustomerAllocation(boolean treatedLikeCustomerOnly);
    
    /**
     * Setting Tradable instance for DAIM Allocation
     */
     public Tradable getTradable();
     public void setTradable(Tradable tradable);
     
     
     /**
      * Used to set the Qty used for PMM Allocation for SPread to Leg Trade.
     * @return
     */
     public int getQtyForPmmAllocation();
     public void setQtyForPmmAllocation(int pmmAllocationQty);
     
     /**
      * Added for C2 Matching algorthim project. In some cases like HAL auction, the participant list is not sorted by time when it goes through the AllcoationPriceTimeStrategy.
      * So, the list has to be resorted to make it in time priority.
     * @return
     */
    public boolean isTimeSortingRequired();
    public void setIsTimeSortingRequired(boolean isTimeSortingRequired);
     
     
}
