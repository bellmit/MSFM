whenever sqlerror continue

drop table trading_session_template;

whenever sqlerror exit failure

create table trading_session_template
(
	databaseIdentifier number(20) not null,
	active_ind char(1),
	session_key number(20) not null,
	template_name varchar2(256) not null,
	seq_nbr number(10),
	prod_preopen_time number(20),
	prod_open_time number(20),
	prod_close_time number(20),
	auto_preopen_prod_ind char(1) not null,
	auto_open_prod_ind char(1) not null,
	auto_close_prod_ind char(1) not null,
	prod_earlyclose_time number(20),
	auto_earlyclose_prod_ind char(1) not null,
	extensions      varchar2(128)
)
/* tablespace sbtg_sm_data01 */
;

alter table trading_session_template
add constraint trading_session_template_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;
