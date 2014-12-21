
/**
 * Title:        SBT System Admin GUI Project<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Nick DePasquale<p>
 * Company:      Chicago Board Options Exchange<p>
 * @author Nick DePasquale
 * @version 1.0
 */

package com.cboe.presentation.marketData;

import junit.framework.*;

import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.presentation.exampleStructs.ExampleMarketVolumeStruct;

import com.cboe.interfaces.presentation.common.formatters.MarketVolumeFormatStrategy;

import com.cboe.presentation.common.formatters.FormatFactory;

public class TestMarketVolume extends TestCase
{
    private MarketVolumeStruct[] marketVolumeStructListLimitOrdersOnly;
    private MarketVolumeStruct[] marketVolumeStructList;
    private MarketVolumeStruct[] marketVolumeStructListMultiPartyIOC;
    private MarketVolumeStruct[] marketVolumeStructListMultiPartyAON;
    private MarketVolumeStruct[] marketVolumeStructListAllMultiParties;
    private MarketVolumeStruct[] marketVolumeStructListLimitIOC;
    private MarketVolumeStruct[] marketVolumeStructListListLimitOrdersIOCAON;
    private MarketVolumeStruct[] marketVolumeStructListListLimitOrdersIOCFOK;

    private final String MarketVolumeStructListLimitOrdersOnlyName = "LimitOrdersOnly";
    private final String MarketVolumeStructListName = "All";
    private final String MarketVolumeStructListMultiPartyIOCName = "MultiPartyIOC";
    private final String MarketVolumeStructListMultiPartyAONName = "MultiPartyAON";
    private final String MarketVolumeStructListAllMultiPartiesName = "AllMultiParties";
    private final String MarketVolumeStructListLimitIOCName = "LimitIOC";
    private final String MarketVolumeStructListListLimitOrdersIOCAONName = "LimitIOCAON";
    private final String MarketVolumeStructListListLimitOrdersIOCFOKName = "LimitIOCFOK";


    public TestMarketVolume(String name)
    {
        super(name);
    }

    protected void setUp()
    {

        marketVolumeStructListLimitOrdersOnly = ExampleMarketVolumeStruct.getExampleMarketVolumeStructListLimitOrdersOnly();
        marketVolumeStructList = ExampleMarketVolumeStruct.getExampleMarketVolumeStructList();

        marketVolumeStructListMultiPartyIOC = ExampleMarketVolumeStruct.getExampleMarketVolumeStructListMultiPartyIOC();
        marketVolumeStructListMultiPartyAON = ExampleMarketVolumeStruct.getExampleMarketVolumeStructListMultiPartyAON();
        marketVolumeStructListAllMultiParties = ExampleMarketVolumeStruct.getExampleMarketVolumeStructAllMultiParties();
        marketVolumeStructListLimitIOC = ExampleMarketVolumeStruct.getExampleMarketVolumeStructListLimitIOC();
        marketVolumeStructListListLimitOrdersIOCAON = ExampleMarketVolumeStruct.getExampleMarketVolumeStructListLimitOrdersIOCAON();
        marketVolumeStructListListLimitOrdersIOCFOK = ExampleMarketVolumeStruct.getExampleMarketVolumeStructListLimitOrdersIOCFOK();


    }

    public static Test suite()
    {
        return new TestSuite(TestMarketVolume.class);
    }

    public void testMarketVolumeStructHelper_ContainsVolumeContingency()
    {

        assertTrue(MarketVolumeStructListLimitOrdersOnlyName, MarketVolumeStructHelper.containsVolumeContingency(marketVolumeStructListLimitOrdersOnly)==false);
        assertTrue(MarketVolumeStructListName ,MarketVolumeStructHelper.containsVolumeContingency(marketVolumeStructList)==true);
        assertTrue(MarketVolumeStructListMultiPartyIOCName ,MarketVolumeStructHelper.containsVolumeContingency(marketVolumeStructListMultiPartyIOC)==true);
        assertTrue(MarketVolumeStructListMultiPartyAONName ,MarketVolumeStructHelper.containsVolumeContingency(marketVolumeStructListMultiPartyAON)==true);
        assertTrue(MarketVolumeStructListAllMultiPartiesName ,MarketVolumeStructHelper.containsVolumeContingency(marketVolumeStructListAllMultiParties)==true);
        assertTrue(MarketVolumeStructListLimitIOCName ,MarketVolumeStructHelper.containsVolumeContingency(marketVolumeStructListLimitIOC)==false);
    }

    public void testMarketVolumeStructHelper_GetQuantity()
    {

        assertEquals(MarketVolumeStructListLimitOrdersOnlyName,"10",MarketVolumeStructHelper.getQuantity(marketVolumeStructListLimitOrdersOnly));
        assertEquals(MarketVolumeStructListName,"24",MarketVolumeStructHelper.getQuantity(marketVolumeStructList));

        assertEquals(MarketVolumeStructListMultiPartyIOCName,"24",MarketVolumeStructHelper.getQuantity(marketVolumeStructListMultiPartyIOC));
        assertEquals(MarketVolumeStructListMultiPartyAONName,"24",MarketVolumeStructHelper.getQuantity(marketVolumeStructListMultiPartyAON));
        assertEquals(MarketVolumeStructListAllMultiPartiesName,"24",MarketVolumeStructHelper.getQuantity(marketVolumeStructListAllMultiParties));
        assertEquals(MarketVolumeStructListLimitIOCName,"24",MarketVolumeStructHelper.getQuantity(marketVolumeStructListLimitIOC));


    }

    public void testMarketVolumeStructHelper_GetVolumeContingencyQuantity()
    {

        assertEquals(MarketVolumeStructListLimitOrdersOnlyName,"0",MarketVolumeStructHelper.getVolumeContingencyQuantity(marketVolumeStructListLimitOrdersOnly));
        assertEquals(MarketVolumeStructListName,"19",MarketVolumeStructHelper.getVolumeContingencyQuantity(marketVolumeStructList));

        assertEquals(MarketVolumeStructListMultiPartyIOCName,"19",MarketVolumeStructHelper.getVolumeContingencyQuantity(marketVolumeStructListMultiPartyIOC));
        assertEquals(MarketVolumeStructListMultiPartyAONName,"19",MarketVolumeStructHelper.getVolumeContingencyQuantity(marketVolumeStructListMultiPartyAON));
        assertEquals(MarketVolumeStructListAllMultiPartiesName,"19",MarketVolumeStructHelper.getVolumeContingencyQuantity(marketVolumeStructListAllMultiParties));
        assertEquals(MarketVolumeStructListLimitIOCName,"0",MarketVolumeStructHelper.getVolumeContingencyQuantity(marketVolumeStructListLimitIOC));



    }

    public void testMarketVolumeStructHelper_GetVolumeContingencyIndicator()
    {

        assertEquals(MarketVolumeStructListLimitOrdersOnlyName ,"",MarketVolumeStructHelper.getVolumeContingencyIndicator(marketVolumeStructListLimitOrdersOnly));
        assertEquals(MarketVolumeStructListName ,"*",MarketVolumeStructHelper.getVolumeContingencyIndicator(marketVolumeStructList));

        assertEquals(MarketVolumeStructListMultiPartyIOCName ,"*",MarketVolumeStructHelper.getVolumeContingencyIndicator(marketVolumeStructListMultiPartyIOC));
        assertEquals(MarketVolumeStructListMultiPartyAONName ,"*",MarketVolumeStructHelper.getVolumeContingencyIndicator(marketVolumeStructListMultiPartyAON));
        assertEquals(MarketVolumeStructListAllMultiPartiesName ,"*",MarketVolumeStructHelper.getVolumeContingencyIndicator(marketVolumeStructListAllMultiParties));
        assertEquals(MarketVolumeStructListLimitIOCName ,"",MarketVolumeStructHelper.getVolumeContingencyIndicator(marketVolumeStructListLimitIOC));
    }

    public void testMarketVolumeStructHelper_IsVolumeContingenyMultipleParty()
    {

        assertTrue(MarketVolumeStructListLimitOrdersOnlyName ,MarketVolumeStructHelper.isVolumeContingencyMultipleParty(marketVolumeStructListLimitOrdersOnly)==false);
        assertTrue(MarketVolumeStructListName ,MarketVolumeStructHelper.isVolumeContingencyMultipleParty(marketVolumeStructList)==true);

        assertTrue(MarketVolumeStructListMultiPartyIOCName ,MarketVolumeStructHelper.isVolumeContingencyMultipleParty(marketVolumeStructListMultiPartyIOC)==true);
        assertTrue(MarketVolumeStructListMultiPartyAONName ,MarketVolumeStructHelper.isVolumeContingencyMultipleParty(marketVolumeStructListMultiPartyAON)==true);
        assertTrue(MarketVolumeStructListAllMultiPartiesName ,MarketVolumeStructHelper.isVolumeContingencyMultipleParty(marketVolumeStructListAllMultiParties)==true);
        assertTrue(MarketVolumeStructListLimitIOCName ,MarketVolumeStructHelper.isVolumeContingencyMultipleParty(marketVolumeStructListLimitIOC)==false);
/*
marketVolumeStructListLimitOrdersOnly
marketVolumeStructList

    marketVolumeStructListMultiPartyIOC;
    marketVolumeStructListMultiPartyAON;
    marketVolumeStructListAllMultiParties;
    marketVolumeStructListLimitIOC;

*/

    }

    public void testMarketVolumeFormatStrategy_Format_REAL_VOLUME_NAME()
    {
        MarketVolumeFormatStrategy  aMarketVolumeFormatStrategy = FormatFactory.getMarketVolumeFormatStrategy();
        String style = MarketVolumeFormatStrategy.REAL_VOLUME_NAME;

        assertEquals(MarketVolumeStructListLimitOrdersOnlyName ,"10",aMarketVolumeFormatStrategy.format(marketVolumeStructListLimitOrdersOnly,style));
        assertEquals(MarketVolumeStructListName ,"24",aMarketVolumeFormatStrategy.format(marketVolumeStructList, style));
        assertEquals(MarketVolumeStructListMultiPartyIOCName ,"24",aMarketVolumeFormatStrategy.format(marketVolumeStructListMultiPartyIOC, style));
        assertEquals(MarketVolumeStructListMultiPartyAONName ,"24",aMarketVolumeFormatStrategy.format(marketVolumeStructListMultiPartyAON, style));
        assertEquals(MarketVolumeStructListAllMultiPartiesName ,"24",aMarketVolumeFormatStrategy.format(marketVolumeStructListAllMultiParties, style));
        assertEquals(MarketVolumeStructListLimitIOCName ,"24",aMarketVolumeFormatStrategy.format(marketVolumeStructListLimitIOC, style));

        assertEquals(MarketVolumeStructListListLimitOrdersIOCAONName ,"24",aMarketVolumeFormatStrategy.format(marketVolumeStructListListLimitOrdersIOCAON, style));
        assertEquals(MarketVolumeStructListListLimitOrdersIOCFOKName ,"24",aMarketVolumeFormatStrategy.format(marketVolumeStructListListLimitOrdersIOCFOK, style));

    }

    public void testMarketVolumeFormatStrategy_Format_REAL_VOLUME_PLUS_NAME()
    {
        MarketVolumeFormatStrategy  aMarketVolumeFormatStrategy = FormatFactory.getMarketVolumeFormatStrategy();
        String style = MarketVolumeFormatStrategy.REAL_VOLUME_PLUS_NAME;

        assertEquals(MarketVolumeStructListLimitOrdersOnlyName ,"10",aMarketVolumeFormatStrategy.format(marketVolumeStructListLimitOrdersOnly, style));
        assertEquals(MarketVolumeStructListName ,"24+",aMarketVolumeFormatStrategy.format(marketVolumeStructList, style));
        assertEquals(MarketVolumeStructListMultiPartyIOCName ,"24+",aMarketVolumeFormatStrategy.format(marketVolumeStructListMultiPartyIOC, style));
        assertEquals(MarketVolumeStructListMultiPartyAONName ,"24+",aMarketVolumeFormatStrategy.format(marketVolumeStructListMultiPartyAON, style));
        assertEquals(MarketVolumeStructListAllMultiPartiesName ,"24+",aMarketVolumeFormatStrategy.format(marketVolumeStructListAllMultiParties, style));
        assertEquals(MarketVolumeStructListLimitIOCName ,"24",aMarketVolumeFormatStrategy.format(marketVolumeStructListLimitIOC, style));

        assertEquals(MarketVolumeStructListListLimitOrdersIOCAONName ,"24+",aMarketVolumeFormatStrategy.format(marketVolumeStructListListLimitOrdersIOCAON, style));
        assertEquals(MarketVolumeStructListListLimitOrdersIOCFOKName ,"24+",aMarketVolumeFormatStrategy.format(marketVolumeStructListListLimitOrdersIOCFOK, style));

    }


    public void testMarketVolumeFormatStrategy_Format_REAL_CONTINGENT_VOLUME_NAME()
    {
        MarketVolumeFormatStrategy  aMarketVolumeFormatStrategy = FormatFactory.getMarketVolumeFormatStrategy();
        String style = MarketVolumeFormatStrategy.REAL_CONTINGENT_VOLUME_NAME;

        assertEquals(MarketVolumeStructListLimitOrdersOnlyName ,"10",aMarketVolumeFormatStrategy.format(marketVolumeStructListLimitOrdersOnly, style));
        assertEquals(MarketVolumeStructListName ,"24+19*",aMarketVolumeFormatStrategy.format(marketVolumeStructList, style));
        assertEquals(MarketVolumeStructListMultiPartyIOCName ,"24+19*",aMarketVolumeFormatStrategy.format(marketVolumeStructListMultiPartyIOC, style));
        assertEquals(MarketVolumeStructListMultiPartyAONName ,"24+19*",aMarketVolumeFormatStrategy.format(marketVolumeStructListMultiPartyAON, style));
        assertEquals(MarketVolumeStructListAllMultiPartiesName ,"24+19*",aMarketVolumeFormatStrategy.format(marketVolumeStructListAllMultiParties, style));
        assertEquals(MarketVolumeStructListLimitIOCName ,"24",aMarketVolumeFormatStrategy.format(marketVolumeStructListLimitIOC, style));
        assertEquals(MarketVolumeStructListListLimitOrdersIOCAONName ,"24+12",aMarketVolumeFormatStrategy.format(marketVolumeStructListListLimitOrdersIOCAON, style));
        assertEquals(MarketVolumeStructListListLimitOrdersIOCFOKName ,"24+7",aMarketVolumeFormatStrategy.format(marketVolumeStructListListLimitOrdersIOCFOK, style));

        //getExampleMarketVolumeStructListLimitOrdersIOCAON()
    }
}
