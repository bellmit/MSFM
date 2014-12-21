whenever sqlerror continue

drop table order_book_price_item;

whenever sqlerror exit failure

create table order_book_price_item
(
	databaseIdentifier	    number(20) not null,
    item_price              varchar2(20) not null,
    total_qty               number(10),
    limit_only_qty          number(10),
    timed_order_cnt         number(10),
    aon_order_cnt           number(10),
    limit_order_cnt         number(10),
	mkt_turn_dtl   	        number(20),
	first_price_dtl 	    number(20),
    last_price_dtl          number(20),
    next_item               number(20),
    non_q_pri_qty           number(10),
    non_q_non_pri_qty       number(10),
    non_q_pri_order_cnt     number(10),
    non_q_non_pri_order_cnt number(10),
    q_pri_order_cnt         number(10),
    q_pri_qty               number(10)
)
/* tablespace sbtb_sm_data01 */
;

alter table order_book_price_item
add constraint order_book_price_item_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_sm_indx01 */
;

whenever sqlerror continue
