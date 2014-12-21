whenever sqlerror continue 
 
whenever sqlerror exit failure 

ALTER TABLE sbt_tradereport ADD
(
    CLASSKEY  NUMBER(20)
);

CREATE INDEX sbt_tradereport_i3 ON
sbt_tradereport(TIME, CLASSKEY, PRODUCT);

