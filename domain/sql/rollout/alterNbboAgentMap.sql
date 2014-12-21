whenever sqlerror continue 
 
whenever sqlerror exit failure 

ALTER TABLE nbboagentmap ADD 
(
    CREAT_REC_TIME    TIMESTAMP DEFAULT SYSTIMESTAMP
);

CREATE INDEX nbboagentmap_i2 ON
nbboagentmap(CREAT_REC_TIME);
