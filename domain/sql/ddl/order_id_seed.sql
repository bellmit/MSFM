whenever sqlerror continue

drop table order_id_seed;

whenever sqlerror exit failure

create table order_id_seed
(
   databaseidentifier number(20) not null,
   used_branch varchar2(3)
);

alter table order_id_seed
add constraint order_id_seed_pk
primary key (databaseidentifier);
