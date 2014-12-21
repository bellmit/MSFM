whenever sqlerror continue

drop table mkt_data;

whenever sqlerror exit failure

create table mkt_data
(
	databaseIdentifier	number(20) not null,
        session_name            varchar2(30) not null,
	prod_key		    number(20) not null,
	class_key		    number(20) not null,
	nbbo			    number(20),
	botr			    number(20),
	cont_cur_mkt		number(20),
	non_cont_cur_mkt	number(20),
	pub_cur_mkt	        number(20),
	recap			    number(20),
	exchanges_indicators   varchar2(200)
);

alter table mkt_data
add constraint mkt_data_pk
primary key (databaseIdentifier);

alter table mkt_data
add constraint mkt_data_u1
unique (session_name, prod_key);

create index mkt_data_i1
on mkt_data(session_name, class_key);

