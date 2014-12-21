whenever sqlerror continue

drop table mkt_hist_reports_archive;

whenever sqlerror exit failure

create table mkt_hist_reports_archive
(
    databaseIdentifier	number(20)	not nulL,
    session_name	varchar2(30)	not null,
    prod_key		number(20) 	not null,
    entry_type		number(2) 	not null,
    entry_time		number(16) 	not null,
    last_sale_price	varchar2(12),
    last_sale_vol	number(10),
    product_state	number(2),
    ticker_prefix	varchar2(4),
    dayofweek 		number(1) 	not null,
    CREAT_REC_TIME      timestamp default systimestamp
)
storage (freelists 12 freelist groups 4)
partition by range (dayofweek)
(partition mkt_hist_reports_archive_1 values less than (2) storage (freelists 12 freelist groups 4),
partition mkt_hist_reports_archive_2 values less than (3) storage (freelists 12 freelist groups 4),
partition mkt_hist_reports_archive_3 values less than (4) storage (freelists 12 freelist groups 4),
partition mkt_hist_reports_archive_4 values less than (5) storage (freelists 12 freelist groups 4),
partition mkt_hist_reports_archive_5 values less than (6) storage (freelists 12 freelist groups 4),
partition mkt_hist_reports_archive_6 values less than (7) storage (freelists 12 freelist groups 4),
partition mkt_hist_reports_archive_7 values less than (8) storage (freelists 12 freelist groups 4))
;

create index mkt_hist_reports_archive_pk
on mkt_hist_reports_archive(dayofweek,databaseIdentifier) local;
