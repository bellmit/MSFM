whenever sqlerror continue

drop table profile;

whenever sqlerror exit failure

create table profile 
(
	databaseIdentifier	number(20) not null,
	user_key			number(20),
	class_key			number(20),
	account_relation_key	number(20),
	subAccount 			varchar2(16),
	membershipDefined   varchar2(2),
	sessionName  varchar2(30),
	accountBlanked  varchar2(1),
        originCode      varchar2(1)
)
/* tablespace sbtg_sm_data01; */
;

alter table profile 
add constraint profile_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table profile
add constraint profile_u1
unique (user_key, class_key, sessionName)
deferrable initially deferred
/* using index tablespace sbtg_sm_indx01 */
;
