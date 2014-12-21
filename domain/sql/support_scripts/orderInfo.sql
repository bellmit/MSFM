set verify off

accept testName prompt 'Test name: '

accept branch prompt 'Branch: '

accept branchSequence prompt 'Branch Seq#: '

column order_dbid format 999999999999999

select o.databaseIdentifier order_dbid,
       to_char(convert_java_time(o.receivedtime), 'HH24:MI:SS.') || lpad(mod(o.receivedtime, 1000), 3, '0')  received_time,
       h.eventtime - o.receivedtime queue_time
from cme_orders_&testName o, cme_order_hist_&testName h
where o.databaseIdentifier = h.orderdbid
and o.branch = '&branch'
and o.branchSequenceNumber = &branchSequence
and h.eventtype = 1;

column reportkey format a25

select r.reportkey,
       r.eventtype,
       to_char(convert_java_time(r.datetime), 'HH24:MI:SS.') || lpad(mod(r.datetime, 1000), 3, '0') status_time,
       a.datetime - r.datetime elapsed_ack_time
from cme_reports_&testName r, cme_report_acks_&testName a
where r.branch = '&branch'
and r.branchSequenceNumber = &branchSequence
and a.ackReportKey = r.reportKey;
