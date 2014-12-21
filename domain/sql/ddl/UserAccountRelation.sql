whenever sqlerror continue

drop table user_account_relation;

whenever sqlerror exit failure

create table user_account_relation
(
	databaseIdentifier	number(20) not null,
	lastModifiedTime	number(16) not null,
	user_key 			number(20) not null,
	account_key			number(20) not null,
	active				char(1)
)
/* tablespace sbtg_sm_data01 */
;

alter table user_account_relation
add constraint user_account_relation_pk1
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table user_account_relation
add constraint user_account_relation_u1
unique (user_key, account_key)
/* using index tablespace sbtg_sm_indx01 */
deferrable initially deferred 
;
/* using index tablespace sbtg_sm_indx01 */
create index user_account_relation_i2 on user_account_relation(account_key);
