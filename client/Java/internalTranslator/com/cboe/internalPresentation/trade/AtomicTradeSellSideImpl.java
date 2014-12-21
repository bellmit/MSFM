// -----------------------------------------------------------------------------------
// Source file: AtomicTradeBuySideImpl.java
//
// PACKAGE: com.cboe.interfaces.internalPresentation.trade
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.trade;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.user.ExchangeFirmFactory;
import com.cboe.presentation.user.ExchangeAcronymFactory;
import com.cboe.presentation.util.CBOEIdImpl;

import com.cboe.interfaces.internalPresentation.trade.AtomicTradeSideModel;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.util.CBOEId;

import com.cboe.idl.trade.AtomicTradeStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.domain.util.StructBuilder;
import com.cboe.domain.util.TradeReportStructBuilder;

public class AtomicTradeSellSideImpl extends AbstractMutableBusinessModel implements AtomicTradeSideModel
{
    private AtomicTradeStruct atomicTradeStruct;

    private ExchangeFirm cmta;
    private ExchangeAcronym broker;
    private ExchangeAcronym originator;
    private ExchangeFirm firm;
    private CBOEId orderOrQuoteKey;

    protected AtomicTradeSellSideImpl()
    {
        super();
    }

    public AtomicTradeSellSideImpl(AtomicTradeStruct struct)
    {
        this();
        setAtomicTradeStruct(struct);
    }

    public Object clone() throws CloneNotSupportedException
    {
        AtomicTradeStruct clonedStruct = TradeReportStructBuilder.cloneAtomicTradeStruct(getAtomicTradeStruct());
        return new AtomicTradeSellSideImpl(clonedStruct);
    }

    public AtomicTradeStruct getAtomicTradeStruct()
    {
        return this.atomicTradeStruct;
    }

    protected void setAtomicTradeStruct(AtomicTradeStruct struct)
    {
        this.atomicTradeStruct = struct;
        cmta = null;
        broker = null;
        originator = null;
        firm = null;
        orderOrQuoteKey = null;
    }

    public void setOriginType(char originType)
    {
        checkState(getAtomicTradeStruct());
        if ( getOriginType() != originType )
        {
            Character oldValue = new Character(getOriginType());
            getAtomicTradeStruct().sellerOriginType = originType;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, new Character(originType));
        }
    }

    public char getOriginType()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sellerOriginType;
    }

    public void setFirmBranch(String firmBranch)
    {
        checkState(getAtomicTradeStruct());
        checkParam(firmBranch, "firmBranch");
        if (!getFirmBranch().equals(firmBranch))
        {
            String oldValue = getFirmBranch();
            getAtomicTradeStruct().sellerFirmBranch = firmBranch;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, firmBranch);
        }
    }

    public String getFirmBranch()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sellerFirmBranch;
    }

    public void setFirmBranchSequenceNumber(int sequenceNumber)
    {
        checkState(getAtomicTradeStruct());
        if (getFirmBranchSequenceNumber()!=sequenceNumber)
        {
            int oldValue = getFirmBranchSequenceNumber();
            getAtomicTradeStruct().sellerFirmBranchSequenceNumber = sequenceNumber;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, sequenceNumber);
        }
    }

    public int getFirmBranchSequenceNumber()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sellerFirmBranchSequenceNumber;
    }

    protected void setCMTAStruct(ExchangeFirmStruct cmtaStruct, boolean fireEvents)
    {
        checkState(getAtomicTradeStruct());
        checkParam(cmtaStruct, "cmta");
        ExchangeFirmStruct oldValue = getCMTAStruct();
        if (oldValue.exchange != cmtaStruct.exchange ||
            oldValue.firmNumber != cmtaStruct.firmNumber)
        {
            getAtomicTradeStruct().sellerCmta = cmtaStruct;
            if (fireEvents)
            {
                this.cmta = null;
                setModified(true);
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, cmtaStruct);
            }
        }
    }
    public void setCMTA(ExchangeFirm cmta)
    {
        checkParam(cmta, "cmta");
        if (!getCMTA().equals(cmta))
        {
            ExchangeFirm oldValue = getCMTA();
            this.cmta = cmta;
            setCMTAStruct(cmta.getExchangeFirmStruct(), false);
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, cmta);
        }
    }

    public ExchangeFirm getCMTA()
    {
        if (cmta == null)
        {
            cmta = ExchangeFirmFactory.createExchangeFirm(getCMTAStruct());
        }
        return cmta;
    }

    protected ExchangeFirmStruct getCMTAStruct()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sellerCmta;
    }

    public void setCorrespondentId(String correspondentId)
    {
        checkState(getAtomicTradeStruct());
        checkParam(correspondentId, "correspondent ID");
        if (!getCorrespondentId().equals(correspondentId))
        {
            String oldValue = getCorrespondentId();
            getAtomicTradeStruct().sellerCorrespondentId = correspondentId;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, correspondentId);
        }
    }

    public String getCorrespondentId()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sellerCorrespondentId;
    }

    public void setPositionEffect(char positionEffect)
    {
        checkState(getAtomicTradeStruct());
        if (getPositionEffect() != positionEffect)
        {
            Character oldValue = new Character(getPositionEffect());
            getAtomicTradeStruct().sellerPositionEffect = positionEffect;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, new Character(positionEffect));
        }
    }

    public char getPositionEffect()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sellerPositionEffect;
    }

    public void setAccount(String account)
    {
        checkState(getAtomicTradeStruct());
        checkParam(account, "account");
        if (!getAccount().equals(account))
        {
            String oldValue = getAccount();
            getAtomicTradeStruct().sellerAccount = account;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, account);
        }
    }

    public String getAccount()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sellerAccount;
    }

    public void setSubaccount(String subaccount)
    {
        checkState(getAtomicTradeStruct());
        checkParam(subaccount, "subaccount");
        if (!getSubaccount().equals(subaccount))
        {
            String oldValue = getSubaccount();
            getAtomicTradeStruct().sellerSubaccount = subaccount;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, subaccount);
        }
    }

    public String getSubaccount()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sellerSubaccount;
    }

    public void setBroker(ExchangeAcronym broker)
    {
        checkState(getAtomicTradeStruct());
        checkParam(broker, "broker");
        if (!getBroker().equals(broker))
        {
            ExchangeAcronym oldValue = getBroker();
            this.broker = broker;
            setBrokerStruct(broker.getExchangeAcronymStruct(), false);
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, broker);
        }
    }

    public ExchangeAcronym getBroker()
    {
        if (broker == null)
        {
            broker = ExchangeAcronymFactory.createExchangeAcronym(getBrokerStruct());
        }
        return broker;
    }

    protected void setBrokerStruct(ExchangeAcronymStruct broker, boolean fireEvents)
    {
        checkState(getAtomicTradeStruct());
        checkParam(broker, "broker");
        ExchangeAcronymStruct oldValue = getBrokerStruct();
        if ( oldValue.exchange != broker.exchange ||
             oldValue.acronym != broker.acronym )
        {
            getAtomicTradeStruct().sellerBroker = broker;
            if (fireEvents)
            {
                this.broker = null;
                setModified(true);
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, broker);
            }
        }
    }

    protected ExchangeAcronymStruct getBrokerStruct()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sellerBroker;
    }

    public void setOriginator(ExchangeAcronym originator)
    {
        checkState(getAtomicTradeStruct());
        checkParam(originator, "originator");
        if (!getOriginator().equals(originator))
        {
            ExchangeAcronym oldValue = getOriginator();
            this.originator = originator;
            setOriginatorStruct(originator.getExchangeAcronymStruct(), false);
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, originator);
        }

    }

    public ExchangeAcronym getOriginator()
    {
        if (originator == null)
        {
            originator = ExchangeAcronymFactory.createExchangeAcronym(getOriginatorStruct());
        }
        return originator;
    }

    protected void setOriginatorStruct(ExchangeAcronymStruct originator, boolean fireEvents)
    {
        checkState(getAtomicTradeStruct());
        checkParam(originator, "originator");
        ExchangeAcronymStruct oldValue = getOriginatorStruct();
        if ( oldValue.exchange != originator.exchange ||
             oldValue.acronym != originator.acronym )
        {
            getAtomicTradeStruct().sellerOriginator = originator;
            if (fireEvents)
            {
                this.originator = null;
                setModified(true);
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, originator);
            }
        }

    }

    protected ExchangeAcronymStruct getOriginatorStruct()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sellerOriginator;
    }


    public void setFirm(ExchangeFirm firm)
    {
        checkState(getAtomicTradeStruct());
        checkParam(firm, "firm");
        if (!getFirm().equals(firm))
        {
            ExchangeFirm oldValue = getFirm();
            this.firm = firm;
            setFirmStruct(firm.getExchangeFirmStruct(), false);
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, firm);
        }
    }

    public ExchangeFirm getFirm()
    {
        if(firm == null)
        {
            firm = ExchangeFirmFactory.createExchangeFirm(getFirmStruct());
        }
        return firm;
    }

    protected void setFirmStruct(ExchangeFirmStruct firm, boolean fireEvents)
    {
        checkState(getAtomicTradeStruct());
        checkParam(firm, "firm");
        ExchangeFirmStruct oldValue = getFirmStruct();
        if ( oldValue.exchange != firm.exchange ||
             oldValue.firmNumber != firm.firmNumber )
        {
            getAtomicTradeStruct().sellerFirm = firm;
            if (fireEvents)
            {
                this.firm = null;
                setModified(true);
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, firm);
            }
        }
    }

    protected ExchangeFirmStruct getFirmStruct()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sellerFirm;
    }


    public void setOptionalData(String optionalData)
    {
        checkState(getAtomicTradeStruct());
        checkParam(optionalData, "optionalData");
        if (!getOptionalData().equals(optionalData))
        {
            String oldValue = getOptionalData();
            getAtomicTradeStruct().sellerOptionalData = optionalData;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, optionalData);
        }
    }

    public String getOptionalData()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sellerOptionalData;
    }

    public void setOrderOrQuoteKey(CBOEId key)
    {
        checkState(getAtomicTradeStruct());
        checkParam(key, "OrderOrQuoteKey");
        if (!getOrderOrQuoteKey().equals(key))
        {
            CBOEId oldValue = getOrderOrQuoteKey();
            this.orderOrQuoteKey = key;
            setOrderOrQuoteKeyStruct(key.getStruct(), false);
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, key);
        }
    }

    public CBOEId getOrderOrQuoteKey()
    {
        if (orderOrQuoteKey == null)
        {
            orderOrQuoteKey = new CBOEIdImpl(getOrderOrQuoteKeyStruct());
        }
        return orderOrQuoteKey;
    }

    protected void setOrderOrQuoteKeyStruct(CboeIdStruct key, boolean fireEvents)
    {
        checkState(getAtomicTradeStruct());
        checkParam(key, "OrderOrQuoteKey");
        CboeIdStruct oldValue = getOrderOrQuoteKeyStruct();
        if ( !StructBuilder.isEqual(oldValue, key) )
        {
            getAtomicTradeStruct().sellerOrderOrQuoteKey = key;
            if (fireEvents)
            {
                this.orderOrQuoteKey = null;
                setModified(true);
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, key);
            }
        }
    }

    protected CboeIdStruct getOrderOrQuoteKeyStruct()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sellerOrderOrQuoteKey;
    }

    public void setOrderOrQuote(boolean orderOrQuote)
    {
        checkState(getAtomicTradeStruct());
        if (isOrderOrQuote() != orderOrQuote)
        {
            boolean oldValue = isOrderOrQuote();
            getAtomicTradeStruct().sellerOrderOrQuote = orderOrQuote;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, orderOrQuote);
        }
    }

    public boolean isOrderOrQuote()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sellerOrderOrQuote;
    }

    public void setReinstatable(boolean reinstatable)
    {
        checkState(getAtomicTradeStruct());
        if (isReinstatable() != reinstatable)
        {
            boolean oldValue = isReinstatable();
            getAtomicTradeStruct().reinstatableForSeller = reinstatable;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, reinstatable);
        }
    }

    public boolean isReinstatable()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().reinstatableForSeller;
    }

}
