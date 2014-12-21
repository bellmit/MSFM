whenever sqlerror exit failure

create or replace function formatSubAccount(originType in char, account in varchar2, subAccount in varchar2)
return varchar2 as
    result varchar2(20);
begin
    if originType = 'M' then
        result := account;
    else
        result := subAccount;
    end if;
    return result;
end;
/

create or replace function formatOptionalData(originType in char, subAccount in varchar2, branch in varchar2, branchSeqNumber in number, correspondentFirm in varchar2, optionalData in varchar2)
return varchar2 as
    result varchar2(20);
begin
    if originType = 'M' then
        -- 5 leading spaces + subAccount padded to 6 + 5 trailing spaces
        result := '     ' || rpad(nvl(subAccount, ' '), 6) || '     ';
    else
        -- branch + sequenceNumber + constant + correspondentFirm + optionalData
        result := rpad(substr(branch, 1, 3) ,3) ||
                  lpad(to_char(branchSeqNumber), 4, '0') || ' 1' || 
                  rpad(substr(nvl(correspondentFirm, ' '), 1, 3), 3) ||
                  rpad(substr(nvl(optionalData, ' '), 1, 4), 4);
    end if;
    return result;
end;
/


create or replace view sbt_tradereportentry_v
as
select  databaseIdentifier,
        matchedSequenceNumber,
        active,
        entry_type,
        entry_time,
        last_entry_type,
        last_update_time,
        buyerUserKey,
        buyer,
        buy_broker_exch,
        sellerUserKey,
        seller,
        sell_broker_exch,
        buyFirm,
        buy_firm_exch,
        buy_firm_branch,
        buy_firm_branch_seq_no,
        buyOrderId,
        buyQuoteId,
        buyReinstatable,
        buyerOriginType,
        buyerCmta,
        buy_cmta_exch,
        buyerPositionEffect,
        buy_acct,
        formatSubAccount(buyerOriginType, buy_acct, buyerSubAccount) buyerSubAccount,
        formatOptionalData(buyerOriginType, buyerSubAccount, buy_firm_branch, buy_firm_branch_seq_no, buy_corr_id, buyerOptionalData) buyerOptionalData,
	buy_auct_trade_ind,
        sellFirm,
        sell_firm_exch,
        sell_firm_branch,
        sell_firm_branch_seq_no,
        sellOrderId,
        sellQuoteId,
        sellReinstatable,
        sellerOriginType,
        sellerCmta,
        sell_cmta_exch,
        sellerPositionEffect,
        sell_acct,
        formatSubAccount(sellerOriginType, sell_acct, sellerSubAccount) sellerSubAccount,
        formatOptionalData(sellerOriginType, sellerSubAccount, sell_firm_branch, sell_firm_branch_seq_no, sell_corr_id, sellerOptionalData) sellerOptionalData,
	sell_auct_trade_ind,
        quantity,
        tradeReportForEntry,
        session_name,
        buy_corr_id,
        buy_originator,
        buy_originator_exch,
        sell_corr_id,
        sell_originator,
        sell_originator_exch,
	buy_bill_type_code,
        sell_bill_type_code,
        round_lot_qty,
        buyer_clear_type,
        seller_clear_type,
        extensions,
        buy_away_exch_text,
        sell_away_exch_text
from sbt_tradereportentry;

grant select on sbt_tradereportentry_v to public;
