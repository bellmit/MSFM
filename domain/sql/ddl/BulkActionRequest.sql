whenever sqlerror continue

drop table bulk_action_request;

whenever sqlerror exit failure

create table bulk_action_request
(
	databaseidentifier	number(20)	not null,
	actionrequesttype	varchar2(32)	not null,
	userid			varchar2(15)	not null,
	transactionid		varchar2(64)	not null,
	servername		varchar2(64)	not null,
	optionaltext		varchar2(1024),
	requesttime		number(20),
	creat_rec_time          timestamp default systimestamp
)
/* tablespace sbtb_me_data03 */
;

alter table bulk_action_request add constraint bulk_action_request_pk primary key (databaseidentifier);

CREATE INDEX bulk_action_request_i1 ON
bulk_action_request(CREAT_REC_TIME);

