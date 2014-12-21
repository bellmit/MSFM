whenever sqlerror continue

drop table mkt_data_summary;

whenever sqlerror exit failure

create table mkt_data_summary
(
	databaseIdentifier	number(20) not null,
	underlying_price		varchar2(12),
	open_interest		number(10),
    session_name        varchar2(30) not null,
	prod_key		    number(20) not null,
	class_key		    number(20) not null,
	prod_type_code		number(2) not null,
	recap			    number(20)
);

alter table mkt_data_summary
add constraint rmkt_data_summary_pk
primary key (databaseIdentifier);

