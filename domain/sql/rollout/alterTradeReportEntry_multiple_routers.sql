whenever sqlerror continue
whenever sqlerror exit failure
ALTER TABLE sbt_tradereportentry
ADD( 
    outboundVendor VARCHAR2(10)
   );
