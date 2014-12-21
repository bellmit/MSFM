whenever sqlerror continue

drop table auction_order_relat;

whenever sqlerror exit failure

create table auction_order_relat
(
	databaseIdentifier	    number(20) not null,
	auction_dbid            number(20) not null,
	order_dbid              number(20) not null,
	type                    number(3) not null,
	creat_rec_time			timestamp default systimestamp
)
;

alter table auction_order_relat
add constraint auction_order_relat_pk
primary key (databaseIdentifier)
;

CREATE INDEX auction_order_relat_i1 ON
auction_order_relat(CREAT_REC_TIME);

