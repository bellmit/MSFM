whenever sqlerror continue

whenever sqlerror exit failure

ALTER TABLE sbt_alert
   ADD (
   	     orsId                   varchar2(6)
       );