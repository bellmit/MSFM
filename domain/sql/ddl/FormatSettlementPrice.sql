create or replace function formatSettlementPrice 
                (session_name in varchar2,
		sysdate in date,
                prod_type_code in int, 
                opt_type_code in varchar2, 
                class_sym in varchar2, 
                expr_date in varchar2,
                exer_price in varchar2,
                settlement_price in varchar2) return varchar2 as

	tempString varchar2(20);
	tempInt int;
	strikePriceInteger varchar2(5);
	strikePriceDecimal varchar2(4);
	settlePriceInteger varchar2(5);
	settlePriceDecimal varchar2(4);
	lineText varchar2(200);
begin
	lineText := '10';				 	 	 -- record id
	lineText := lineText || to_char(sysdate, 'YYYYMMDD');            -- processing date
	if session_name = 'ONE_MAIN' then
		lineText := lineText || '07';				-- exchange code 07 = one chicago 
	elsif session_name = 'CFE_MAIN' then
		lineText := lineText || '20';			 	-- exchange code 20 = CBOE futures exchange
	elsif session_name = 'COF_MAIN' then
		lineText := lineText || '20';			 	-- exchange code 20 = CFE Exchange for Options on Futures
	end if;
	if prod_type_code = 7 then
		tempString := 'X';
	elsif prod_type_code = 4 then
		tempString := 'F';
	elsif prod_type_code = 5 then
		tempString := 'O';                                      -- use 'O' for index (the underlying for COF)
	else 
		tempString := ' ';
	end if;
	lineText := lineText || tempString;				-- product type code
	lineText := lineText || '  ';					-- 2X filler space 
	if opt_type_code = 'CALL' then
		tempString := 'C';
	elsif opt_type_code = 'PUT' then
		tempString := 'P';
	else 
		tempString := ' ';
	end if;
	lineText := lineText || tempString; 				-- call/put code
	lineText := lineText || '  ';					-- 2X filler space 
	lineText := lineText || rpad(class_sym, 6);			-- trading symbol
	lineText := lineText || '  ';					-- 2X filler space 

	if session_name = 'COF_MAIN' and prod_type_code = 5 then
		lineText := lineText || '        ';  			-- pad 8 spaces for COF underlying expiration
	else
		lineText := lineText || to_char(to_date(expr_date, 'YYYYMMDD'), 'MM'); -- expiration month
		-- if session_name = 'COF_MAIN' then
		lineText := lineText || to_char(to_date(expr_date, 'YYYYMMDD'), 'DD'); -- expiration Day
		-- else
		-- lineText := lineText || '  ';					-- 2x filler space
		-- end if;
		lineText := lineText || to_char(to_date(expr_date, 'YYYYMMDD'), 'YYYY'); -- expiration year
	end if;

	lineText := lineText || '  ';					-- 2x filler space 
	if exer_price is not null  then 
		tempInt := instr(exer_price, '.');
		strikePriceInteger := substr(exer_price, 1, tempInt - 1);
		strikePriceDecimal := substr(exer_price, tempInt + 1);
	else 
		strikePriceInteger := ' ' ;
		strikePriceDecimal := ' ';
	end if;
	if session_name = 'COF_MAIN' and prod_type_code = 5 then
		lineText := lineText || '         ';				-- pad spaces for strike of COF underlying
	elsif session_name = 'COF_MAIN' then
		lineText := lineText || lpad(strikePriceInteger,5, '0');             -- strike price integer
		lineText := lineText || rpad(strikePriceDecimal,4, '0');             -- strike price decimal
	else
		lineText := lineText || lpad(strikePriceInteger,5);             -- strike price integer
		lineText := lineText || rpad(strikePriceDecimal,4);             -- strike price decimal
	end if;

	lineText := lineText || lpad(' ',10);				-- 10X filler space 
        if session_name = 'COF_MAIN' and prod_type_code = 5 then
                lineText := lineText || '         ';				-- pad spaces for mid price of COF underlying
	else
		lineText := lineText || lpad('0',9,'0');			-- mid day price, does not support
	end if;
	lineText := lineText || '    ';					-- 4x filler space 
        if settlement_price is null then
		settlePriceInteger := '00000';
		settlePriceDecimal := '0000';
        elsif settlement_price = 'NONE' then
		settlePriceInteger := '00000';
		settlePriceDecimal := '0000';
        else
		tempInt := instr(settlement_price, '.');
		settlePriceInteger := substr(settlement_price, 1, tempInt - 1);
		settlePriceDecimal := substr(settlement_price, tempInt + 1);
        end if;
	lineText := lineText || lpad(settlePriceInteger,5,'0');		-- settlement price integer
	lineText := lineText || rpad(settlePriceDecimal,4,'0');		-- settlement price decimal
	lineText := lineText || '    ';					-- 4X filler space 
	lineText := lineText || ' ';					-- settle on open indicator
	lineText := lineText || lpad(' ',118);				-- 118 filler space
	return lineText;
end;
/
