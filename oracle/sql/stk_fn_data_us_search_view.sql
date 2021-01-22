create or replace view stk_fn_data_us_search_view as
with todayk as                                      
(select t.close,t.code,t.kline_date                 
    from (select s.*,                               
                 ROW_NUMBER() over(PARTITION by code order by kline_date desc) as num     
            from stk_kline_us s where kline_date>to_char(sysdate-7,'yyyyMMDD') 
            ) t            
       where t.num = 1)
select a.* from(             
select fn.code,fn.fn_date,                          
"4004" as ROE,
"1001" as revenue,                        
round((decode(sum("1001") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following),0,0,"1001"/sum("1001") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following))-1)*100,2) as Revenue_growth_rate,    
nvl("4002",round(decode("1001",0,0,"1002"/"1001")*100,2)) as Gross_Profit_Margin, 
"4001" as Profit_Margin,
"4007" as Operating_Margin,          
"1008" as Net_profit,                          
round((decode(sum("1008") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following),0,0,"1008"/sum("1008") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following))-1)*100,2) as net_profit_Growth_rate,           
"2001" as assets,
"2002" as debt,
round(decode("2001",0,0,"2002"/"2001")*100,2) as debt_rate,
"1003" as  Research_Development,
abs(round(decode("1001",0,0,"1003"/"1001")*100,2)) as   Research_Rate,                        
"2005" as "Ӧ���˿�",                           
"2010" as "Ӧ���ʿ�",                           
"2006" as "���",                           
"2007" as Cash_Cash_Equivalents, 
"2020" as current_assets,
"2008" as  "����Ͷ��",                          
"2009" as  "����Ͷ��",                          
"3001" as  "��Ӫ��������ֽ�",                
"3002" as  "Ͷ�ʻ�������ֽ�",                
"3003" as  "���ʻ�������ֽ�",                
"3004" as  "�ֽ�������",                      
"4014" as "�ٶ�����",                           
"4012" as "��������",                      
"4008" as "EPS",                           
"4005" as "ÿ������",                      
"4013" as "ÿ�ɼ�ֵ",                      
round(decode("4008",0,0,k.close/"4008"),2) as "PE", 
round(decode("4005",0,0,k.close/"4005"),2) as "PS", 
round(decode("4013",0,0,k.close/"4013"),2) as "PB", 
"4018" as "PEG",
row_number() over(partition by fn.code order by fn.fn_date desc) rn                       
from (                                              
select * from (select type,fn_value,code,fn_date,fiscal_year_ends from stk_fn_data_us where fn_date >= to_char(sysdate-800,'yyyyMMDD')
) pivot (sum(fn_value)                              
for type in (                                       
4004, /*ROE(ttm)*/                                   
1002, /*ë��     */  
4002, /*ë����    */                            
1001, /*Ӫ���ܶ�     */                               
1008, /*������                   */                  
2001, /*�ʲ��ܶ�                */                    
2002, /*ծ���ܶ�                */                    
1003, /*�з�����                 */                  
2005, /*Ӧ���˿��ܼ�(����)*/                          
2010, /*Ӧ���ʿ�                    */                
2006, /*����ܶ�                 */                   
2007, /*�ֽ��ֽ�ȼ���   */                         
2008, /*����Ͷ��              */                     
2009, /*����Ͷ��                */                   
2020, /*�����ʲ��ܼ�  */
3001, /*��Ӫ��������ֽ�  */                       
3002, /*Ͷ�ʻ�������ֽ� */                        
3003, /*���ʻ�������ֽ�  */                       
3004, /*�ֽ�������  */                             
4014, /*�ٶ�����        */                           
4012, /*��������(mrq) */                              
4008, /*EPS(ttm)   */                                 
4005, /*ÿ������(ttm)  */                             
4018, /*PEG    */  
4001, /*��������(ttm)*/ 
4007, /*Ӫҵ������(ttm)*/                                   
4013 /*�����ֵ(mrq)*/                               
))                                                  
order by fn_date desc) fn, todayk k 
where fn.code=k.code ) a where a.rn <= 8
