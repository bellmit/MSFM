select to_char(convert_java_time(rcv_time), 'HH24:MI:SS') sample_time, avg_queue_time, max_queue_time, min_queue_time, nbr_processed
from ( select trunc(o.receivedtime / 60000) * 60000 rcv_time,
              avg(h.eventtime - o.receivedtime) avg_queue_time,
              max(h.eventtime - o.receivedtime) max_queue_time,
              min(h.eventtime - o.receivedtime) min_queue_time,
              count(*) nbr_processed
	from sbtorder o, sbtorderhistory h
	where o.databaseIdentifier = h.orderdbid
	and h.eventtype = 1
	group by trunc(o.receivedtime / 60000) * 60000)
order by 1
/
