whenever sqlerror continue

whenever sqlerror exit failure 

ALTER TABLE bulk_action_dtl ADD 
(
    TRADEID NUMBER(20) 
);

ALTER TABLE bulk_action_dtl MODIFY
(
    TARGETDBID NUMBER(20) NULL
);

