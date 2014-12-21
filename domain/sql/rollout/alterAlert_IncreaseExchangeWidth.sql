whenever sqlerror continue

whenever sqlerror exit failure

ALTER TABLE sbt_alert
   MODIFY (
   	     lastsaleExchange        varchar2(5)
       );