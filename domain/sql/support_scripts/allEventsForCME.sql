select to_char(convert_java_time(receive_time), 'HH24:MI') receive_time,
       eventtype, event_count, avg_ack_time, min_ack_time, max_ack_time
from (select receive_time, eventtype, count(*) event_count,
             avg(end_time - start_time) avg_ack_time,
             min(end_time - start_time) min_ack_time,
             max(end_time - start_time) max_ack_time
      from (select r.reportkey,
                   r.eventtype,
                   trunc(r.datetime / 60000) * 60000 receive_time,
                   r.datetime start_time,
                   min(a.datetime) end_time
            from sbt_user_report r, sbt_user_report_ack a
            where r.reportkey = a.ackreportkey
            and r.userid like 'CME%'
            group by r.reportkey, r.eventtype, trunc(r.datetime / 60000) * 60000, r.datetime)
      group by receive_time, eventtype)
/
