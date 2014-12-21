accept testName prompt 'Test Name: '

create table cme_orders_&testName tablespace SBTB_SM_INDX01 as select databaseIdentifier, userid,  branch, branchSequenceNumber, productkey, classkey, receivedTime from sbtorder;

create table cme_order_hist_&testName tablespace SBTB_SM_INDX01 as select orderdbid, eventtype, eventtime from sbtorderhistory;

create table cme_reports_&testName tablespace SBTB_SM_INDX01 as select reportkey, userid, branch, branchSequenceNumber, eventtype, datetime from sbt_user_report;

create table cme_report_acks_&testName tablespace SBTB_SM_INDX01 as select ackReportKey, datetime from sbt_user_report_ack;
