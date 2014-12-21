whenever sqlerror continue

drop table TradedThroughOrder;

whenever sqlerror exit failure

create table TradedThroughOrder
(
	databaseIdentifier number(20) not null,
	alertId number(20) not null,
	orderId number(20) not null
)
;

alter table TradedThroughOrder
add constraint sbt_tradedthroughorder_pk
primary key (databaseIdentifier)
;

