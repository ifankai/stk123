drop materialized view STK_SEARCH_MVIEW;

create materialized view STK_SEARCH_MVIEW
refresh complete on demand
 next sysdate + 2/24
as
select * from stk_earning_search_view
union all
select code,name,market,f9,null as hot,null as industry,null as main_industry,null as my_industry,null as fn_date,null as roe,
null as close_change_10,null as close_change_20,null as close_change_30,null as close_change_60,null as close_change_120,
null as volume_avg_5,null as pe,null as pb,null as ps,null as market_cap,null as revenue_growth_rate,
null as gross_profit_margin,null as Sale_Profit_Margin,null as net_profit_growth_rate,null as debt_rate,
null as research_rate,null as pe_ntile,null as pb_ntile,null as ps_ntile,null as ntile,null as listing_days,
null as net_profit_er_0331,null as net_profit_er_0630,null as net_profit_er_0930,null as last_net_profit,
null as last_net_profit_fn_date,null as net_profit_max,null as net_profit_max_flag,null as revenue_max_flag,
null as last_net_profit_one_quarter,null as net_profit_last_year,null as Capital_reserve_per_share,
null as Undistributed_profit_per_share,null as cash_net_profit_rate,null as pe_yoy,
null as er_date,null as er_low,null as er_high,null as last_amount,null as er_pe,null as er_net_profit_max_flag,
null as peg,null as forecast_pe_this_year,null as forecast_pe_next_year,
null as holder 
from stk_hk
;


select * from STK_SEARCH_MVIEW where market=1 order by market, code;

call dbms_refresh.refresh('STK_SEARCH_MVIEW');

create table stk_search_mview(
  code varchar2(10),
  name varchar2(40),
  market number(1),
  f9 clob,
  industry varchar2(2000),
  main_industry varchar2(200),
  my_industry varchar2(40),
  fn_date varchar2(10),
  roe number(12,2),
  close_change_10 number(12,2),
  close_change_20 number(12,2),
  close_change_30 number(12,2),
  close_change_60 number(12,2),
  close_change_120 number(12,2),
  volume_avg_5 number(12,2),
  pe number(12,2),
  pb number(12,2),
  ps number(12,2),
  market_cap number(12,2),
  revenue_growth_rate number(12,2),
  gross_profit_margin number(12,2),
  Sale_Profit_Margin number(12,2),
  net_profit_growth_rate number(12,2),
  debt_rate number(12,2),
  research_rate number(12,2),
  pe_ntile number(4),
  pb_ntile number(4),
  ps_ntile number(4),
  ntile number(4),
  listing_days number(8),
  net_profit_er_0331 number(14),
  net_profit_er_0630 number(14),
  net_profit_er_0930 number(14),
  last_net_profit number(14),
  last_net_profit_fn_date varchar2(10),
  net_profit_max number(12,2),
  net_profit_max_flag number(2),
  revenue_max_flag number(2),
  last_net_profit_one_quarter number(12,2),
  net_profit_last_year number(14),
  cash_net_profit_rate number(12,2),
  pe_yoy number(12,2),
  er_date varchar2(10),
  er_low number(12,2),
  er_high number(12,2),
  last_amount number(12,2),
  er_pe number(12,2),
  er_net_profit_max_flag number(2),
  peg number(12,2),
  forecast_pe_this_year number(12,2),
  forecast_pe_next_year number(12,2)
);
