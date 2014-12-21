
/**
 * Title:        SBT GUI Project<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Nick DePasquale<p>
 * Company:      Chicago Board Options Exchange<p>
 * @author Nick DePasquale
 * @version 1.0
 */
package com.cboe.presentation.exampleStructs;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.idl.cmiConstants.VolumeTypes;

public class ExampleMarketVolumeStruct
{

    public ExampleMarketVolumeStruct()
    {
    }

    /**
    * @author Nick DePasquale
    *
    * @return MarketVolumeStruct
    */
    public static  MarketVolumeStruct getExampleMarketVolumeStruct()
    {
        //MarketVolumeStruct(short __contingencyType, int __quantity, boolean __multipleParties)
        MarketVolumeStruct aMarketVolumeStruct = new MarketVolumeStruct(VolumeTypes.LIMIT,10,false);

        return aMarketVolumeStruct;
    }

    /**
    * @author Nick DePasquale
    *
    * @return MarketVolumeStruct
    */
    public static  MarketVolumeStruct getExampleAONMarketVolumeStruct()
    {
        MarketVolumeStruct aMarketVolumeStruct = getExampleMarketVolumeStruct();
        aMarketVolumeStruct.volumeType = VolumeTypes.AON;
        aMarketVolumeStruct.quantity = 12;

        return aMarketVolumeStruct;
    }

    /**
    * @author Nick DePasquale
    *
    * @return MarketVolumeStruct
    */
    public static  MarketVolumeStruct getExampleIOCMarketVolumeStruct()
    {
        MarketVolumeStruct aMarketVolumeStruct = getExampleMarketVolumeStruct();
        aMarketVolumeStruct.volumeType = VolumeTypes.IOC;
        aMarketVolumeStruct.quantity = 14;

        return aMarketVolumeStruct;
    }

    /**
    * @author Nick DePasquale
    *
    * @return MarketVolumeStruct
    */
    public static  MarketVolumeStruct getExampleFOKMarketVolumeStruct()
    {
        MarketVolumeStruct aMarketVolumeStruct = getExampleMarketVolumeStruct();
        aMarketVolumeStruct.volumeType = VolumeTypes.FOK;
        aMarketVolumeStruct.quantity = 7;

        return aMarketVolumeStruct;
    }

    /**
    * @author Nick DePasquale
    * DO NOT MODIFY without first checking for the dependancies other methods
    * have based on the sequence and content of the array that is returned
    * @return MarketVolumeStruct[]
    */
    public static  MarketVolumeStruct[] getExampleMarketVolumeStructList()
    {
        MarketVolumeStruct marketVolumeStructList[] = new MarketVolumeStruct[4];

        // do not change the order of these items in the array
        // other methods use this as a base to create other lists
        marketVolumeStructList[0] = getExampleMarketVolumeStruct();
        marketVolumeStructList[1] = getExampleIOCMarketVolumeStruct();

        marketVolumeStructList[2] = getExampleAONMarketVolumeStruct();
        marketVolumeStructList[3] = getExampleFOKMarketVolumeStruct();

        return marketVolumeStructList;

    }

    /**
    * @author Nick DePasquale
    *
    * @return MarketVolumeStruct[]
    */
    public static  MarketVolumeStruct[] getExampleMarketVolumeStructListMultiPartyIOC()
    {
        MarketVolumeStruct [] marketVolumeStructList = getExampleMarketVolumeStructList();

        marketVolumeStructList[1].multipleParties = true;

        return marketVolumeStructList;

    }

        /**
    * @author Nick DePasquale
    * Same List as getExampleMarketVolumeStructList, except  MultiParty is set for AON
    * @return MarketVolumeStruct[]
    */
    public static  MarketVolumeStruct[] getExampleMarketVolumeStructListMultiPartyAON()
    {
        MarketVolumeStruct [] marketVolumeStructList = getExampleMarketVolumeStructList();

        marketVolumeStructList[2].multipleParties = true;

        return marketVolumeStructList;

    }
    /**
    * @author Nick DePasquale
    * only limit orders
    * @return MarketVolumeStruct[]
    */
    public static  MarketVolumeStruct[] getExampleMarketVolumeStructListLimitOrdersOnly()
    {
        MarketVolumeStruct [] marketVolumeStructList = getExampleMarketVolumeStructList();

        marketVolumeStructList[1] = null;
        marketVolumeStructList[2] = null;
        marketVolumeStructList[3] = null;

        return marketVolumeStructList;

    }

    /**
    * @author Nick DePasquale
    * only limit orders
    * @return MarketVolumeStruct[]
    */
    public static  MarketVolumeStruct[] getExampleMarketVolumeStructListLimitOrdersIOCAON()
    {
        MarketVolumeStruct [] marketVolumeStructList = getExampleMarketVolumeStructList();

        marketVolumeStructList[3] = null;

        return marketVolumeStructList;

    }
    /**
    * @author Nick DePasquale
    * only limit orders
    * @return MarketVolumeStruct[]
    */
    public static  MarketVolumeStruct[] getExampleMarketVolumeStructListLimitOrdersIOCFOK()
    {
        MarketVolumeStruct [] marketVolumeStructList = getExampleMarketVolumeStructList();

        marketVolumeStructList[2] = null;

        return marketVolumeStructList;

    }

    /***
    * @author Nick DePasquale
    * limit orders and IOC
    * @return MarketVolumeStruct[]
    */
    public static  MarketVolumeStruct[] getExampleMarketVolumeStructListLimitIOC()
    {
        MarketVolumeStruct [] marketVolumeStructList = getExampleMarketVolumeStructList();

        marketVolumeStructList[2] = null;
        marketVolumeStructList[3] = null;

        return marketVolumeStructList;

    }
    /**
    * @author Nick DePasquale
    * Same List as getExampleMarketVolumeStructList, except  MultiParty is set for all
    * @return MarketVolumeStruct[]
    */
    public static  MarketVolumeStruct[] getExampleMarketVolumeStructAllMultiParties()
    {
        int x;

        MarketVolumeStruct [] marketVolumeStructList = getExampleMarketVolumeStructList();

        for(x=0; x<4; x++)
        {
            marketVolumeStructList[x].multipleParties = true;
        }

        return marketVolumeStructList;

    }

}
