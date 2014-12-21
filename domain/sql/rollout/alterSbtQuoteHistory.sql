whenever sqlerror continue 
 
whenever sqlerror exit failure 

ALTER TABLE sbtquotehistory ADD 
(TRADE_SERVER_ID NUMBER(1)
);
