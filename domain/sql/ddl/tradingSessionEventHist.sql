whenever sqlerror continue

drop table tradingSessionEventHist;

whenever sqlerror exit failure

create table tradingSessionEventHist
(
	databaseIdentifier number(20) not null,
	dateTimeMillis number(20),
	sessionName varchar2(256),
	eventType number(2),
	eventState number(2),
	contextString varchar2(128),
    eventGroup varchar2(32)
)
/* tablespace sbtg_sm_data01 */
;

alter table tradingSessionEventHist
add constraint tradingSessionEventHist_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;
