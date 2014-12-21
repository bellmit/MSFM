whenever sqlerror continue

drop table trade_report_ack;

whenever sqlerror exit failure

create table trade_report_ack
(
    ack_databaseIdentifier  number(20) not null,
    atomic_trade_id         number(20) not null,
    matched_seqno           number(20) not null,
    entry_type              char(1),
    ack_ind                 char(1),
    error_flags             number(20) not null
);

alter table trade_report_ack
add constraint trade_report_ack_pk
primary key (ack_databaseIdentifier)
;

create index trade_report_ack_i1
on trade_report_ack(atomic_trade_id);
