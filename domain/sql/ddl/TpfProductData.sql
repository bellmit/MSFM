whenever sqlerror continue

drop table tpf_product_data;

whenever sqlerror exit failure

create table tpf_product_data
(
    databaseIdentifier  number(20) not null,
    tpf_ordinal         number(20),
    product_key         number(20),
    product_type        number(2),
    class_key           number(20),
	rep_class_key       number(20),
    rep_class_sym       varchar(10),
    hash_id             varchar(16)
);

alter table tpf_product_data
add constraint tpf_product_data_pk
primary key (databaseIdentifier);

