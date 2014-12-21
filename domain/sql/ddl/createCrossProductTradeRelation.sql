whenever sqlerror continue

drop table CROSSPRODUCTTRADERELATION;

whenever sqlerror exit failure

create table CROSSPRODUCTTRADERELATION
(
    databaseIdentifier number(20) not null, 
    localtradereportid number(20) not null,   
    remotetradereportid number(20) not null,
    remoteproductkey number(20) not null,
    remotetradetime number(20) not null,
    remotetradequantity number(10) not null,
    remotesessionname varchar(30) not null
);

create unique index CROSSPRODUCTTRADERELATION_PK on CROSSPRODUCTTRADERELATION (databaseIdentifier);

alter table CROSSPRODUCTTRADERELATION add primary key (databaseIdentifier) using index CROSSPRODUCTTRADERELATION_PK;

