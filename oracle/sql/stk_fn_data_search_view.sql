create or replace view stk_fn_data_search_view as

select a.*,
max(net_profit_one_quarter) over (order by a.code,a.fn_date desc rows between 0 following and 7 following) net_profit_max,
max(revenue_one_quarter) over (order by a.code,a.fn_date desc rows between 0 following and 7 following) revenue_max,
sum(net_profit_one_quarter) over (order by a.code,a.fn_date desc rows between 3 following and 3 following) last_net_profit_one_quarter  --for earning
 from(             
select fn.code,fn.fn_date,                          
case 
  when substr(fn_date, -4)='0331' then "109"+sum("109") over (order by fn.code,fn.fn_date desc rows between 1 following and 1 following)-sum("109") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following)
  when substr(fn_date, -4)='0630' then "109"+sum("109") over (order by fn.code,fn.fn_date desc rows between 2 following and 2 following)-sum("109") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following)
  when substr(fn_date, -4)='0930' then "109"+sum("109") over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following)-sum("109") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following)
  when substr(fn_date, -4)='1231' then "109"
end as ROE,
round("300"/100000000,2) as revenue, 

case 
  when substr(fn_date, -4)='0331' then round("300"/100000000,2)
  else round(("300" - sum("300") over (order by fn.code,fn.fn_date desc rows between 1 following and 1 following))/100000000,2)
end as revenue_one_quarter,


--round((decode(sum("300") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following),0,0,"300"/sum("300") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following))-1)*100,2) 
round("110", 2) as Revenue_growth_rate,

round("106",2) as Gross_Profit_Margin, 
round("126",2) as Sale_Profit_Margin,
--"4001" as Profit_Margin,
round("124",2) as Operating_Margin,          
round("303"/100000000,2) as Net_profit,

case 
  when substr(fn_date, -4)='0331' then round("303"/100000000,2)
  else round(("303" - sum("303") over (order by fn.code,fn.fn_date desc rows between 1 following and 1 following))/100000000,2)
end as net_profit_one_quarter,

case 
  when substr(fn_date, -4)='0331' then sum("303") over (order by fn.code,fn.fn_date desc rows between 1 following and 1 following)-sum("303") over (order by fn.code,fn.fn_date desc rows between 2 following and 2 following)
  when substr(fn_date, -4)='0630' then sum("303") over (order by fn.code,fn.fn_date desc rows between 2 following and 2 following)-sum("303") over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following)
end as net_profit_er_0930,

case 
  when substr(fn_date, -4)='0331' then sum("303") over (order by fn.code,fn.fn_date desc rows between 1 following and 1 following)-sum("303") over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following)
  when substr(fn_date, -4)='1231' then "303"-sum("303") over (order by fn.code,fn.fn_date desc rows between 2 following and 2 following)
end as net_profit_er_0630,

case  
  when substr(fn_date, -4)='1231' then "303"-sum("303") over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following)
end as net_profit_er_0331,
  
sum("303") over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following) as last_net_profit,--for earning

sum(fn.fn_date) over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following) as last_net_profit_fn_date, --for earning


--case 
--  when "303"<0 and sum("303") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following) < 0 then -999  /*续亏*/
--  when "303">0 and sum("303") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following) < 0 then 999   /*扭亏*/
--  when "303"<0 and sum("303") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following) > 0 then -9999 /*转亏*/
--  else                         
--    round((decode(sum("303") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following),0,0,"303"/sum("303") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following))-1)*100,2)
--end 

round("111",2) as net_profit_Growth_rate,

case 
  when substr(fn_date, -4)='0331' then sum("303") over (order by fn.code,fn.fn_date desc rows between 1 following and 1 following)
  when substr(fn_date, -4)='0630' then sum("303") over (order by fn.code,fn.fn_date desc rows between 2 following and 2 following)
  when substr(fn_date, -4)='0930' then sum("303") over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following)
  when substr(fn_date, -4)='1231' then "303"
end as net_profit_last_year,

round("203"/100000000,2) as assets,                             
round("121",2) as debt_rate,
"206" as  Research_Development,
round(decode("300",0,0,"206"/"300")*100,2) as   Research_Rate,                        
"200" as "应收账款",                           
"204" as "应付帐款",                           
"202" as "库存",                           
--"2007" as "现金及现金等价物",                   
--"2008" as  "短期投资",                          
--"2009" as  "长期投资",                          
"400" as  "运营活动所产生现金",                
"401" as  "投资活动所产生现金",                
"402" as  "融资活动所产生现金",                
"403" as  "现金净增减额",                      
"118" as "速动比率",                           
"117" as "流动比率",                      
--"101" as "EPS",
case 
  when substr(fn_date, -4)='0331' then "101"+sum("101") over (order by fn.code,fn.fn_date desc rows between 1 following and 1 following)-sum("101") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following)
  when substr(fn_date, -4)='0630' then "101"+sum("101") over (order by fn.code,fn.fn_date desc rows between 2 following and 2 following)-sum("101") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following)
  when substr(fn_date, -4)='0930' then "101"+sum("101") over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following)-sum("101") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following)
  when substr(fn_date, -4)='1231' then "101"
end as "EPS",                   
--"4005" as "每股收入",                      
"102" as "每股价值",                      
--round(decode(EPS,0,0,k.close/EPS),2) as "PE", 
--round(decode("4005",0,0,k.close/"4005"),2) as "PS", 
--round(decode("102",0,0,k.close/"102"),2) as "PB", 
--"4018" as "PEG",
--k.close,
"104" as Capital_reserve_per_share,
"105" as Undistributed_profit_per_share,
"123"*100 as cash_net_profit_rate,

row_number() over(partition by fn.code order by fn.fn_date desc) rn                       
from (      
                                        
select * from (select type,fn_value,code,fn_date from stk_fn_data where fn_date >= to_char(sysdate-1000,'yyyyMMDD')
) pivot (sum(fn_value)                              
for type in (                                       
109, /*ROE(ttm)*/                                   
106, /*毛利率         */                              
126, /*销售净利率*/
300, /*营收总额     */   
110, /*主营业务收入增长率(%)*/                            
303, /*净利润                   */ 
111, /* 净利润增长率(%)*/                 
203, /*资产总额                */                    
121, /*债务率                */                    
206, /*研发费用                 */                  
200, /*应收账款总计(净额)*/                          
204, /*应付帐款                    */                
202, /*库存总额                 */                   
--2007, /*现金及现金等价物   */                         
--2008, /*短期投资              */                     
--2009, /*长期投资                */                   
400, /*运营活动所产生现金  */                       
401, /*投资活动所产生现金 */                        
402, /*融资活动所产生现金  */                       
403, /*现金净增减额  */                             
118, /*速动比率        */                           
117, /*流动比率(mrq) */                              
101, /*EPS(ttm)   */                                 
--4005, /*每股收入(ttm)  */                             
--4018, /*PEG    */  
--4001, /*净利润率(ttm)*/ 
124, /*营业利润率(ttm)*/                                   
102, /*帐面价值(mrq)*/  
104, /*每股资本公积金(元)*/  
105, /*每股未分配利润(元)*/         
123 /*经营现金净流量与净利润的比率(%)*/
))  
                                                
order by fn_date desc) fn   -- where code='000997'
) a where a.rn <= 10

