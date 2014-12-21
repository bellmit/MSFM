whenever sqlerror continue

drop table tradingSessionEventHistDetail;

whenever sqlerror exit failure

create table tradingSessionEventHistDetail
(
	databaseIdentifier number(20) not null,
	historyKey number(20) not null,
	clientName varchar2(256),
	eventTime number(20),
	eventState number(2),
	transactionSeqNumber number(20)
)
/* tablespace sbtg_sm_data01 */
;

alter table tradingSessionEventHistDetail
add constraint trdSessEvtHistDetail_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;
