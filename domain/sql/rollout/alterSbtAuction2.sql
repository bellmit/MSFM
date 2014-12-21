whenever sqlerror continue 
 
whenever sqlerror exit failure 

ALTER TABLE SBTAUCTION 
 ADD (AUCTIONSTARTINGREASON 	NUMBER(2) 
);
