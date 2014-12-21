whenever sqlerror continue

drop table comm_sequencers;

whenever sqlerror exit failure

create table comm_sequencers
(
 DATABASEIDENTIFIER   NUMBER(20) NOT NULL,
 sequencerName        varchar2(1024),
 sendSequenceNbr      number(10),
 receiveSequenceNbr   number(10),
 lastSentMessageLength number(10),
 lastSentMessage       LONG RAW
)
/* tablespace sbtg_sm_data01 */
;

alter table comm_sequencers add constraint comm_sequencers_pk
primary key (databaseIdentifier)
/* using index sbtg_sm_indx01 */
;

alter table comm_sequencers add constraint comm_sequencers_u1
unique (sequencerName)
/* using index sbtg_sm_indx01 */
;

