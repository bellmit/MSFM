whenever sqlerror continue 
 
whenever sqlerror exit failure 

ALTER TABLE mkt_data_hist ADD 
(TRADE_SERVER_ID NUMBER(1) 
);
