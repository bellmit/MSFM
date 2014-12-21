package com.cboe.domain.order;

import java.util.StringTokenizer;
import java.lang.reflect.Field;

import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.order.OrderHandlingInstructionStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.HandlingInstruction;
import com.cboe.interfaces.domain.Order;
import com.cboe.domain.util.PriceFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.TransactionalBObject;

/**
 * This class is designed to hold on any order handling instructions
 */

public class OrderHandlingInstructionImpl extends TransactionalBObject implements HandlingInstruction {

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

    static Field _oppositeSideBOTR;
	static Field _executionPrice;
	static Field _originalExecutionPrice;
    static Field _maximumExecutionVolume;
    static Field _maximumExecutionVolumeReason;
    static Field _tradingRestriction;
    static Field _remainderHandlingMode;
    static Field _ignoreContingency;
    static Field _orderQuantityAtReceiveTime;
    static Field _tradedVolume;
    static Field _maxTradableQtyForIndexHybrid;
    static Field _overRideReasonForIndexHybrid;
    static Field _expressOrder;
    static Field _bookableOrderMarketLimit;
    
    private boolean bypassReflection = false;
    
	static { /*NAME:fieldDefinition:*/
		try{
			_oppositeSideBOTR = OrderHandlingInstructionImpl.class.getDeclaredField("oppositeSideBOTR");
			_executionPrice = OrderHandlingInstructionImpl.class.getDeclaredField("executionPrice");
			_originalExecutionPrice = OrderHandlingInstructionImpl.class.getDeclaredField("originalExecutionPrice");
            _maximumExecutionVolume = OrderHandlingInstructionImpl.class.getDeclaredField("maximumExecutionVolume");
            _maximumExecutionVolumeReason = OrderHandlingInstructionImpl.class.getDeclaredField("maximumExecutionVolumeReason");
            _tradingRestriction = OrderHandlingInstructionImpl.class.getDeclaredField("tradingRestriction");
            _remainderHandlingMode = OrderHandlingInstructionImpl.class.getDeclaredField("remainderHandlingMode");
            _ignoreContingency = OrderHandlingInstructionImpl.class.getDeclaredField("ignoreContingency");
            _orderQuantityAtReceiveTime = OrderHandlingInstructionImpl.class.getDeclaredField("orderQuantityAtReceiveTime");
            _tradedVolume = OrderHandlingInstructionImpl.class.getDeclaredField("tradedVolume");
            _maxTradableQtyForIndexHybrid = OrderHandlingInstructionImpl.class.getDeclaredField("maxTradableQtyForIndexHybrid");
            _overRideReasonForIndexHybrid = OrderHandlingInstructionImpl.class.getDeclaredField("overRideReasonForIndexHybrid");
            _expressOrder = OrderHandlingInstructionImpl.class.getDeclaredField("expressOrder");
            _bookableOrderMarketLimit = OrderHandlingInstructionImpl.class.getDeclaredField("bookableOrderMarketLimit");
            	
            _oppositeSideBOTR.setAccessible(true);
            _executionPrice.setAccessible(true);
            _originalExecutionPrice.setAccessible(true);
            _maximumExecutionVolume.setAccessible(true);
            _maximumExecutionVolumeReason.setAccessible(true);
            _tradingRestriction.setAccessible(true);
            _remainderHandlingMode.setAccessible(true);
            _ignoreContingency.setAccessible(true);
            _orderQuantityAtReceiveTime.setAccessible(true);
            _tradedVolume.setAccessible(true);
            _maxTradableQtyForIndexHybrid.setAccessible(true);
            _overRideReasonForIndexHybrid.setAccessible(true);
            _expressOrder.setAccessible(true);
            _bookableOrderMarketLimit.setAccessible(true);
        }
		catch (NoSuchFieldException ex) { System.out.println(ex); }
	}

    public OrderHandlingInstructionImpl() {
        setDefaultMaxTradableQtyForIndexHybrid();
        setDefaultOverRideReasonForIndexHybrid();
        
    }

    public Price getOppositeSideBOTR(){
        return (Price)(bypassReflection?oppositeSideBOTR:editor.get(_oppositeSideBOTR, oppositeSideBOTR));
    }

    public Price getExecutionPrice(){
        return (Price)(bypassReflection?executionPrice:editor.get(_executionPrice, executionPrice));
    }

    public Price getOriginalExecutionPrice(){
        return (Price)(bypassReflection?originalExecutionPrice:editor.get(_originalExecutionPrice, originalExecutionPrice));
    }

    public int getMaximumExecutionVolume(){
        return bypassReflection?maximumExecutionVolume:editor.get(_maximumExecutionVolume, maximumExecutionVolume);
    }

    public int getTradedVolume(){
        return bypassReflection?tradedVolume:editor.get(_tradedVolume, tradedVolume);
    }

    public short getMaximumExecutionVolumeReason(){
        return bypassReflection?maximumExecutionVolumeReason:editor.get(_maximumExecutionVolumeReason, maximumExecutionVolumeReason);
    }

    public short getTradingRestriction(){
        return bypassReflection?tradingRestriction:editor.get(_tradingRestriction, tradingRestriction);
    }

    public short getRemainderHandlingMode(){
        return bypassReflection?remainderHandlingMode:editor.get(_remainderHandlingMode, remainderHandlingMode);
    }

    public boolean ignoreContingency(){
        return  bypassReflection?ignoreContingency:editor.get(_ignoreContingency, ignoreContingency);
    }

    public int getOrderQuantityReceived(){
        return  bypassReflection?orderQuantityAtReceiveTime:editor.get(_orderQuantityAtReceiveTime, orderQuantityAtReceiveTime);
    }
    public short getOverRideReasonForIndexHybrid(){
        return  bypassReflection?overRideReasonForIndexHybrid:editor.get(_overRideReasonForIndexHybrid, overRideReasonForIndexHybrid);
    }

    public int getMaxTradableQtyForIndexHybrid(){
        return  bypassReflection?maxTradableQtyForIndexHybrid:editor.get(_maxTradableQtyForIndexHybrid, maxTradableQtyForIndexHybrid);
    }
    
    public void setOverRideReasonForIndexHybrid(short aOverRideReasonForIndexHybrid){
        editor.set(_overRideReasonForIndexHybrid, aOverRideReasonForIndexHybrid, overRideReasonForIndexHybrid);
    }

    public void setDefaultOverRideReasonForIndexHybrid(){
        setOverRideReasonForIndexHybrid(DEFAULT_OVERRIDE_REASON_FOR_INDEX_HYBRID);
    }
    
    public boolean isOrderReturnReasonSetForIndexHybrid()
    {
        return  getOverRideReasonForIndexHybrid() != DEFAULT_OVERRIDE_REASON_FOR_INDEX_HYBRID ; 
    }    

    public boolean isExpressOrder(){
        return  bypassReflection?expressOrder:editor.get(_expressOrder, expressOrder);
    }
    
    public double getBookableOrderMarketLimit(){
        return  bypassReflection?bookableOrderMarketLimit:editor.get(_bookableOrderMarketLimit, bookableOrderMarketLimit);
    }

    public void setMaxTradableQtyForIndexHybrid(int aMaxTradableQtyForIndexHybrid){
        editor.set(_maxTradableQtyForIndexHybrid, aMaxTradableQtyForIndexHybrid, maxTradableQtyForIndexHybrid);
    }

    public void setDefaultMaxTradableQtyForIndexHybrid(){
        setMaxTradableQtyForIndexHybrid(DEFAULT_TRADABLE_QTY_FOR_INDEX_HYBRID);
    }
    
    public boolean isDefaultMaxTradableQtyForIndexHybrid()
    {
        return  getMaxTradableQtyForIndexHybrid() == DEFAULT_TRADABLE_QTY_FOR_INDEX_HYBRID; 
    }

    public void setOppositeSideBOTR(Price aBOTR){
	    editor.set(_oppositeSideBOTR, aBOTR, oppositeSideBOTR);
    }

    public void setExecutionPrice(Price aPrice){
        editor.set(_executionPrice, aPrice, executionPrice);
    }

    public void setOriginalExecutionPrice(Price aPrice){
        editor.set(_originalExecutionPrice, aPrice, originalExecutionPrice);
    }

    public void setMaximumExecutionVolume(int aVolume){
        editor.set(_maximumExecutionVolume, aVolume, maximumExecutionVolume);
    }

    public void setTradedVolume(int aVolume){
        editor.set(_tradedVolume, aVolume, tradedVolume);
    }

    public void setMaximumExecutionVolumeReason(short aReason){
        editor.set(_maximumExecutionVolumeReason, aReason, maximumExecutionVolumeReason);
    }

    public void setTradingRestriction(short aRestriction){
        editor.set(_tradingRestriction, aRestriction, tradingRestriction);
    }

    public void setRemainderHandlingMode(short aHandlingMode){
        editor.set(_remainderHandlingMode, aHandlingMode, remainderHandlingMode);
    }

    public void setIgnoreContingency(boolean aBoolean){
        editor.set(_ignoreContingency, aBoolean, ignoreContingency);
    }

    public void setOrderQuantityReceived(int aQuantity){
        editor.set(_orderQuantityAtReceiveTime, aQuantity, orderQuantityAtReceiveTime);
    }

    public void setExpressOrder(boolean aBoolean){
        editor.set(_expressOrder, aBoolean, expressOrder);
    }
    
    public void setBookableOrderMarketLimit(double aLimit){
        editor.set(_bookableOrderMarketLimit, aLimit, bookableOrderMarketLimit);
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
            Log.information("OrderHandlingInstructionImpl >>> less than 8 fields are in instruction: " + instruction);
            Log.information("OrderHandlingInstructionImpl >>> order handling instruction is not updated.");
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
            Log.alarm(this, "OrderHandlingInstructionImpl.toPersistenceString(): OppositeSideBOTR is null, setting to No Price");
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
            Log.alarm(this, "OrderHandlingInstructionImpl.toPersistenceString(): ExecutionPrice is null, setting to No Price");
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

	public boolean isBypassReflection() {
		return bypassReflection;
	}

	public void setBypassReflection(boolean bypassReflection) {
		this.bypassReflection = bypassReflection;
	}
    
}
