whenever sqlerror continue

drop table trans_fee;

whenever sqlerror exit failure

rem The format of this table was chosen to match a back office table
rem that we were planning to snapshot to our system.  Later it was
rem decided not to do the shapshot.

create table trans_fee (
 TRANS_FEE_CODE                            CHAR(2) NOT NULL,
 TRANS_FEE_DESC                            VARCHAR2(50) NOT NULL,
 LMOD_USERNAME                             VARCHAR2(30) NOT NULL,
 LMOD_DATE                                 DATE NOT NULL
);

alter table trans_fee
add constraint trans_fee_pk
primary key (TRANS_FEE_CODE);

insert into trans_fee
values ('00', 'Equity', 'initial load', sysdate);

insert into trans_fee
values ('31', 'BONDS', 'initial load', sysdate);

insert into trans_fee
values ('51', 'OTC (Over the Counter Stocks)', 'initial load', sysdate);

insert into trans_fee
values ('61', 'S/P 100 INDEX', 'initial load', sysdate);

insert into trans_fee
values ('62', 'S/P 500 INDEX', 'initial load', sysdate);

insert into trans_fee
values ('63', 'Dow Jones Industrial', 'initial load', sysdate);

insert into trans_fee
values ('64', 'Dow Jones Transportation', 'initial load', sysdate);

insert into trans_fee
values ('65', 'Dow Jones Utilities', 'initial load', sysdate);

insert into trans_fee
values ('66', 'S/P 100 Index Reduced Value', 'initial load', sysdate);

insert into trans_fee
values ('71', 'SECTORS', 'initial load', sysdate);

insert into trans_fee
values ('81', 'INTEREST RATES', 'initial load', sysdate);

insert into trans_fee
values ('91', 'Narrow Based Index', 'initial load', sysdate);

commit;
