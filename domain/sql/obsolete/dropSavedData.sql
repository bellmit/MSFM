accept testName prompt 'Test Name: '

drop table cme_orders_&testName;

drop table cme_order_hist_&testName;

drop table cme_reports_&testName;

drop table cme_report_acks_&testName;
