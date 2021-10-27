
alter table stk_trade_strategy modify strategy_code varchar2(20);
drop index idx_trade_strategy_code;
update stk_pe set result_1=null,result_2=null where report_date<='20211001';

