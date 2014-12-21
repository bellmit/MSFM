whenever sqlerror continue

drop table LOGIN_USERID;

whenever sqlerror exit failure

create table LOGIN_USERID
(
    databaseIdentifier number(20) not null,
    userId             varchar2(15) not null,
    active             char(1),
    user_key           number(20) not null
)
; 
/* tablespace SBTG_SM_DATA01; */

alter table LOGIN_USERID
add constraint LOGIN_USERID_pk
primary key (databaseIdentifier)
; 
/* using index tablespace sbtg_sm_indx01 */


