whenever sqlerror continue

drop table pref;

whenever sqlerror exit failure

create table pref
(
	databaseIdentifier	number(20) not null,
	create_time		number(16) not null,
	lmod_time		number(16) not null,
	user_key		number(20) not null,
	pref_type		number(2) not null,
	pref_name		varchar2(3000) not null,
	pref_value		varchar2(4000)
)
/* tablespace sbtg_me_data01 */
;

alter table pref
add constraint pref_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_me_indx01 */
;

alter table pref
add constraint pref_u1
unique (user_key, pref_type, pref_name)
/* using index tablespace sbtg_me_indx01 */
;
