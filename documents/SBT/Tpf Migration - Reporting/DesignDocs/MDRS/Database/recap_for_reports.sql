create table recap_for_reports 
(
	databaseIdentifier	number(20) 	not null,
	session_name		varchar2(30) 	not null,
	class_key		number(20)	not null,
	product_key		number(20)      not null,
	prod_type_code          number(2)       ,
    	opt_type_code           varchar2(4)	,
	number_Of_Trades	number(20),
    	open_interest		number(10),
	underlying_price	varchar2(12),
    	yesterdays_close_price  varchar2(12),
	last_sale_price		varchar2(12),
	last_sale_vol		number(10),
	last_sale_price_vol	number(10),
	total_vol		number(10),
	trade_time		number(16),
	tick_dir		char(1),
	tick_amt		varchar2(12),
	net_chg                 varchar2(12),
	net_chg_dir             char(1),
	bid_price               varchar2(12),
	bid_size                number(10),
	bid_time                number(16),
	bid_dir                 char(1),
	ask_price               varchar2(12),
	ask_size                number(10),
	ask_time                number(16),
	otc_ind                 char(1),
	recap_prefix		varchar2(20),
	high_price		varchar2(12),
	high_price_vol  	number(10),
	low_price		varchar2(12),
	low_price_vol   	number(10),
	open_price		varchar2(12),
	open_price_vol  	number(10),
	close_price		varchar2(12),
	lowPriceTime;		number(20),
	highPriceTime;		number(20),
	openingPriceTime;	number(20),
	has_been_traded_ind	char(1)
);
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       


