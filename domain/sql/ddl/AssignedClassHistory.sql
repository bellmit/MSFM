whenever sqlerror continue

drop table asgn_class_history;

whenever sqlerror exit failure

create table asgn_class_history
(
    databaseIdentifier    number(20) not null,
    action_type           char(1) not null,
    ent_uid               number(10) not null,
	user_acr              varchar2(10) not null,
	user_exch             varchar2(8) not null,
    asgn_class_key        number(20) not null,
    date_time             number(16) not null,
    assignment_type       varchar2(10),
    session_name          varchar2(30) 
)
/* tablespace SBTG_SM_DATA01 */
;

alter table asgn_class_history
add constraint asgn_class_history_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

