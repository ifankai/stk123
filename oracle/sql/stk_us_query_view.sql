create or replace view stk_us_query_view as
with todayk as                                       
 (select t.close,t.code,t.kline_date                      
 from (select s.*,ROW_NUMBER() over(PARTITION by code order by kline_date desc) as num                  
 from stk_kline_us s where code=p_view_param.get_param('code') ) t where t.num = 1)     

select fn.code,fn.fn_date, "4004" as "ROE","1001" as "营收总额",
 round((decode(sum("1001") over (order by fn_date desc rows between 4 following and 4 following),0,0,"1001"/sum("1001") over (order by fn_date desc rows between 4 following and 4 following))-1)*100,2)||'%' as "营收增长率",     
 nvl("4002",round(decode("1001",0,0,"1002"/"1001")*100,2))||'%' as "毛利率",            
 "1008" as "净利润",                           
 round((decode(sum("1008") over (order by fn_date desc rows between 4 following and 4 following),0,0,"1008"/sum("1008") over (order by fn_date desc rows between 4 following and 4 following))-1)*100,2)||'%' as "净利润增长率",            
 "2001" as "总资产",                              
 round(decode("2001",0,0,"2002"/"2001")*100,2)||'%' as "负债率",   
 "2020" as "流动资产",               
 "2020"-"2002" as "营运资金",
 "1003" as       "研发费用",                           
 abs(round(decode("1001",0,0,"1003"/"1001")*100,2))||'%' as "研发占比",            
 "2005" as "应收账款",                            
 "2010" as "应付帐款",                          
 "2006" as "库存",                            
 "2007" as "现金及现金等价物",
 "2008" as "短期投资",                           
 "2009" as "长期投资",                           
 "3001" as "运营活动所产生现金",                 
 "3002" as "投资活动所产生现金",                 
 "3003" as "融资活动所产生现金",              
 "3004" as "现金净增减额",                       
 "4014" as "速动比率",                            
 "4012" as "流动比率",                       
 "4008" as "EPS",                            
 "4005" as "每股收入",   
 "4013" as "每股价值",
 round(decode("4008",0,0,k.close/"4008"),2) as "PE",  
 round(decode("4005",0,0,k.close/"4005"),2) as "PS",  
 round(decode("4013",0,0,k.close/"4013"),2) as "PB",  
 "4018" as "PEG"               

from (                                               
 select * from (select type,fn_value,code,fn_date,fiscal_year_ends from stk_fn_data_us where code=p_view_param.get_param('code')
     ) pivot (sum(fn_value) for type in ( 
    4004, /*ROE(ttm)*/                                    
    1002, /*毛利          */                             
    4002, /*毛利          */                               
    1001, /*营收总额     */                   
    1008, /*净利润                   */                   
    2001, /*资产总额                */        
    2002, /*债务总额                */       
    2020, /*流动资产",                   */
    1003, /*研发费用                 */                   
    2005, /*应收账款总计(净额)*/                           
    2010, /*应付帐款                    */                 
    2006, /*库存总额                 */                    
    2007, /*现金及现金等价物   */                          
    2008, /*短期投资        */                      
    2009, /*长期投资                */                    
    3001, /*运营活动所产生现金  */                    
    3002, /*投资活动所产生现金 */                         
    3003, /*融资活动所产生现金  */        
    3004, /*现金净增减额  */                              
    4014, /*速动比率        */                            
    4012, /*流动比率(mrq) */                               
    4008, /*EPS(ttm)   */                                  
    4005, /*每股收入(ttm)  */                              
    4018, /*PEG    */                                      
    4013 /*帐面价值(mrq)*/                             
  ))                                                   
order by fn_date desc) fn, todayk k where fn.code=k.code
