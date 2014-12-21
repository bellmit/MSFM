//
// -----------------------------------------------------------------------------------
// Source file: OrderIdImpl.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import java.util.*;

import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.order.MutableOrderId;
import com.cboe.interfaces.presentation.util.CBOEId;

import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;

import com.cboe.presentation.user.ExchangeFirmFactory;
import com.cboe.presentation.util.CBOEIdImpl;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;

import com.cboe.domain.util.StructBuilder;

class OrderIdImpl extends AbstractMutableBusinessModel implements MutableOrderId
{
    protected ExchangeFirm executingOrGiveUpFirm;
    protected String branch;
    protected Integer branchSequenceNumber;
    protected String correspondentFirm;
    protected String orderDate;
    protected int highCboeId;
    protected int lowCboeId;
    protected CBOEId cboeId;
    protected OrderIdStruct orderIdStruct;

    protected String formattedBranchSequence;

    private static final Comparator myStdComparator = new Comparator()
    {
        public int compare(Object order1, Object order2)
        {
            OrderId order1Id = (OrderId)order1;
            OrderId order2Id = (OrderId)order2;

            return order1Id.compareTo(order2Id);
        }
    };
    
    public OrderIdImpl(OrderIdStruct orderIdStruct)
    {
        this.orderIdStruct = orderIdStruct;
        checkParam(orderIdStruct, "OrderIdStruct");
        initialize();
    }

    /**
     * Returns a hash or the low and high id's
     */
    public int hashCode()
    {
        return cboeId.hashCode();
    }

    public Object clone() throws CloneNotSupportedException
    {
        OrderIdStruct struct = new OrderIdStruct(
                executingOrGiveUpFirm.getExchangeFirmStruct(),
                branch,
                branchSequenceNumber.intValue(),
                correspondentFirm,
                orderDate,
                highCboeId,
                lowCboeId);
        OrderIdImpl impl = new OrderIdImpl(struct);
        return impl;
    }

    public boolean equals(Object otherOrderId)
    {
        boolean equal = super.equals(otherOrderId);

        if(!equal)
        {
            OrderId castedObject = (OrderId)otherOrderId;

            if(getLowCboeId() > 0 && getHighCboeId() > 0)
            {
                equal = getCboeId().equals(castedObject.getCboeId());
            }
            else
            {
                equal = getExecutingOrGiveUpFirm().equals(castedObject.getExecutingOrGiveUpFirm()) &&
                        getBranch().equals(castedObject.getBranch()) &&
                        getBranchSequenceNumber().equals(castedObject.getBranchSequenceNumber()) &&
                        getCorrespondentFirm().equals(castedObject.getCorrespondentFirm()) &&
                        getOrderDate().equals(castedObject.getOrderDate());
            }
        }
        return equal;
    }

    public int compareTo(Object otherOrderId)
    {
        int result = 0;

        if(!equals(otherOrderId))
        {
            OrderId castedObject = (OrderId)otherOrderId;

            if(getLowCboeId() > 0 && getHighCboeId() > 0)
            {
                result = getCboeId().compareTo(castedObject.getCboeId());
            }
            else
            {
                result = getExecutingOrGiveUpFirm().compareTo(castedObject.getExecutingOrGiveUpFirm());
                if(result == 0)
                {
                    result = getBranch().compareTo(castedObject.getBranch());
                    if(result == 0)
                    {
                        int thisVal = getBranchSequenceNumber().intValue();
                        int anotherVal = castedObject.getBranchSequenceNumber().intValue();
                        result = (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
                        if(result == 0)
                        {
                            result = getCorrespondentFirm().compareTo(castedObject.getCorrespondentFirm());
                            if(result == 0)
                            {
                                result = getOrderDate().compareTo(castedObject.getOrderDate());
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public ExchangeFirm getExecutingOrGiveUpFirm()
    {
        return executingOrGiveUpFirm;
    }

    public String getBranch()
    {
        return branch;
    }

    public Integer getBranchSequenceNumber()
    {
        return branchSequenceNumber;
    }

    public String getCorrespondentFirm()
    {
        return correspondentFirm;
    }

    public String getOrderDate()
    {
        return orderDate;
    }

    public CBOEId getCboeId()
    {
        return cboeId;
    }

    public int getHighCboeId()
    {
        return highCboeId;
    }

    public int getLowCboeId()
    {
        return lowCboeId;
    }

    /**
     * Gets the underlying struct
     * @return OrderIdStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public OrderIdStruct getStruct()
    {
        return orderIdStruct;
    }

    public String getFormattedBranchSequence()
    {
        if(formattedBranchSequence == null)
        {
            StringBuffer buffer = new StringBuffer(20);
            buffer.append(getBranch()).append(':').append(getBranchSequenceNumber());
            formattedBranchSequence = buffer.toString();
        }
        return formattedBranchSequence;
    }

    public void setBranch(String newValue)
    {
        Object oldValue = getBranch();
        branch = newValue;
        orderIdStruct.branch = branch;
        formattedBranchSequence = null;
        setModified();
        firePropertyChange(PROPERTY_BRANCH, oldValue, newValue);
    }

    public void setBranchSequenceNumber(Integer newValue)
    {
        Object oldValue = getBranchSequenceNumber();
        branchSequenceNumber = newValue;
        orderIdStruct.branchSequenceNumber = branchSequenceNumber.intValue();
        formattedBranchSequence = null;
        setModified();
        firePropertyChange(PROPERTY_BRANCH_SEQUENCE, oldValue, newValue);
    }

    public void setCboeId(CBOEId newValue)
    {
        Object oldValue = getCboeId();
        cboeId = newValue;
        lowCboeId = cboeId.getLowId();
        highCboeId = cboeId.getHighId();
        orderIdStruct.highCboeId = highCboeId;
        orderIdStruct.lowCboeId = lowCboeId;
        setModified();
        firePropertyChange(PROPERTY_CBOE_ID, oldValue, newValue);
    }

    public void setCorrespondentFirm(String newValue)
    {
        Object oldValue = getCorrespondentFirm();
        correspondentFirm = newValue;
        orderIdStruct.correspondentFirm = correspondentFirm;
        setModified();
        firePropertyChange(PROPERTY_CORRESPONDENT_FIRM, oldValue, newValue);
    }

    public void setExecutingOrGiveUpFirm(ExchangeFirm newValue)
    {
        Object oldValue = getExecutingOrGiveUpFirm();
        if (newValue == null)
        {
            executingOrGiveUpFirm = ExchangeFirmFactory.createExchangeFirm("", "");
        }
        else
        {
            executingOrGiveUpFirm = newValue;
        }
        orderIdStruct.executingOrGiveUpFirm = executingOrGiveUpFirm.getExchangeFirmStruct();
        setModified();
        firePropertyChange(PROPERTY_EXECUTING_GIVEUP_FIRM, oldValue, executingOrGiveUpFirm);
    }

    public void setOrderDate(String newValue)
    {
        Object oldValue = getOrderDate();
        orderDate = newValue;
        orderIdStruct.orderDate = orderDate;
        setModified();
        firePropertyChange(PROPERTY_ORDER_DATE, oldValue, newValue);
    }

    protected void setModified()
    {
        setModified(true);
    }

    private void initialize()
    {
        formattedBranchSequence = null;

        setComparator(myStdComparator);

        if(orderIdStruct.executingOrGiveUpFirm != null)
        {
            executingOrGiveUpFirm = ExchangeFirmFactory.createExchangeFirm(orderIdStruct.executingOrGiveUpFirm);
        }
        else
        {
            executingOrGiveUpFirm = ExchangeFirmFactory.createExchangeFirm("", "");
            orderIdStruct.executingOrGiveUpFirm = executingOrGiveUpFirm.getExchangeFirmStruct();
        }
        if(orderIdStruct.branch != null)
        {
            branch = new String(orderIdStruct.branch);
        }
        else
        {
            branch = "";
            orderIdStruct.branch = branch;
        }
        branchSequenceNumber = new Integer(orderIdStruct.branchSequenceNumber);
        if(orderIdStruct.correspondentFirm != null)
        {
            correspondentFirm = new String(orderIdStruct.correspondentFirm);
        }
        else
        {
            correspondentFirm = "";
            orderIdStruct.correspondentFirm = correspondentFirm;
        }
        if(orderIdStruct.orderDate != null)
        {
            orderDate = new String(orderIdStruct.orderDate);
        }
        else
        {
            orderDate = "";
            orderIdStruct.orderDate = orderDate;
        }
        highCboeId = orderIdStruct.highCboeId;
        lowCboeId = orderIdStruct.lowCboeId;

        CboeIdStruct struct = StructBuilder.buildCboeIdStruct();
        struct.highCboeId = getHighCboeId();
        struct.lowCboeId = getLowCboeId();
        cboeId = new CBOEIdImpl(struct);
    }
}