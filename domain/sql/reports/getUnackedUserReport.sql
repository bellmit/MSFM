
set linesize 140;
set pagesize 10000;
column rptType   format a8;
column eventDesc format a15;

select
    to_char(convert_java_time(a.dateTime), 'mm/dd/yyyy HH24:mi:ss') time,
    decode(a.reportType,
            1, 'orderRpt',
            2, 'quoteRpt',
            a.reportType) rptType,
    decode(a.eventType,
            1,   'new order',
            2,   'order fill',
            3,   'order cxl',
            4,   'order bust fill',
            5,   'order reinst',
            7,   'order update',
            8,   'order booked',
            -13, 'order exception',
            102, 'quote fill',
            103, 'quote cxl',
            107, 'quote bust',
            a.eventType) eventDesc,
    a.userId userId,
    a.transSeqNum transSeqNum,
    a.highCboeId cboeHigh,
    a.lowCboeId cboeLow,
    a.executingOrGiveupFirm exFirm,
    a.executingOrGiveupFirmExchange exExch,
    a.branch branch,
    a.branchSequenceNumber brSeq,
    a.correspondentFirm corr,
    a.orderDate ordrDate
from
    sbt_user_report a
where
    a.userId not in ('XXH', 'XXS')
    and not exists (select '' from sbt_user_report_ack b
        where a.userid = b.userid and a.reportkey = b.ackreportkey)
order by a.dateTime, a.userId;

