whenever sqlerror continue

drop table user_sess_authorization;

whenever sqlerror exit failure

create table user_sess_authorization
(
	databaseIdentifier	number(20) not null,
	user_key			number(20) not null,
	sess_name			varchar2(30) not null
)
/* tablespace sbtg_sm_data01 */
;

alter table user_sess_authorization
add constraint user_sess_authorization_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table user_sess_authorization
add constraint user_sess_authorization_u1
unique (user_key, sess_name)
/* using index tablespace sbtg_sm_indx01 */
;
