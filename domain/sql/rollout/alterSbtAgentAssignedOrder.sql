whenever sqlerror continue 
 
whenever sqlerror exit failure 

ALTER TABLE sbtagentassignedorder ADD 
(
    CREAT_REC_TIME    TIMESTAMP DEFAULT SYSTIMESTAMP
);

CREATE INDEX sbtAgentAssignedOrder_i4 ON 
sbtAgentAssignedOrder(CREAT_REC_TIME);


