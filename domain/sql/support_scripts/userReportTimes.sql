select to_char(convert_java_time(receive_time), 'HH24:MI') receive_time,
       event_count, avg_wait_time
from (select receive_time, count(*) event_count, avg(end_time - start_time) avg_wait_time
      from (select r.reportkey,
                   trunc(r.datetime / 60000) * 60000 receive_time,
                   r.datetime start_time,
                   min(a.datetime) end_time
            from sbt_user_report r, sbt_user_report_ack a
            where r.reportkey = a.ackreportkey
            group by r.reportkey, trunc(r.datetime / 60000) * 60000, r.datetime)
      group by receive_time);
