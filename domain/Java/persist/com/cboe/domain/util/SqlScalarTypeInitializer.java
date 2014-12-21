package com.cboe.domain.util;

public class SqlScalarTypeInitializer
{
    private static boolean initialized;

    public static void initTypes()
    {
        synchronized( SqlScalarTypeInitializer.class )
        {
            if( initialized ){ return; }

            Class[] types = { com.cboe.domain.bestQuote.MarketVolume.class,
                              com.cboe.domain.bestQuote.MarketVolumeHolder.class,
                              com.cboe.domain.bestQuote.ExchangeVolumeHolder.class,
                              com.cboe.domain.marketData.ExchangeAcronym.class,
                              com.cboe.domain.marketData.ExchangeAcronymHolder.class,
                              com.cboe.domain.marketData.ExchangeIndicator.class,
                              com.cboe.domain.marketData.ExchangeIndicatorHolder.class,
                              com.cboe.domain.util.ExchangeVolume.class,
                              com.cboe.domain.util.ExchangeVolumeHolder.class,
                              com.cboe.domain.product.OptionTypeImpl.class,
                              com.cboe.domain.util.ExpirationDateImpl.class,
                              com.cboe.domain.util.PriceSqlType.class,
                              com.cboe.domain.util.SideBaseImpl.class
                            };

            for( Class type: types )
            {
                try
                {
                    //Force the class to be loaded
                    Class.forName( type.getName() );
                } 
                catch( ClassNotFoundException ex )
                {
                    throw new RuntimeException( ex );
                }
            }
            
            initialized=true;
        }
    }

}
