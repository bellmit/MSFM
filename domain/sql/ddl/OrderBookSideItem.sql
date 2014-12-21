whenever sqlerror continue

drop table order_book_side_item;

whenever sqlerror exit failure

create table order_book_side_item
(
	databaseIdentifier	    number(20) not null,
    item_side               varchar2(4) not null,
	mkt_price_item   	    number(20),
	first_limit_price_item 	number(20),
    best_limit_price_item   number(20),
    best_non_q_price_item   number(20),
    best_q_price_item       number(20)
)
/* tablespace sbtb_sm_data01 */
;

alter table order_book_side_item
add constraint order_book_side_item_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_sm_indx01 */
;

whenever sqlerror continue
