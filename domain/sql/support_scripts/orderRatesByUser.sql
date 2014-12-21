set linesize 120

select to_char(convert_java_time(rcv_time), 'HH24:MI:SS') RCV_TIME,
       userid,
       trunc((order_count - replace_count) / 60.0, 2) new_order_rate,
       trunc(cancel_count / 60.0, 2) cancel_rate,
       trunc(replace_count / 60.0, 2) replace_rate,
       trunc(fill_count / 60.0, 2) fill_rate
from (select (trunc(eventtime / 60000) * 60000) rcv_time,
             userid,
             count(decode(eventtype, 1, 1)) order_count,
             count(decode(eventtype, 3, 1)) cancel_count,
             count(decode(eventtype, 6, 1)) replace_count,
             sum(decode(eventtype, 2, 1)) fill_count
      from sbtorderhistory
      group by (trunc(eventtime / 60000) * 60000), userid)
/
