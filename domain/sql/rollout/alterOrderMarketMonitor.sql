whenever sqlerror continue 
 
whenever sqlerror exit failure 

ALTER TABLE order_market_monitor ADD 
(
    CREAT_REC_TIME    TIMESTAMP DEFAULT SYSTIMESTAMP
);

CREATE INDEX order_market_monitor_i2 ON
order_market_monitor(CREAT_REC_TIME);

