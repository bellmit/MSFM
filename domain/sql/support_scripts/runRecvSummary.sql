select to_char(convert_java_time(rcv_time), 'HH24:MI:SS') RCV_TIME,
       total_recv_count,
       recv_rate
from (select (trunc(receivedtime / 60000) * 60000) rcv_time,
             count(*) total_recv_count,
             trunc(count(*) / 60.0, 2) recv_rate
      from sbtorder
      group by (trunc(receivedtime / 60000) * 60000))
/
