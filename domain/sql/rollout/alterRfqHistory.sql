whenever sqlerror continue 
 
whenever sqlerror exit failure 

ALTER TABLE rfq_history ADD 
(
    CREAT_REC_TIME    TIMESTAMP DEFAULT SYSTIMESTAMP
);

CREATE INDEX rfq_history_i1 ON
rfq_history(CREAT_REC_TIME);

