//
//-----------------------------------------------------------------------------------
//Source file: NonECQueryResult.java
//
//PACKAGE: package com.cboe.infra.presentation.traderService;
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

public class NonECQueryResult
{
    public String orbName;
    public String hostName;
    public String portNumber;
    public String pOAName;
    public String pOAState;
    public String isPersistent;
    public String isAlive;
    public String bindOrder;
    public String[] offerProperties;

    public NonECQueryResult()
    {
    }

    public NonECQueryResult(
            String orbName,
            String hostName,
            String portNumber,
            String pOAName,
            String pOAState,
            String isPersistent,
            String isAlive,
            String bindOrder,
            String[] offerProperties
            )
    {
        this.orbName = orbName;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.pOAName = pOAName;
        this.pOAState = pOAState;
        this.isPersistent = isPersistent;
        this.isAlive = isAlive;
        this.bindOrder = bindOrder;
        this.offerProperties = offerProperties;
    }

    /**
     * @return Returns the bindOrder.
     */
    public String getBindOrder()
    {
        return this.bindOrder;
    }

    /**
     * @param bindOrder The bindOrder to set.
     */
    public void setBindOrder(String bindOrder)
    {
        this.bindOrder = bindOrder;
    }

    /**
     * @return Returns the host.
     */
    public String getHostName()
    {
        return this.hostName;
    }

    /**
     * @param host The host to set.
     */
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }

    /**
     * @return Returns the isAlive.
     */
    public String getIsAlive()
    {
        return this.isAlive;
    }

    /**
     * @param isAlive The isAlive to set.
     */
    public void setIsAlive(String isAlive)
    {
        this.isAlive = isAlive;
    }

    /**
     * @return Returns the isPersistent.
     */
    public String getIsPersistent()
    {
        return this.isPersistent;
    }

    /**
     * @param isPersistent The isPersistent to set.
     */
    public void setIsPersistent(String isPersistent)
    {
        this.isPersistent = isPersistent;
    }

    /**
     * @return Returns the offerProperties.
     */
    public String[] getOfferProperties()
    {
        return this.offerProperties;
    }

    /**
     * @param offerProperties The offerProperties to set.
     */
    public void setOfferProperties(String[] offerProperties)
    {
        this.offerProperties = offerProperties;
    }

    /**
     * @return Returns the orbName.
     */
    public String getOrbName()
    {
        return this.orbName;
    }

    /**
     * @param orbName The orbName to set.
     */
    public void setOrbName(String orbName)
    {
        this.orbName = orbName;
    }

    /**
     * @return Returns the pOAName.
     */
    public String getPOAName()
    {
        return this.pOAName;
    }

    /**
     * @param name The pOAName to set.
     */
    public void setPOAName(String name)
    {
        this.pOAName = name;
    }

    /**
     * @return Returns the pOAState.
     */
    public String getPOAState()
    {
        return this.pOAState;
    }

    /**
     * @param state The pOAState to set.
     */
    public void setPOAState(String state)
    {
        this.pOAState = state;
    }

    /**
     * @return Returns the port.
     */
    public String getPortNumber()
    {
        return this.portNumber;
    }

    /**
     * @param port The port to set.
     */
    public void setPortNumber(String portNumber)
    {
        this.portNumber = portNumber;
    }
}
