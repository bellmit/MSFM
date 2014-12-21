select to_char(convert_java_time(start_time), 'HH24:MI:SS') enter_time,
	branch, branchsequencenumber, ack_time
from (
select r.reportkey, r.branch, r.branchsequenceNumber, r.datetime start_time,
	   min(a.datetime) -  r.datetime ack_time
    from sbt_user_report r, sbt_user_report_ack a
    where r.reportkey = a.ackreportkey
    and r.userid = 'CME8'
    group by r.reportkey, r.datetime, r.branch, r.branchsequenceNumber
having min(a.datetime) - r.datetime > 10000)
/
