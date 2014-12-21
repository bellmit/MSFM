whenever sqlerror continue

drop table RFQ_HISTORY;

whenever sqlerror exit failure

create table RFQ_HISTORY
(
	databaseIdentifier number(20) not null,
  /* prefix used by every record */
    userId varchar2(15),
    userKey number(20),
    productKey number(20),
    classKey number(20),
    eventType number(3),
    eventTime number(24),
	timeToLive number(24),
	quantity number(20),
	rfqType number(1),
    productState number(2),
    session_name varchar2(30),
    creat_rec_time timestamp default systimestamp
)
/* tablespace sbtb_sm_data01 */
;

alter table RFQ_HISTORY
add constraint RFQ_HISTORY_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_sm_indx01 */
;

CREATE INDEX rfq_history_i1 ON
rfq_history(CREAT_REC_TIME);


