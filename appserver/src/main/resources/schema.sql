alter table stk_report_detail add output_down_longtime varchar2(2000);

create table stk_trade_strategy(
  id number(8),
  code varchar2(10),
  trade_date varchar2(8),
  strategy_code varchar2(10),  
  insert_time date  
);
create sequence s_trade_strategy_id INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE CACHE 10;
alter table stk_trade_strategy add constraint pk_trade_strategy_id primary key (id);
create index idx_trade_strategy_code on stk_trade_strategy (code,trade_date);

