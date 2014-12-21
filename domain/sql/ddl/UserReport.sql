whenever sqlerror continue

drop table SBT_User_Report_Ack;
drop table SBT_User_Report;

whenever sqlerror exit failure

create table SBT_User_Report
(
    databaseIdentifier      number(20) not null,
    reportKey               varchar2(40) not null,
    userId                  varchar2(15),
    firmKey                 varchar2(6),
    firmExchange            varchar2(5),
    reportType              number(2),
    eventType               number(5),
    transSeqNum             number(10),
    productKey              number(10),
    data                    long raw,
    dateTime                number(16),
    executingOrGiveUpFirm   varchar2(5),
    executingOrGiveUpFirmExchange varchar2(5),
    branch                  varchar2(5),
    branchSequenceNumber    number(10),
    correspondentFirm       varchar2(5),
    orderDate               varchar2(8),
    highCboeId              number(10),
    lowCboeId               number(10),
    classKey                number(10)
)
/* tablespace sbtb_me_data03 */
;

alter table SBT_User_Report
add constraint SBT_User_Report_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_me_indx03 */
;

alter table SBT_User_Report
add constraint SBT_User_Report_u1
unique (reportKey)
/* using index tablespace sbtb_me_indx03 */
;


