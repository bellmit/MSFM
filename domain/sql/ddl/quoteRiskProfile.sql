whenever sqlerror continue

drop table user_quote_risk_profiles;

whenever sqlerror exit failure

create table user_quote_risk_profiles
(
 databaseIdentifier  NUMBER(20) not null,
 sbt_user            NUMBER(20),
 classKey            NUMBER(20),
 volumeThreshold     NUMBER(12),
 timeWindow          NUMBER(12),
 enabled             VARCHAR2(2)
)
/* tablespace sbtg_sm_data01 */
;

alter table user_quote_risk_profiles
add constraint user_quote_risk_profiles_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

