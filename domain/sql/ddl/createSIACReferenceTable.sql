whenever sqlerror continue

drop table siac_reference_nbr;

whenever sqlerror exit failure

create table siac_reference_nbr
(
	databaseIdentifier number(20) not null,
	tradeID number(20),
	time number(20),
	product	number(20),
	refNumber number(20),
	price number(20),
	quantity number(20),
	originalReference number(20),
	settlementDate number(20)
)
;
alter table siac_reference_nbr
add constraint siac_reference_nbr_pk
primary key (databaseIdentifier)
;
