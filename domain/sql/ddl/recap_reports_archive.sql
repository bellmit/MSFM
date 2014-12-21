whenever sqlerror continue

drop table recap_reports_archive;

whenever sqlerror exit failure

create table recap_reports_archive
(
    databaseIdentifier	number(20)   not null,
    session_name	varchar2(30) not null,
    class_key		number(20)   not null,
    product_key		number(20)   not null,
    prod_type_code  number(2),
    opt_type_code   varchar2(4),
    number_Of_Trades number(20),
    open_interest	number(10),
    underlying_price varchar2(12),
    last_sale_price	varchar2(12),
    last_sale_vol	number(10),
    last_sale_price_vol	number(10),
    total_vol		number(10),
    trade_time		number(16),
    tick_dir		char(1),
    tick_amt		varchar2(12),
    net_chg         varchar2(12),
    net_chg_dir     char(1),
    bid_price       varchar2(12),
    bid_size        number(10),
    bid_time        number(16),
    bid_dir         char(1),
    ask_price       varchar2(12),
    ask_size        number(10),
    ask_time        number(16),
    otc_ind         char(1),
    recap_prefix	varchar2(20),
    high_price		varchar2(12),
    high_price_vol  number(10),
    low_price		varchar2(12),
    low_price_vol   number(10),
    open_price		varchar2(12),
    open_price_vol  number(10),
    close_price		varchar2(12),
    lowPriceTime	number(20),
    highPriceTime	number(20),
    openingPriceTime	number(20),
    underlying_category	number(2),
    has_been_traded_ind	char(1),
    dayofweek           number(1)   not null
)
storage (freelists 12 freelist groups 4)
partition by range (dayofweek)
(partition recap_reports_archive_1 values less than (2) storage (freelists 12 freelist groups 4),
partition recap_reports_archive_2 values less than (3) storage (freelists 12 freelist groups 4),
partition recap_reports_archive_3 values less than (4) storage (freelists 12 freelist groups 4),
partition recap_reports_archive_4 values less than (5) storage (freelists 12 freelist groups 4),
partition recap_reports_archive_5 values less than (6) storage (freelists 12 freelist groups 4),
partition recap_reports_archive_6 values less than (7) storage (freelists 12 freelist groups 4),
partition recap_reports_archive_7 values less than (8) storage (freelists 12 freelist groups 4))
;

create index recap_reports_archive_pk
on recap_reports_archive(dayofweek,databaseIdentifier) local;
