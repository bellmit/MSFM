whenever sqlerror continue

drop table order_book_price_dtl;

whenever sqlerror exit failure

create table order_book_price_dtl
(
	databaseIdentifier	    number(20) not null,
    tradable_key            number(20) not null,
    tradable_type           number(1) not null,
    quote_user_id           varchar2(15),
    next_dtl                number(20)
)
/* tablespace sbtb_sm_data01 */
; 

alter table order_book_price_dtl
add constraint order_book_price_dtl_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_sm_indx01 */
;

whenever sqlerror continue
