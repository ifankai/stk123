
alter table stk_trade_strategy modify strategy_code varchar2(20);
drop index idx_trade_strategy_code;
create index idx_trade_strategy_date on stk_trade_strategy (trade_date);
update stk_pe set result_1=null,result_2=null,result_3=null,result_4=null,result_5=null,result_6=null,result_7=null,result_8=null 
where report_date<='20211001';

