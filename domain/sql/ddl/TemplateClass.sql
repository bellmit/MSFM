whenever sqlerror continue

drop table template_class;

whenever sqlerror exit

create table template_class (
    databaseIdentifier  number(20) not null,
    prod_class_key      number(20) not null,
    template_key        number(20) not null,
    all_products_ind    char(1) not null,
    undly_sess_name     varchar2(30),
    latest_expr_date    number(20),
    low_exer_price      varchar2(12),
    high_exer_price     varchar2(12)
)
/* tablespace sbtg_sm_data01 */
;

alter table template_class add constraint template_class_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

create index TEMPLATE_CLASS_i1 on TEMPLATE_CLASS(PROD_CLASS_KEY);

whenever sqlerror continue
