set pagesize 200;
set linesize 180;
column productkey format 9999999999999999999999;
column classkey format 9999999999999999999999;

loginDb W_MAIN << EOF
set pagesize 200;
set linesize 180;
column productkey format 9999999999999999999999;
column classkey format 9999999999999999999999;
spool $RUN_DIR/log/ProductMigrationDatabaseQueries_`date |cut -f5 -d' '`.out
select count(*), state from sbtorder where timeinforce = 'D' group by state;
select count(*), state from sbtorder where timeinforce = 'G' group by state;

select count(*), classkey from sbtorder where timeinforce = 'D' group by classkey;
select count(*), classkey from sbtorder where timeinforce = 'G' group by classkey;

select count(*), location from sbtorder group by location;
select count(*), location from sbtroutedmessage where processedflag = 'N' group by location;

select EXECUTINGORGIVEUPFIRMEXCH as exch, EXECUTINGORGIVEUPFIRM as firm, BRANCH, BRANCHSEQUENCENUMBER as seq,
CORRESPONDENTFIRM as cfirm, orderdate, timeinforce as tif, state, originalquantity as origq, cancelledquantity as cxlq,
substr(location, 0, 32) as loc from sbtorder where location = 'HOMT1' order by classkey, timeinforce;

select EXECUTINGORGIVEUPFIRMEXCH as exch, EXECUTINGORGIVEUPFIRM as firm, BRANCH, BRANCHSEQUENCENUMBER as seq,
CORRESPONDENTFIRM as cfirm, orderdate, timeinforce as tif, state, originalquantity as origq, cancelledquantity as cxlq,
substr(location, 0, 32) as loc from sbtorder where location = 'HOMT4' order by classkey, timeinforce;

select ab.branch, ab.branchsequencenumber as seq, substr(ab.location, 0, 32) as ord_loc, cd.messagetype as msgtype,
cd.processedflag as procesed, substr(cd.location, 0, 32) as rmsg_loc from sbtorder ab, sbtroutedmessage cd where
ab.databaseidentifier = cd.orderdbid and ab.location = 'HOMT1' order by ab.classkey, ab.timeinforce;

select ab.branch, ab.branchsequencenumber as seq, substr(ab.location, 0, 32) as ord_loc, cd.messagetype as msgtype,
cd.processedflag as procesed, substr(cd.location, 0, 32) as rmsg_loc from sbtorder ab, sbtroutedmessage cd where
ab.databaseidentifier = cd.orderdbid and ab.location = 'HOMT4' order by ab.classkey, ab.timeinforce;

select BRANCH,BRANCHSEQUENCENUMBER as seq,STATE,CLASSKEY,TIMEINFORCE,substr(LOCATION,0,32) as LOCATION from sbtorder order by classkey, TIMEINFORCE;

select count(*), classkey from sbtorderhistory group by classkey;
select count(*), classkey from sbtorderhistory where timeinforce = 'G' group by classkey;
select count(*), classkey from sbtorderhistory where timeinforce = 'D' group by classkey;
exit
EOF
