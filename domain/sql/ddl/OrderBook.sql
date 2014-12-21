whenever sqlerror continue

drop table order_book;

whenever sqlerror exit failure

create table order_book
(
	databaseIdentifier	number(20) not null,
	prod_key			number(20) not null,
	buy_side    		number(20),
	sell_side    		number(20)
)
/* tablespace sbtb_sm_data01; */
;

alter table order_book
add constraint order_book_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_sm_indx01; */
;

whenever sqlerror continue
