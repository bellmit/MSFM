whenever sqlerror continue 
 
whenever sqlerror exit failure 

ALTER TABLE order_exchange_market ADD 
(
    CREAT_REC_TIME    TIMESTAMP DEFAULT SYSTIMESTAMP
);

CREATE INDEX order_exchange_market_i1 ON
order_exchange_market(CREAT_REC_TIME);

