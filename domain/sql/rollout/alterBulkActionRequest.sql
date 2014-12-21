whenever sqlerror continue 
 
whenever sqlerror exit failure 

ALTER TABLE  bulk_action_request ADD 
(
    CREAT_REC_TIME    TIMESTAMP DEFAULT SYSTIMESTAMP
);

CREATE INDEX bulk_action_request_i1 ON
bulk_action_request(CREAT_REC_TIME);

