whenever sqlerror continue
whenever sqlerror exit failure

ALTER TABLE sbt_tradereportentry
ADD(
       sell_side_ind VARCHAR2(1)
       );
