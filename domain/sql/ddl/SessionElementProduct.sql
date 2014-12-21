whenever sqlerror continue

drop table session_element_product;

whenever sqlerror exit

create table session_element_product (
    databaseIdentifier  number(20) not null,
    prod_key            number(20) not null,
    session_class_key   number(20) not null,
    prod_state          number(2)
)
/* tablespace sbtg_me_data01 */
;

alter table session_element_product add constraint session_element_product_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_me_indx01 */
;

whenever sqlerror continue
