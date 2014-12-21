select to_char(convert_java_time(rcv_time), 'HH24:MI:SS') RCV_TIME,
       eventtype,
       event_cnt,
       event_rate
from (select (trunc(eventtime / 60000) * 60000) rcv_time,
             eventtype,
             count(*) event_cnt,
             trunc(count(*) / 60.0, 2) event_rate
      from sbtquotehistory
      group by (trunc(eventtime / 60000) * 60000), eventtype)
/
