whenever sqlerror continue

drop table sbt_user_enablement;

whenever sqlerror exit failure

create table sbt_user_enablement
(
	databaseIdentifier	number(20) not null,
	user_id             varchar2(15) not null,
	user_key            number(20),
	session_name        varchar2(30),
	product_type        number(2),
	testClassesOnly     char(1)
)
/* tablespace SBTG_SM_DATA01; */
;

alter table sbt_user_enablement
add constraint sbt_user_enablement_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;
