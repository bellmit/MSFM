whenever sqlerror continue

drop table prod_type;

rem whenever sqlerror exit failure

create table prod_type
(
	databaseIdentifier	number(20) not null,
	prod_type_code		number(2) not null,
	type_name		varchar2(30) not null,
	type_desc		varchar2(80) not null,
	create_time		number(16) not null,
	lmod_time		number(16) not null
)
/* tablespace sbtg_sm_data01 */
;

alter table prod_type
add constraint prod_type_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

