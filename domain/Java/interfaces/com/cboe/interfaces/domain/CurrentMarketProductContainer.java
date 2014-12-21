/*
 * Interface : CurrentMarketProductContainer
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cboe.interfaces.domain;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;

/**
 * @author Vaziranc
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface CurrentMarketProductContainer 
{

        public CurrentMarketStruct getBestMarket();

        /**
         * @return
         */
        public CurrentMarketStruct getBestPublicMarketAtTop();

        /**
         * @param struct
         */
        public void setBestMarket(CurrentMarketStruct bestMarket);

        /**
         * @param struct
         */
        public void setBestPublicMarketAtTop(CurrentMarketStruct bestPublicMarket);

}
