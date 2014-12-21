whenever sqlerror continue

drop table SBT_User_Report_User;

whenever sqlerror exit failure

create table SBT_User_Report_User
(
    databaseIdentifier      number(20) not null,
    userName                varchar2(20),
    userId 		    varchar2(15),
    reportType              number(2),
    dateTime                number(16),
    classKey                number(10)
)
/* tablespace sbtb_sm_data01 */
;

alter table SBT_User_Report_User
add constraint SBT_User_Report_User_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_sm_indx01 */
;
create index sbt_user_report_user_i1 on sbt_user_report_user(userId, reportType, classKey);

