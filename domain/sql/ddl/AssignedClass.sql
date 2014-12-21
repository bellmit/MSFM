whenever sqlerror continue

drop table asgn_class;

rem whenever sqlerror exit failure

create table asgn_class
(
	databaseIdentifier	number(20) not null,
	user_key			number(20) not null,
	asgn_class_key		number(20) not null,
    assignment_type     varchar2(10),
    session_name        varchar2(30) 
)
/* tablespace SBTG_SM_DATA01 */
;

alter table asgn_class
add constraint asgn_class_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table asgn_class
add constraint asgn_class_COF_u1
unique (user_key, session_name, asgn_class_key);
/* using index tablespace sbtg_sm_indx01 */
;

/* using index tablespace sbtg_sm_indx01 */
create index asgn_class_i2 on asgn_class(asgn_class_key, user_key);
