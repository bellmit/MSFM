whenever sqlerror continue

drop table user_firm_relation;

whenever sqlerror exit failure

create table user_firm_relation
(
	databaseIdentifier	number(20) not null,
	user_key			number(20) not null,
	firm_key			number(20) not null
)
/* tablespace sbtg_sm_data01 */
;

alter table user_firm_relation
add constraint user_firm_relation_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table user_firm_relation
add constraint user_firm_relation_u1
unique (user_key, firm_key)
/* using index tablespace sbtg_sm_indx01 */
;
