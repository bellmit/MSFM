whenever sqlerror continue

drop table tradingSessionRegistrationMap;

whenever sqlerror exit failure

create table tradingSessionRegistrationMap
(
	databaseIdentifier number(20) not null,
	sessionName varchar2(30),
	clientName varchar2(256),
	pcsGroupType number(20),
	eventHistoryDetail number(20),
	lmod_time number(20),
	create_time number(20),
	details varchar2(1024)
)
/* tablespace sbtg_sm_data01 */
;

alter table tradingSessionRegistrationMap
add constraint tradingSessRegMap_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table tradingSessionRegistrationMap
add constraint tradingSessRegMap_u1
unique (sessionName, clientName)
/* using index tablespace sbtg_sm_indx01 */
;
