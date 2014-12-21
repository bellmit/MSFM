select classkey, count(*) total_orders, sum(least(1, originalQuantity - tradedQuantity - cancelledQuantity)) active_orders
from sbtorder
group by classkey;
