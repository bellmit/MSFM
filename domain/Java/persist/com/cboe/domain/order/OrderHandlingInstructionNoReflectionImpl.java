package com.cboe.domain.order;

import com.cboe.domain.util.PriceFactory;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.order.OrderHandlingInstructionStruct;
import com.cboe.infrastructureServices.foundationFramework.transactionManagement.BOSessionManager;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.Transaction;
import com.cboe.infrastructureServices.foundationFramework.utilities.TransactionListener;
import com.cboe.interfaces.domain.HandlingInstruction;
import com.cboe.interfaces.domain.Price;
import com.cboe.server.domain.BaseDomainObjectImpl;
import com.objectwave.transactionalSupport.TransactionLog;

/**
 * This class is designed to hold on any order handling instructions
 */

public class OrderHandlingInstructionNoReflectionImpl extends BaseDomainObjectImpl implements HandlingInstruction {

    private Price oppositeSideBOTR;
    private Price executionPrice;
    private Price originalExecutionPrice;
    private int maximumExecutionVolume;
    private short maximumExecutionVolumeReason;
    private short tradingRestriction;
    private short remainderHandlingMode;
    private boolean ignoreContingency = false;
    private int orderQuantityAtReceiveTime;
    private int tradedVolume = 0;
    private boolean expressOrder = false;
    private boolean inboundISOEnabled = false;
    private double bookableOrderMarketLimit = 0.0;
    private transient boolean attempted;
    public final short DEFAULT_OVERRIDE_REASON_FOR_INDEX_HYBRID= -1;
    public final short DEFAULT_TRADABLE_QTY_FOR_INDEX_HYBRID= -1;
    
    private int maxTradableQtyForIndexHybrid;
    private short overRideReasonForIndexHybrid;

    private short returnedReason = -1;

    private volatile OrderHandlingInstructionTXChangeLog orderHITXChangeLog = null;
    private volatile OrderHandlingInstructionTXChangeLog firstOrderHITXChangeLog = null;



    public OrderHandlingInstructionNoReflectionImpl() 
    {
        setDefaultMaxTradableQtyForIndexHybrid();
        setDefaultOverRideReasonForIndexHybrid();
        
    }

    public Price getOppositeSideBOTR(){
        return oppositeSideBOTR;
    }

    public Price getExecutionPrice(){
        return executionPrice;
    }

    public Price getOriginalExecutionPrice(){
        return originalExecutionPrice;
    }

    public int getMaximumExecutionVolume(){
        return maximumExecutionVolume;
    }

    public int getTradedVolume(){
        return tradedVolume;
    }

    public short getMaximumExecutionVolumeReason(){
        return maximumExecutionVolumeReason;
    }

    public short getTradingRestriction(){
        return tradingRestriction;
    }

    public short getRemainderHandlingMode(){
        return remainderHandlingMode;
    }

    public boolean ignoreContingency(){
        return ignoreContingency;
    }

    public int getOrderQuantityReceived(){
        return orderQuantityAtReceiveTime;
    }
    public short getOverRideReasonForIndexHybrid(){
        return overRideReasonForIndexHybrid;
    }

    public int getMaxTradableQtyForIndexHybrid(){
        return maxTradableQtyForIndexHybrid;
    }
    
    public void setOverRideReasonForIndexHybrid(short aOverRideReasonForIndexHybrid)
    {
        if(isBypassReflection())
        {
            overRideReasonForIndexHybrid = aOverRideReasonForIndexHybrid;
        }
        else
        {
            getCurrentTXLog().setOverRideReasonForIndexHybrid(getOverRideReasonForIndexHybrid());
            overRideReasonForIndexHybrid = aOverRideReasonForIndexHybrid;
        }
    }

    public void setDefaultOverRideReasonForIndexHybrid()
    {
        setOverRideReasonForIndexHybrid(DEFAULT_OVERRIDE_REASON_FOR_INDEX_HYBRID);
    }
    
    public boolean isOrderReturnReasonSetForIndexHybrid()
    {
        return  getOverRideReasonForIndexHybrid() != DEFAULT_OVERRIDE_REASON_FOR_INDEX_HYBRID ; 
    }    

    public boolean isExpressOrder()
    {
        return expressOrder;
    }
    
    public double getBookableOrderMarketLimit()
    {
        return bookableOrderMarketLimit;
    }

    public void setMaxTradableQtyForIndexHybrid(int aMaxTradableQtyForIndexHybrid)
    {
        if(isBypassReflection())
        {
            maxTradableQtyForIndexHybrid = aMaxTradableQtyForIndexHybrid;
        }
        else
        {
            getCurrentTXLog().setMaxTradableQtyForIndexHybrid(getMaxTradableQtyForIndexHybrid());
            maxTradableQtyForIndexHybrid = aMaxTradableQtyForIndexHybrid;
        }
    }

    public void setDefaultMaxTradableQtyForIndexHybrid(){
        setMaxTradableQtyForIndexHybrid(DEFAULT_TRADABLE_QTY_FOR_INDEX_HYBRID);
    }
    
    public boolean isDefaultMaxTradableQtyForIndexHybrid()
    {
        return  getMaxTradableQtyForIndexHybrid() == DEFAULT_TRADABLE_QTY_FOR_INDEX_HYBRID; 
    }

    public void setOppositeSideBOTR(Price aBOTR)
    {
        if(isBypassReflection())
        {
            oppositeSideBOTR = aBOTR;
        }
        else
        {
            getCurrentTXLog().setOppositeSideBOTR(getOppositeSideBOTR());
            oppositeSideBOTR = aBOTR;
        }
    }

    public void setExecutionPrice(Price aPrice)
    {
        if(isBypassReflection())
        {
            executionPrice = aPrice;
        }
        else
        {
            getCurrentTXLog().setExecutionPrice(getExecutionPrice());
            executionPrice = aPrice;
        }
    }

    public void setOriginalExecutionPrice(Price aPrice)
    {
        if(isBypassReflection())
        {
            originalExecutionPrice = aPrice;
        }
        else
        {
            getCurrentTXLog().setOriginalExecutionPrice(getOriginalExecutionPrice());
            originalExecutionPrice = aPrice;
        }
    }

    public void setMaximumExecutionVolume(int aVolume)
    {
        if(isBypassReflection())
        {
            maximumExecutionVolume = aVolume;
        }
        else
        {
            getCurrentTXLog().setMaximumExecutionVolume(getMaximumExecutionVolume());
            maximumExecutionVolume = aVolume;
        }
    }

    public void setTradedVolume(int aVolume)
    {
        if(isBypassReflection())
        {
            tradedVolume = aVolume;
        }
        else
        {
            getCurrentTXLog().setTradedVolume(getTradedVolume());
            tradedVolume = aVolume;
        }
    }

    public void setMaximumExecutionVolumeReason(short aReason)
    {
        if(isBypassReflection())
        {
            maximumExecutionVolumeReason = aReason;
        }
        else
        {
            getCurrentTXLog().setMaximumExecutionVolumeReason(getMaximumExecutionVolumeReason());
            maximumExecutionVolumeReason = aReason;
        }
    }

    public void setTradingRestriction(short aRestriction)
    {
        if(isBypassReflection())
        {
            tradingRestriction = aRestriction;
        }
        else
        {
            getCurrentTXLog().setTradingRestriction(getTradingRestriction());
            tradingRestriction = aRestriction;
        }
    }

    public void setRemainderHandlingMode(short aHandlingMode)
    {
        if(isBypassReflection())
        {
            remainderHandlingMode = aHandlingMode;
        }
        else
        {
            getCurrentTXLog().setRemainderHandlingMode(getRemainderHandlingMode());
            remainderHandlingMode = aHandlingMode;
        }
    }

    public void setIgnoreContingency(boolean aBoolean)
    {
        if(isBypassReflection())
        {
            ignoreContingency = aBoolean;
        }
        else
        {
            getCurrentTXLog().setIgnoreContingency(ignoreContingency);
            ignoreContingency = aBoolean;
        }
    }

    public void setOrderQuantityReceived(int aQuantity)
    {
        if(isBypassReflection())
        {
            orderQuantityAtReceiveTime = aQuantity;
        }
        else
        {
            getCurrentTXLog().setOrderQuantityAtReceiveTime(getOrderQuantityReceived());
            orderQuantityAtReceiveTime = aQuantity;
        }
    }

    public void setExpressOrder(boolean aBoolean)
    {
        if(isBypassReflection())
        {
            expressOrder = aBoolean;
        }
        else
        {
            getCurrentTXLog().setExpressOrder(isExpressOrder());
            expressOrder = aBoolean;
        }
    }
    
    public void setBookableOrderMarketLimit(double aLimit)
    {
        if(isBypassReflection())
        {
            bookableOrderMarketLimit = aLimit;
        }
        else
        {
            getCurrentTXLog().setBookableOrderMarketLimit(getBookableOrderMarketLimit());
            bookableOrderMarketLimit = aLimit;
        }
    }
    
    /**
     * Update from anOrderHandlingInstructionStruct and a BestOfRest price
     */
    public void updateFrom(OrderHandlingInstructionStruct instruct, PriceStruct botr, int orderQuantityReceived){
        setOppositeSideBOTR(PriceFactory.create(botr));
        setExecutionPrice(PriceFactory.create(instruct.executionPrice));
        setMaximumExecutionVolume(instruct.maximumExecutionVolume);
        setMaximumExecutionVolumeReason(instruct.maximumExecutionVolumeReason);
        setTradingRestriction(instruct.tradingRestriction);
        setRemainderHandlingMode(instruct.remainderHandlingMode);
        setOrderQuantityReceived(orderQuantityReceived);
        setIgnoreContingency(instruct.ignoreContingency);
        setTradedVolume(0);
    }
    
    public boolean updateFrom(String instruction, int orderQuantityReceived)
    {
        boolean update = updateFrom(instruction);
        setOrderQuantityReceived(orderQuantityReceived);
        return update;
    } 

    public boolean updateFrom(String instruction)
    {
        String pattern = ":";
        int count = 0;
        int pos = instruction.indexOf(pattern);
        
        while ( pos != -1 ) {
           count++;
           pos = instruction.indexOf(pattern,pos+1);
        }

        if (count < 8)
        {
            Log.information("OrderHandlingInstructionNoReflectionImpl >>> less than 8 fields are in instruction: " + instruction);
            Log.information("OrderHandlingInstructionNoReflectionImpl >>> order handling instruction is not updated.");
            return false;
        }

        int stringLength = instruction.length();
        int i=0;
        int j=0;
        
        while (j != stringLength)
        {
            j = instruction.indexOf(pattern,0);
            setOppositeSideBOTR(PriceFactory.create(instruction.substring(0,j)));
            i = ++j;
            j = instruction.indexOf(pattern,i);
            
            setExecutionPrice(PriceFactory.create(instruction.substring(i,j)));
            i = ++j;
            j = instruction.indexOf(pattern,i);
     
            setMaximumExecutionVolume(Integer.parseInt(instruction.substring(i,j)));
            i = ++j;
            j = instruction.indexOf(pattern,i);
         
            setMaximumExecutionVolumeReason(Short.parseShort(instruction.substring(i,j)));
            i = ++j;
            j = instruction.indexOf(pattern,i);
       
            setTradingRestriction(Short.parseShort(instruction.substring(i,j)));
            i = ++j;
            j = instruction.indexOf(pattern,i);
           
            setRemainderHandlingMode(Short.parseShort(instruction.substring(i,j)));
            i = ++j;
            j = instruction.indexOf(pattern,i);
          
            setIgnoreContingency(Boolean.valueOf(instruction.substring(i,j)).booleanValue());
            i = ++j;
            j = instruction.indexOf(pattern,i);
            
            
            setTradedVolume(Integer.parseInt(instruction.substring(i,j)));
            i = ++j;
            j = instruction.indexOf(pattern,i);
          
            if (j == -1)
            { 
                j = stringLength;
                setMaxTradableQtyForIndexHybrid(Integer.parseInt(instruction.substring(i,j)));
                break;
            }
            else
            {
                setMaxTradableQtyForIndexHybrid(Integer.parseInt(instruction.substring(i,j)));
                i = ++j;
                j = instruction.indexOf(pattern,i);
            }

            if (j == -1)
            { 
                j = stringLength;
                setExpressOrder((instruction.substring(i,j)).equals("T")?true:false);
                break;
            }
            else
            {
                setExpressOrder((instruction.substring(i,j)).equals("T")?true:false);
                i = ++j;
                j = instruction.indexOf(pattern,i);
            }

            if (j == -1)
            { 
                j = stringLength;
                setInboundISOEnabled((instruction.substring(i,j)).equals("T")?true:false);
                break;
            }
            else
            {
                setInboundISOEnabled((instruction.substring(i,j)).equals("T")?true:false);
                i = ++j;
                j = instruction.indexOf(pattern,i);
            }

           // if additional fields are added, this should have the above conditions applied and the 
           // new string field should be treated as this one is - the last field
                j = stringLength;
                setBookableOrderMarketLimit(Double.parseDouble(instruction.substring(i,j))); 
                break;
        }
               
        return true;
    }

    public static String printStruct(OrderHandlingInstructionStruct instruct)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("Price:");
        buf.append(PriceFactory.create(instruct.executionPrice).toString());

        buf.append(" MaxVol:");
        buf.append(instruct.maximumExecutionVolume);

        buf.append(" MaxReason:");
        buf.append(instruct.maximumExecutionVolumeReason);

        buf.append(" Restrict:");
        buf.append(instruct.tradingRestriction);

        buf.append(" RemainderHdl:");
        buf.append(instruct.remainderHandlingMode);

        buf.append(" IgnoreCont:");
        buf.append(instruct.ignoreContingency);     
        
        return buf.toString();
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("BOTR:");
        buf.append(getOppositeSideBOTR().toString());

        buf.append(" Price:");
        buf.append(getExecutionPrice().toString());

        buf.append(" MaxVol:");
        buf.append(getMaximumExecutionVolume());

        buf.append(" MaxReason:");
        buf.append(getMaximumExecutionVolumeReason());

        buf.append(" Restrict:");
        buf.append(getTradingRestriction());

        buf.append(" RemainderHdl:");
        buf.append(getRemainderHandlingMode());

        buf.append(" IgnoreCont:");
        buf.append(ignoreContingency());

        buf.append(" TradedVolume:");
        buf.append(getTradedVolume());
        
        buf.append(" MaxTradableQtyForIndexHybrid:");
        buf.append(getMaxTradableQtyForIndexHybrid());
        
        buf.append(" ExpressOrder:");
        buf.append(isExpressOrder());
        
        buf.append(" ISOEnabled:");
        buf.append(this.isInboundISOEnabled());
        
        buf.append(" BookableOrderMarketLimit:");
        buf.append(getBookableOrderMarketLimit());
        
        return buf.toString();
    }

    public String toPersistenceString()
    {
        StringBuffer buf = new StringBuffer();
        Price price = getOppositeSideBOTR();
        if (price != null)
        {
            buf.append(price.toString());
        }
        else
        {
            Log.alarm(this, "OrderHandlingInstructionNoReflectionImpl.toPersistenceString(): OppositeSideBOTR is null, setting to No Price");
            buf.append(PriceFactory.getNoPrice().toString());
        }   

        buf.append(":");
        price = getExecutionPrice();
        if (price != null)
        {
            buf.append(price.toString());
        }
        else
        {
            Log.alarm(this, "OrderHandlingInstructionNoReflectionImpl.toPersistenceString(): ExecutionPrice is null, setting to No Price");
            buf.append(PriceFactory.getNoPrice().toString());
        } 

        buf.append(":");
        buf.append(getMaximumExecutionVolume());

        buf.append(":");
        buf.append(getMaximumExecutionVolumeReason());

        buf.append(":");
        buf.append(getTradingRestriction());

        buf.append(":");
        buf.append(getRemainderHandlingMode());

        buf.append(":");
        buf.append(ignoreContingency());

        buf.append(":");
        buf.append(getTradedVolume());
        
        buf.append(":");
        buf.append(getMaxTradableQtyForIndexHybrid());
        
        buf.append(":");
        buf.append(isExpressOrder()?'T':'F');
        
        buf.append(":");
        buf.append(isInboundISOEnabled()?'T':'F');
        
        buf.append(":");
        buf.append(getBookableOrderMarketLimit());

        return buf.toString();
    }

    public int getAllowedQuantity(){
        return getMaximumExecutionVolume() - getTradedVolume();
    }

    public void setReturnedCode(short value)
    {
        returnedReason = value;
    }

    public short getReturnedCode()
    {
        return returnedReason;
    }
    
    public void setInboundISOEnabled(boolean value)
    {
        this.inboundISOEnabled = value;
    }
    
    public boolean isInboundISOEnabled()
    {
        return inboundISOEnabled;
    }
    public void setAttempted(boolean attempted){
        this.attempted=attempted;
    }
    public boolean getAttempted(){
        return attempted;
    }

    
    /**********************   Reflection Free OrderHI Related changes   *****************/

    private OrderHandlingInstructionTXChangeLog getCurrentTXLog()
    {
        TransactionLog owRequestedTransactionLog = Transaction.getCallbackSession().getCurrentTransaction();
        TransactionLog owRequestedTransactionParentLog = Transaction.getCallbackSession().getCurrentTransaction().getParentTransaction();
        markOrderHIFieldChanged();
        OrderHandlingInstructionTXChangeLog tmpOrderHITXChangeLog = (OrderHandlingInstructionTXChangeLog) getCurrentTXLog(owRequestedTransactionLog, firstOrderHITXChangeLog, orderHITXChangeLog);

        if(tmpOrderHITXChangeLog == null)
        {
            linkMissingOWTransactionLog(owRequestedTransactionLog, owRequestedTransactionParentLog);

            OrderHandlingInstructionTXChangeLog currentOrderHITXLog = new OrderHandlingInstructionTXChangeLog();
            currentOrderHITXLog.setOWCurrentTxLog(owRequestedTransactionLog);
            if(owRequestedTransactionLog != null)
            {
                currentOrderHITXLog.setOwParentTxLog(owRequestedTransactionLog.getParentTransaction());
            }
            currentOrderHITXLog.setParentTXChangeLog(orderHITXChangeLog);
            if(orderHITXChangeLog != null)
            {
                orderHITXChangeLog.setChildTXChangeLog(currentOrderHITXLog);
            }
            orderHITXChangeLog = currentOrderHITXLog;
        }

        if(firstOrderHITXChangeLog == null)
        {
            firstOrderHITXChangeLog = orderHITXChangeLog;
        }

        return orderHITXChangeLog;
    }

    private void linkMissingOWTransactionLog(TransactionLog owRequestedTransactionLog,
            TransactionLog owRequestedTransactionParentLog)
    {
        if(orderHITXChangeLog == null && owRequestedTransactionLog.getParentTransaction() != null)
        {
            TransactionLog tempOWTXLog = owRequestedTransactionLog.getParentTransaction();
            while(tempOWTXLog != null)
            {
                if(tempOWTXLog.getParentTransaction() == null)
                {
                    break;
                }
                tempOWTXLog = tempOWTXLog.getParentTransaction();
            } //end while loop
            
            //Create OrderImplTXLog Root Transaction
            OrderHandlingInstructionTXChangeLog currentOrderHITXLog = new OrderHandlingInstructionTXChangeLog();
            currentOrderHITXLog.setOWCurrentTxLog(tempOWTXLog);
            currentOrderHITXLog.setParentTXChangeLog(null);
            orderHITXChangeLog = currentOrderHITXLog;
            
            firstOrderHITXChangeLog = orderHITXChangeLog;
        }
        
        if(orderHITXChangeLog != null)
        {
            TransactionLog currentCachedOWTXLog = orderHITXChangeLog.getOWCurrentTxLog();
            while(currentCachedOWTXLog != null && currentCachedOWTXLog != owRequestedTransactionParentLog)
            {
                if(currentCachedOWTXLog.getSubTransaction() == owRequestedTransactionLog)
                {
                    break;
                }
                OrderHandlingInstructionTXChangeLog currentOrderHITXLog = new OrderHandlingInstructionTXChangeLog();
                currentOrderHITXLog.setOWCurrentTxLog(currentCachedOWTXLog.getSubTransaction());
                currentOrderHITXLog.setParentTXChangeLog(orderHITXChangeLog);
                orderHITXChangeLog.setChildTXChangeLog(currentOrderHITXLog);
                orderHITXChangeLog = currentOrderHITXLog;
                currentCachedOWTXLog = orderHITXChangeLog.getOWCurrentTxLog();
            }
        }
    }



    public void commitChangesAndClearUndoLog()
    {
        super.commitChangesAndClearUndoLog();
        firstOrderHITXChangeLog = null;
        orderHITXChangeLog = null;
    }

    private void rollbackOrderFieldChanges()
    {
        boolean isRootTransactionRollback = super.rollbackFieldChanges(this, firstOrderHITXChangeLog, orderHITXChangeLog);
        
        if(isRootTransactionRollback)
        {
            super.commitChangesAndClearUndoLog();
            firstOrderHITXChangeLog = null;
            orderHITXChangeLog = null;
        }
    }


    /**
     * This method registers for the Transaction Listener if the object
     * is not marked dirty and does not contains any changes. If it is
     * already marked dirty then this method do nothing. The pusrpose is
     * to register the transaction Listener only once for this object during
     * the current transaction. The object is marked clean (not dirty) on
     * commit(after saving changes) or rollback (after undoing the changes).
     */
    public void markOrderHIFieldChanged()
    {
        if(!isObjectMarkedDirty())  //Already marked dirty object and register for the Transaction Lisenter
        {
            setObjectMarkedDirty(true);
            Transaction.registerListener(new TransactionListener()
             {
                public void commitEvent()
                {
                    commitChangesAndClearUndoLog();
                }
                public void rollbackEvent()
                {
                }
             }
            );

            Transaction.registerRollbackListenerWithPriority( BOSessionManager.getDefaultManager().getSession(), new TransactionListener()
            {
                public void commitEvent()
                {
                }

                public void rollbackEvent()
                {
                    rollbackOrderFieldChanges();
                }
            }
            );
        }
    }

    /********************** End Reflection Free OrderHI Related changes *****************/
}
