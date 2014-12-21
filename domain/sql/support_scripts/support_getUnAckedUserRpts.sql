
set linesize 140;
set pagesize 10000;
column rptType   format a8;
column eventDesc format a15;

break on userid skip 1

select a.userId, count(*) num_unacked_ok, decode(a.eventType,
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
            a.eventType) eventDesc
from sbt_user_report a
where
    a.userId not in ('XXH', 'XXS')
    and not exists (select '' from sbt_user_report_ack b
    where a.userid = b.userid and a.reportkey = b.ackreportkey)
group by a.userId, a.eventType;

