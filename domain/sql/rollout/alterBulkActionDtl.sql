whenever sqlerror continue 
 
whenever sqlerror exit failure 

ALTER TABLE bulk_action_dtl ADD 
(
    CREAT_REC_TIME    TIMESTAMP DEFAULT SYSTIMESTAMP
);

CREATE INDEX bulk_action_dtl_i1 on
bulk_action_dtl(CREAT_REC_TIME);

