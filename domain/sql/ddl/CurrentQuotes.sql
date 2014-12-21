whenever sqlerror continue

drop table cur_quote;

whenever sqlerror exit failure

create table cur_quote
(
	databaseIdentifier	number(20) not null,
    session_name        varchar2(30) not null,
	prod_key		    number(20) not null,
	best_rest		    number(20),
	cont_cur_mkt	    number(20),
	non_cont_cur_mkt	number(20),
	nbbo			    number(20)
);

alter table cur_quote
add constraint cur_quote_pk
primary key (databaseIdentifier);

alter table cur_quote
add constraint cur_quote_u1
unique (session_name, prod_key);

