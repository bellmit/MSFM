whenever sqlerror continue

drop table bulk_action_dtl;

whenever sqlerror exit failure

create table bulk_action_dtl
(
	databaseidentifier	number(20)	not null,
	requestdbid		number(20)	not null,
	targetdbid		number(20)	not null,
	resultcode		varchar2(1),
    creat_rec_time  timestamp default systimestamp,
    tradeid		    number(20)
)
/* tablespace sbtb_me_data03 */
;

alter table bulk_action_dtl add constraint bulk_action_dtl_pk primary key (databaseidentifier);
 
CREATE INDEX bulk_action_dtl_i1 on bulk_action_dtl(CREAT_REC_TIME);

