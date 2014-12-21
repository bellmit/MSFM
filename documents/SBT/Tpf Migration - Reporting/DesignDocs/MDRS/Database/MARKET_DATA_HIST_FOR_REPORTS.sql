create table market_data_hist_for_reports
(
	databaseIdentifier		number(20)	not null,
      	session_name        		varchar2(30)    not null,
	prod_key			number(20) 	not null,
	entry_type			number(2) 	not null,
	entry_time			number(16) 	not null,
	last_sale_price			varchar2(12),
	last_sale_vol			number(10),
	undly_last_sale_price		varchar2(12),
	product_state			number(2),
	ticker_prefix			varchar2(4),
       	dayofweek 			number(1) 	not null,    
       	CREAT_REC_TIME timestamp default systimestamp    
);

