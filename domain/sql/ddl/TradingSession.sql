whenever sqlerror continue

drop table trading_session;

whenever sqlerror exit failure

create table trading_session
(
	databaseIdentifier	    number(20) not null,
	session_name		    varchar2(30) not null,
	exchange_acronym	varchar2(5),
	bus_day                 number(20),
	session_state           number(2),
	session_dest_code       number(2),
	end_session_strategy	varchar2(256) not null,
	auto_end_session_ind   char(1) not null,
	abort_end_session_ind   char(1) not null,
	cur_end_session_event   number(2),
	last_completed_event    number(2),
	start_time              number(20),
	end_time                number(20),
	default_undly_session   varchar2(30),
	last_session_ind		char(1) not null,
	seq_nbr     	    	number(10) not null
)
/* tablespace sbtg_sm_data01 */
;


alter table trading_session
add constraint trading_session_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table trading_session
add constraint trading_session_u1
unique (session_name);
