whenever sqlerror continue

drop table user_firm_affiliation;

whenever sqlerror exit failure


create table user_firm_affiliation
(
	databaseIdentifier	number(20) not null,
	user_acronym		varchar2(10),
	exchange_acronym        varchar2(5),
	affiliated_firm         varchar2(6)
)
;

alter table user_firm_affiliation
add constraint user_firm_affiliation_pk
primary key (databaseIdentifier)
;

alter table user_firm_affiliation
add constraint user_firm_affiliation_unique
unique (user_acronym,exchange_acronym)
;
