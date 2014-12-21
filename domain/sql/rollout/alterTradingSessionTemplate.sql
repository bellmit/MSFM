whenever sqlerror continue
whenever sqlerror exit failure
ALTER table trading_session_template ADD
(
	prod_earlyclose_time number(20),
	auto_earlyclose_prod_ind char(1),
	extensions	varchar2(128)
);
