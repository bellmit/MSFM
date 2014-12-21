whenever sqlerror continue

drop table SBT_User_Report_Ack;

whenever sqlerror exit failure

create table SBT_User_Report_Ack
(
    databaseIdentifier      number(20) not null,
    userName                varchar2(20),
    userId		    varchar2(15),
    ackReportKey            varchar2(40),
    dateTime                number(16),
    transClockPoints        varchar2(512)
)
/* tablespace sbtb_sm_data01 */
;

alter table SBT_User_Report_Ack
add constraint SBT_User_Report_Ack_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_sm_indx01 */
;

create index SBT_User_Report_Ack_i1
on SBT_User_Report_Ack(ackReportKey);
