clear
break on userid skip 2 on report
compute sum of total_orders on userid
compute sum of active_orders on userid
compute count of prod_class_sym on userid
compute sum of total_orders on report
compute sum of active_orders on report


select os.userid, tc.prod_class_sym, tc.prod_type_code, os.total_orders, os.active_orders
from (select userid, classkey, count(*) total_orders, sum(least(1, originalQuantity - tradedQuantity - cancelledQuantity)) active_orders
from sbtorder
group by userid, classkey) os, trading_class tc
where os.classkey = tc.prod_class_key
order by 1, 4 desc, 2, 3;
