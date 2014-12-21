whenever sqlerror continue

drop table clearing_acr;

whenever sqlerror exit failure

create table clearing_acr
(
	databaseIdentifier	number(20) not null,
	userKey			number(20),
	sessionName             varchar2(30),
	clearingAcr             varchar2(10)
)
/* tablespace sbtg_sm_data01; */
;

alter table clearing_acr
add constraint clearing_acr_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table clearing_acr
add constraint clearing_acr_u1
unique (userKey, sessionName)
deferrable initially deferred
/* using index tablespace sbtg_sm_indx01 */
;
