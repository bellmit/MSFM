whenever sqlerror continue

ALTER TABLE sbt_alert_exchange_market add ( 
       time                        number(24),
    	usedForTradeThrough         char(1)
); 
