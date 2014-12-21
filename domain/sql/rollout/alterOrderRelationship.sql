whenever sqlerror continue 
 
whenever sqlerror exit failure 

ALTER TABLE OrderRelationship 
 ADD (
      CREAT_REC_TIME            TIMESTAMP DEFAULT SYSTIMESTAMP
);
