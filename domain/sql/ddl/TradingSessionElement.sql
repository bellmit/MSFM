whenever sqlerror continue

drop table trading_session_element;

whenever sqlerror exit failure

create table trading_session_element
(
	databaseIdentifier number(20) not null,
	session_key number(20) not null,
	template_key number(20) not null,
	element_name varchar2(400),
	element_state number(2),
	seq_nbr number(10),
	bus_day number(20),
	prod_preopen_time number(20),
	prod_open_time number(20),
	prod_close_time number(20),
	auto_preopen_prod_ind char(1),
	auto_open_prod_ind char(1),
	auto_close_prod_ind char(1),
	prod_earlyclose_time number(20),
	auto_earlyclose_prod_ind char(1),
	extensions      varchar2(128)
)
/* tablespace sbtg_sm_data01 */
;

alter table trading_session_element
add constraint trading_session_element_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;
