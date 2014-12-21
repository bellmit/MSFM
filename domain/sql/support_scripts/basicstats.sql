set echo off
set termout off
set heading off
set pagesize 0
set linesize 180
column eventtime format 999999999999999999;
column ORDERDBID format 999999999999999999;
column RECEIVEDTIME format 999999999999999999;

spool basicstats.out

/* Total Quotes */

select 'Total Quotes: ', count(eventtype)
from sbtquotehistory
where eventtype = 101
or eventtype = 106;

/* Total Orders */

select 'Total Orders: ', count(*) from sbtorder;  
/* Total Limit Orders */

select 'Total Limit Orders: ', count(*) from sbtorder
where contingencytype = 1;

/* Total New Orders */

select 'Total New Orders: ', count(*) from sbtorderhistory
where eventtype = 1;

/* Total Booked Orders */

select 'Total Booked Orders: ', count(*) from sbtorderhistory

where eventtype = 8;
                         
/* Total Order Fills */

select 'Total Fills: ', count(*) from sbtorderhistory
where eventtype = 2;

/* Total Quote Fills */

select 'Total Quote Fills: ', count(eventtype)
from sbtquotehistory
where eventtype = 102;

/* Total Trades */

select 'Total Trades: ', count(*) from sbt_tradereport;   

/* Total Immediate Filled Orders */

/*
select 'Total Immediate Filled Orders: ', count(*)
from sbtorderhistory n, sbtorderhistory f, sbtorder O
where n.orderdbid = f.orderdbid and n.eventtype = 1 and f.eventtype = 2
and O.databaseidentifier = n.orderdbid
and f.leavesquantity = 0
and O.contingencytype = 1
and not exists (select * from sbtorderhistory b where n.orderdbid = b.orderdbid
and b.eventtype = 8);
*/                  
