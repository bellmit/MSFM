package com.cboe.presentation.order;

//
//-----------------------------------------------------------------------------------
//Source file: ComplexOrderImpl.java
//
//PACKAGE: com.cboe.presentation.order
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2009 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------


import org.omg.CORBA.UserException;

import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.interfaces.presentation.order.LegOrderDetail;
import com.cboe.interfaces.presentation.order.MutableLegOrderDetail;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.presentation.product.SessionStrategyLeg;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;


public class ComplexOrderImpl extends StrategyOrderImpl
{ 
	public ComplexOrderImpl()
	{
	}
	
	public ComplexOrderImpl(OrderStruct orderStruct)
	{
	    super(orderStruct);
	}
	
	
	public void setSessionProduct(SessionProduct sessionProduct)
	{
	    if(sessionProduct instanceof SessionStrategy)
	    {
	        SessionProduct oldValue = getSessionProduct();
	        _setSessionProduct(sessionProduct);
	        setModified();
	        firePropertyChange(PROPERTY_SESSION_PRODUCT, oldValue, sessionProduct);
	
	        try
	        {
	            SessionProductClass newClass = APIHome.findProductQueryAPI().getClassByKeyForSession(
	                    getActiveSession(), getSessionProduct().getProductKeysStruct().classKey);
	            setSessionProductClass(newClass);
	        }
	        catch(UserException e)
	        {
	            DefaultExceptionHandlerHome.find().process(e, "Could not obtain class for product.");
	        }
	        
	    }
	    else
	    {
	        throw new IllegalArgumentException("sessionProduct must be an instance of SessionStrategy.");
	
	    }
	}
	
	public void setAuctionOrder(){
		newOrder = false;
	}
	
	public void loadDefaultLegs(){
			
        	clearLegDetails();
        	SessionStrategyLeg[] legs = ((SessionStrategy)sessionProduct).getSessionStrategyLegs();
        	for(int i = 0; i < legs.length; i++)
            {
                int productKey = legs[i].getProductKey();
                MutableLegOrderDetail detail = LegOrderDetailFactory.createMutableLegOrderDetail(
                        productKey);
                updateLegInfo(legs[i],detail);
                addLegOrderDetail(productKey, detail);
            }
        	
	}
	
	 public LegOrderDetail getLegOrder(int productKey)
	    {
	        Integer legKey = new Integer(productKey);
	        LegOrderDetail detail = (LegOrderDetail)getLegDetailsMap().get(legKey);
	        
	        if(detail == null)
	        {
	        	detail =  LegOrderDetailFactory.createMutableLegOrderDetail(
                        productKey);
	        }                          	        
	        return detail;
	    }
	
	
	/**
     * Implements Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        StrategyOrderImpl newImpl;
        if(isCreatedFromStruct())
        {
            newImpl = new ComplexOrderImpl(getStruct());
        }
        else
        {
            newImpl = new ComplexOrderImpl();
            // JMK - 8/26/04 - Set session strategy here only when creating an entirely new order
            // Otherwise it'll zero out leg details that were correctly propagated above from the struct
            newImpl.setSessionStrategy(getSessionStrategy());
        }

        return newImpl;
    }

	public void updateLegInfo(SessionStrategyLeg sessionLegs, MutableLegOrderDetail detail){
		detail.setSide(sessionLegs.getSide());
	}
}
