whenever sqlerror continue

drop table best_quote;

whenever sqlerror exit failure

create table best_quote
(
	databaseIdentifier	number(20) not null,
	quote_time		number(16),
	bid_price		varchar2(10),
	bid_size		number(7),
	bid_exch		varchar2(5),
	ask_price		varchar2(10),
	ask_size		number(7),
	ask_exch		varchar2(5)
);

alter table best_quote
add constraint best_quote_pk
primary key (databaseIdentifier);

