whenever sqlerror exit failure

ALTER TABLE sbtorder
 ADD (
         purgetime timestamp NULL
     );
