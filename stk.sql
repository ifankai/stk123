--init table--
/*drop table STK_BILLBOARD;
drop table STK_DEPT_TYPE;
drop table STK_EARNINGS_FORECAST;
drop table STK_ERROR_LOG;
drop table STK_FN_DATA;
drop table STK_FN_TYPE;
drop table STK_HOLDER;
drop table STK_IMPORT_INFO;
drop table STK_IMPORT_INFO_TYPE;
drop table STK_INDUSTRY;
drop table STK_INDUSTRY_RANK;
drop table STK_INDUSTRY_TYPE;
drop table STK_INFO_LOG;
drop table STK_INTERNET_SEARCH;
drop table STK_KLINE;
drop table STK_PE;
drop table STK_REPORT;
drop table STK;
drop table stk_trans_account;*/

create table stk(
  code    varchar2(10),
  name    varchar2(40),
  insert_time date
);
alter table stk add constraint pk_stk primary key (code);
alter table stk add listing_date varchar2(8);
alter table stk add total_capital number(12,2);
alter table stk add status number(4) default 0;
alter table stk add status_date date;
alter table stk add earning_expect varchar2(4000);
alter table stk add earning_expect_date date;
alter table stk add company_profile clob;
alter table stk add sale_limit clob;
alter table stk add market number(1); --1：A股 2：美股 5：外汇
alter table stk modify name varchar2(100);
alter table stk add year_end varchar2(4);
alter table stk add next_earning number(10,2);
alter table stk modify next_quarter_earning varchar2(4000);
alter table stk add cate number(1) default 1;--1:个股 2:指数  3:基金
alter table stk add f9 clob; 
alter table stk add address varchar2(20);

create or replace view stk_cn as select * from stk where market=1 and cate=1;
create or replace view stk_us as select * from stk where market=2 and cate=1;

create index idx_stk__market_cate on stk (market,cate);

create table stk_fn_type(
  type  number(2),
  name  varchar2(100)
);
alter table stk_fn_type add constraint pk_stk_fn_type primary key (type);
alter table stk_fn_type add sina_typecode varchar2(20);
alter table stk_fn_type add not_less_value number(6,2);
alter table stk_fn_type add not_great_value number(6,2);
alter table stk_fn_type add source number(2) default 1;
alter table stk_fn_type modify type number(4);
alter table stk_fn_type add status number(2) default 1;
alter table stk_fn_type add market number(1);
alter table stk_fn_type add is_percent number(1) default 0;
alter table stk_fn_type add currency_unit_adjust number(20,8) default 1;
alter table stk_fn_type modify source number(4);
alter table stk_fn_type add cn_name varchar2(200);
alter table stk_fn_type add disp_order number(4);
ALTER TABLE stk_fn_type RENAME COLUMN cn_name TO disp_name;
ALTER TABLE stk_fn_type RENAME COLUMN sina_typecode TO name_alias;
alter table stk_fn_type modify name_alias varchar2(100);
alter table stk_fn_type add re_calc varchar2(20);
alter table stk_fn_type drop column not_less_value;
alter table stk_fn_type drop column not_great_value;

create table stk_fn_data(
  code   varchar2(10),
  type   number(2),
  fn_date varchar2(8),
  fn_value number(10,2),
  insert_time date
);
alter table stk_fn_data modify type number(4);
alter table stk_fn_data modify fn_value number(20,2);
alter table stk_fn_data add update_time date;
alter table stk_fn_data
  add constraint fk_fn_data__code foreign key (code)
  references stk (code);
alter table stk_fn_data
  add constraint fk_fn_data__type foreign key (type)
  references stk_fn_type (type);
create index idx_fn_data__code on stk_fn_data (code); 
create index idx_fn_data__code_type on stk_fn_data (code,type,fn_date);

create table STK_FN_DATA_US
(
  CODE        VARCHAR2(10),
  TYPE        NUMBER(4),
  FN_DATE     VARCHAR2(8),
  FN_VALUE    NUMBER(20,2),
  INSERT_TIME DATE,
  UPDATE_TIME DATE
)
tablespace STK_TABLESPACE_2;

alter table STK_FN_DATA_US
  add constraint FK_FN_DATA_US__CODE foreign key (CODE)
  references STK (CODE);
alter table STK_FN_DATA_US
  add constraint FK_FN_DATA_US__TYPE foreign key (TYPE)
  references STK_FN_TYPE (TYPE);

create index IDX_FN_DATA_US__CODE on STK_FN_DATA_US (CODE)
  tablespace STK_TABLESPACE_2;

create index IDX_FN_DATA_US__CODE_TYPE on STK_FN_DATA_US (CODE, TYPE, FN_DATE)
  tablespace STK_TABLESPACE_2;
  

create table STK_FN_DATA_US_year
(
  CODE        VARCHAR2(10),
  TYPE        NUMBER(4),
  FN_DATE     VARCHAR2(8),
  FN_VALUE    NUMBER(20,2),
  INSERT_TIME DATE,
  UPDATE_TIME DATE
)
tablespace STK_TABLESPACE_2;

alter table STK_FN_DATA_US_year
  add constraint FK_FN_DATA_US_year_CODE foreign key (CODE)
  references STK (CODE);
alter table STK_FN_DATA_US_year
  add constraint FK_FN_DATA_US_year_TYPE foreign key (TYPE)
  references STK_FN_TYPE (TYPE);

create index IDX_FN_DATA_US_year_CODE on STK_FN_DATA_US_year (CODE)
  tablespace STK_TABLESPACE_2;

create index IDX_FN_DATA_US_year_CODE_TYPE on STK_FN_DATA_US_year (CODE, TYPE, FN_DATE)
  tablespace STK_TABLESPACE_2;


create table stk_info_log(
  code   varchar2(10),
  source varchar2(20),
  description varchar2(4000),
  url   varchar2(1000),
  insert_time date
);
alter table stk_info_log
  add constraint fk_info_log__code foreign key (code)
  references stk (code);
create index idx_info_log__code on stk_info_log (code);  

create table stk_error_log(
  code   varchar2(10),
  error  varchar2(4000),
  insert_time date
);
alter table stk_error_log
  add constraint fk_error_log__code foreign key (code)
  references stk (code);
alter table stk_error_log disable constraint fk_error_log__code ; 

create table stk_industry_type(
  id number(6),
  name varchar2(200),
  source varchar2(20)
);
alter table stk_industry_type add care_flag number(2) default 0;
alter table stk_industry_type add constraint pk_id primary key (id);
alter table stk_industry_type add parent_id number(6);
alter table stk_industry_type add us_name varchar2(200);
create index idx_industry_type_name on stk_industry_type (name); 

create table stk_industry(
  code   varchar2(10),
  industry number(6)
);
alter table stk_industry
  add constraint fk_industry__code foreign key (code)
  references stk (code);
alter table stk_industry
  add constraint fk_industry__industry foreign key (industry)
  references stk_industry_type (id);

create table stk_billboard(
  code   varchar2(10),
  trans_date  varchar2(10),
  dept_id  number(8),
  buy_amount number(18,2) default 0.0,
  sell_amount number(18,2) default 0.0,
  volume number(8,2),
  proportion number(6,2)
);
alter table stk_billboard
  add constraint fk_billboard__code foreign key (code)
  references stk (code);
create index idx_billboard__code on stk_billboard (code);   

create table stk_dept_type(
  dept_id number(8),
  dept_name varchar2(200)
);
alter table stk_dept_type add constraint pk_dept_id primary key (dept_id);
alter table stk_billboard
  add constraint fk_billboard__dept_id foreign key (dept_id)
  references stk_dept_type (dept_id);

create table stk_report(
  id  number(8),
  report_date  varchar2(10),
  report_text  clob
);
alter table stk_report add constraint pk_report_id primary key (id);
alter table stk_report add average_pe number(8,2);

create table stk_kline(
 code   varchar2(10),
 kline_date varchar2(8),
 open   number(8,2),
 close  number(8,2),
 last_close number(8,2),
 high   number(8,2),
 low    number(8,2),
 volumn number(16,2),
 amount number(16,2),
 close_change number(12,6)
);
create index idx_kline__code on stk_kline (code);
create index idx_kline__code_date on stk_kline (code,kline_date);
alter table stk_kline add hsl number(8,2);
alter table stk_kline add pe_ttm number(10,2);
alter table stk_kline add pe_lyr number(10,2);

create table stk_industry_rank(
 industry_id   number(6),
 period        number(4), 
 rank_date     varchar2(8),
 rank       number(4),
 close_change number(6,2),
 rank_desc     clob
);
alter table stk_industry_rank
  add constraint fk_industry_rank__id foreign key (industry_id)
  references stk_industry_type (id);
create index idx_rank__id_period_date on stk_industry_rank (industry_id,period,rank_date);

create table stk_holder(
  code   varchar2(10),
  fn_date   varchar2(8),
  holder  number(10,2)
);
alter table stk_holder
  add constraint fk_holder__code foreign key (code)
  references stk (code);

create table stk_import_info(
  id     number(6),
  code   varchar2(10),
  type   number(4),
  insert_time date,
  care_flag  number(2) default 1,
  info   varchar2(2000)
);
alter table stk_import_info modify info varchar2(4000);
alter table stk_import_info add constraint pk_import_info primary key (id);
alter table stk_import_info
  add constraint fk_import_info__code foreign key (code)
  references stk (code);

create table stk_import_info_type(
  type   number(4),
  name   varchar2(200)
);
alter table stk_import_info_type add constraint pk_import_info_type primary key (type);
alter table stk_import_info
  add constraint fk_import_info__type foreign key (type)
  references stk_import_info_type (type);

create table stk_pe(
  code varchar2(10),
  pe_date varchar2(8),
  pe_lyr number(10,2),
  pe_ttm number(10,2)
);
alter table stk_pe
  add constraint fk_pe__code foreign key (code)
  references stk (code);
create index idx_pe__code on stk_pe (code);  
alter table stk_pe add (ene_upper_cnt number(6),ene_lower_cnt number(6));
alter table stk_pe add (upper_1 number(6),lower_1 number(6));
alter table stk_pe add bias number(6,2);

create table stk_earnings_forecast(
  code varchar2(10),
  forecast_year varchar2(10),
  forecast_net_profit number(16,2),
  insert_time date
);
alter table stk_earnings_forecast
  add constraint fk_earnings_forecast__code foreign key (code)
  references stk (code);
create index idx_earnings_forecast__code on stk_earnings_forecast (code);  

create table stk_internet_search(
  search_source number(4),
  search_url varchar2(1000),
  last_search_text varchar2(1000),
  update_time date
);
alter table stk_internet_search add status number(2);
alter table stk_internet_search add desc_1 varchar2(500);

create table stk_trans_account(
  id number(6),
  week_start_date varchar2(8),
  week_end_date varchar2(8),
  valid_account number(12,2),
  new_account number(12,2),
  hold_a_account number(12,2),
  trans_a_account number(12,2),
  hold_trans_activity number(6,2),
  valid_trans_activity number(6,2),
  new_trans_activity number(6,2),
  result_1 number(6,2),
  result_2 number(6,2)
);
alter table stk_trans_account add constraint pk_trans_account primary key (id);
alter table stk_trans_account modify hold_trans_activity number(8,4);
alter table stk_trans_account modify valid_trans_activity number(8,4);
alter table stk_trans_account modify new_trans_activity number(8,4);
alter table stk_trans_account modify result_1 number(8,4);
alter table stk_trans_account modify result_2 number(8,4);

create table STK_KLINE_US
(
  CODE         VARCHAR2(10),
  KLINE_DATE   VARCHAR2(8),
  OPEN         NUMBER(8,2),
  CLOSE        NUMBER(8,2),
  LAST_CLOSE   NUMBER(8,2),
  HIGH         NUMBER(8,2),
  LOW          NUMBER(8,2),
  VOLUMN       NUMBER(16,2),
  AMOUNT       NUMBER(16,2),
  CLOSE_CHANGE NUMBER(12,6),
  HSL          NUMBER(8,2)
)
tablespace STK_TABLESPACE_2;
alter table stk_kline_us add pe_ttm number(10,2);
alter table stk_kline_us add pe_lyr number(10,2);
create index IDX_KLINE_M__CODE on STK_KLINE_US (CODE) tablespace STK_TABLESPACE_2;
create index IDX_KLINE_M__CODE_DATE on STK_KLINE_US (CODE, KLINE_DATE) tablespace STK_TABLESPACE_2;

create table stk_organization_type(
  id number(4),
  name varchar2(100)
);
alter table stk_organization_type add constraint pk_organization_type_id primary key (id);
create sequence s_organization_type_id
　　INCREMENT BY 1
　　START WITH 1
　　NOMAXVALUE
　　NOCYCLE 
　　CACHE 10;

create table stk_organization(
  id number(8),
  name varchar2(200)
);
alter table stk_organization add constraint pk_organization_id primary key (id);
create index idx_organization_name on stk_organization (name);
create sequence s_organization_id
　　INCREMENT BY 1
　　START WITH 1000
　　NOMAXVALUE
　　NOCYCLE 
　　CACHE 10;


create table stk_ownership(
  code VARCHAR2(10),
  fn_date varchar2(8),
  org_id number(8),
  stk_num number(12,2), 
  rate number(6,2),
  num_change number(12,2)
);
alter table stk_ownership add constraint fk_ownership__org_id foreign key (org_id) references stk_organization (id);
create index idx_ownership__code on stk_ownership (code);
alter table stk_ownership add type number(4);
alter table stk_ownership add constraint fk_ownership__type foreign key (type) references stk_organization_type (id);

create table stk_keyword(
  id number(12),
  name varchar2(200),
  boost number(8) default 1
);
alter table stk_keyword add insert_time date;
alter table stk_keyword add deleted number(1) default 0;
alter table stk_keyword rename column deleted to status; -- -1:deleted  0:normal(manual-add)  1:normal(auto-add)
alter table stk_keyword add constraint pk_keyword_id primary key (id);
create index idx_keyword__name on stk_keyword (name);
ALTER TABLE stk_keyword ADD CONSTRAINT uidx_keyword_name UNIQUE (name);
create sequence s_keyword_id
　　INCREMENT BY 1
　　START WITH 100000
　　NOMAXVALUE
　　NOCYCLE 
　　CACHE 10;

create table stk_keyword_link(
  id number(12),
  code varchar2(20) not null, --ref code
  code_type number(6)  not null,  -- 1:stock, 2:industry,
  keyword_id number(12)  not null,
  boost number(8) default 1
);
alter table stk_keyword_link add link_type number(6) default 0;  -- default(manually add):0, 主营业务:1
alter table stk_keyword_link add insert_time date;
alter table stk_keyword_link add constraint pk_keyword_link_id primary key (id);
alter table stk_keyword_link add constraint fk_keyword_link__key_id foreign key (keyword_id) references stk_keyword (id);
create index idx_keyword_link__code on stk_keyword_link (code);
create index idx_keyword_link__code_type on stk_keyword_link (code,code_type);
create index idx_keyword_link__key_id on stk_keyword_link (keyword_id);
create sequence s_keyword_link_id
　　INCREMENT BY 1
　　START WITH 10000000
　　NOMAXVALUE
　　NOCYCLE 
　　CACHE 10;

create table stk_text(
	id number(8),
  type number(4),  --短文：1，长文：2
	code varchar2(20),
	code_type number(6), -- 1: stock  2: industry ...
  title varchar2(1000),
	text clob,
	insert_time date,
  update_time date
);
comment on column stk_text.type is '0:收藏文章; 短文:1; 长文:2';
alter table stk_text add constraint pk_text_id primary key (id);
alter table stk_text add disp_order number(4) default 0; 
create index idx_text__code_type on stk_text (code,code_type);
create sequence s_text_id INCREMENT BY 1 START WITH 100000 NOMAXVALUE NOCYCLE CACHE 10;


create table stk_data_industry_pe(
  industry_id number(6),
  pe_date varchar2(8),
  type number(1),   -- 1：中小板，2：创业板，3：沪深
  pe  number(10,2),
  pe_ttm number(10,2),
  insert_time date
);
create index idx_data_ind_pe_id_date_type on stk_data_industry_pe (industry_id,pe_date,type);


create table stk_kline_rank_industry(
  rank_id  number(8),
  industry_id number(6),
  rank_date varchar2(8),
  rank_days number(4),
  rank_percent number(6,2),
  rank number(6)  
);
alter table stk_kline_rank_industry
  add constraint fk_kline_rank_ind_id foreign key (industry_id)
  references stk_industry_type (id);
alter table stk_kline_rank_industry add constraint pk_k_rank_ind_id primary key (rank_id);
create sequence s_kline_rank_ind_id INCREMENT BY 1 START WITH 100000 NOMAXVALUE NOCYCLE CACHE 10;
create index idx_kline_rank_ind_ind_rank on stk_kline_rank_industry (rank_date,rank_days);
create unique index idx_kline_rank_ind_ind_id on stk_kline_rank_industry (industry_id,rank_date,rank_days);
ALTER TABLE stk_kline_rank_industry RENAME COLUMN rank_percent TO change_percent;
alter table stk_kline_rank_industry modify change_percent number(8,4);

create table stk_kline_rank_industry_stock(
  rank_id number(8),
  code varchar2(10),
  rank_percent number(6,2)
);
alter table stk_kline_rank_industry_stock
  add constraint fk_kline_rank_ind_rank_id foreign key (rank_id)
  references stk_kline_rank_industry (rank_id);
create index idx_kline_rank_stk_rank_id on stk_kline_rank_industry_stock (rank_id);
ALTER TABLE stk_kline_rank_industry_stock RENAME COLUMN rank_percent TO change_percent;
alter table stk_kline_rank_industry_stock modify change_percent number(8,4);


create table stk_data_eastmoney_guba(
  code varchar2(10),
  insert_date varchar2(8),
  num_click number(8),
  num_reply number(8),
  num_total number(8),
  insert_time date
);
create index idx_data_eastmoney_guba_code on stk_data_eastmoney_guba (code); 
create index idx_data_eastmoney_guba_date on stk_data_eastmoney_guba (insert_date);


create table stk_data_ppi_type(
  id number(8),
  name varchar2(100),
  parent_id number(8)
);
alter table stk_data_ppi_type add constraint pk_data_ppi_id primary key (id);
create sequence s_data_ppi_type_id INCREMENT BY 1 START WITH 1000 NOMAXVALUE NOCYCLE CACHE 10;
alter table stk_data_ppi_type add url varchar2(100);


create table stk_data_ppi(
  type_id number(8),
  ppi_date varchar2(10),
  value  number(10,2),
  insert_time date
);
alter table stk_data_ppi add constraint fk_data_ppi_id foreign key (type_id) references stk_data_ppi_type (id);
create index idx_data_ppi_type_id on stk_data_ppi (type_id);

create table stk_report_daily(
  type number(4),
  report_date varchar2(10),
  code varchar2(10),
  remark varchar2(40),
  insert_time date
);
alter table stk_report_daily add remark_2 varchar2(40);
create index idx_report_daily_type_date on stk_report_daily (type, report_date);

create table stk_capital_flow(
  code varchar2(10),
  flow_date varchar2(8),
  main_amount number(20,2),
  main_percent number(8,2),
  super_large_amount number(20,2),
  super_large_percent number(8,2),
  large_amount number(20,2),
  large_percent number(8,2),
  middle_amount number(20,2),
  middle_percent number(8,2),
  small_amount number(20,2),
  small_percent number(8,2),
  insert_time date
);
create index idx_capital_flow_code on stk_capital_flow (code); 
create index idx_capital_flow_code_date on stk_capital_flow (code,flow_date); 


create table stk_monitor(
  id number(8),
  code varchar2(10),
  type number(4),
  status number(1),
  trigger_date date,
  insert_date date,
  param_1 varchar2(40),
  param_2 varchar2(40),
  param_3 varchar2(40),
  param_4 varchar2(40),
  param_5 varchar2(40),
  result_1 varchar2(400)
);
create sequence s_monitor_id INCREMENT BY 1 START WITH 1000 NOMAXVALUE NOCYCLE CACHE 10;
alter table stk_monitor add constraint pk_monitor_id primary key (id);
create index idx_monitor_code on stk_monitor (code); 
create index idx_monitor_trigger_date on stk_monitor (trigger_date); 

create table stk_dictionary(
  type number(8), 
  key varchar2(40),
  text varchar2(200),
  remark varchar2(400)
);
create index idx_dictionary_type on stk_dictionary (type); 

create table stk_label(
  id number(6),
  name varchar2(40),
  insert_time date,
  update_time date
);
alter table stk_label add constraint pk_label_id primary key (id);
create sequence s_label_id INCREMENT BY 1 START WITH 100000 NOMAXVALUE NOCYCLE CACHE 10;

create table stk_label_text(
  id number(8),
  label_id number(6),
  text_id number(8),
  insert_time date,
  update_time date
);
alter table stk_label_text add constraint pk_label_text_id primary key (id);
alter table stk_label_text
  add constraint fk_label_text_label_id foreign key (label_id)
  references stk_label (id);
create sequence s_label_text_id INCREMENT BY 1 START WITH 10000000 NOMAXVALUE NOCYCLE CACHE 10;


create table stk_user(
  id number(8),
  name varchar2(40),
  nickname varchar2(100),
  password varchar2(40),
  email varchar2(200)  
);
alter table stk_user add constraint pk_user_id primary key (id);
create sequence s_user_id INCREMENT BY 1 START WITH 10000000 NOMAXVALUE NOCYCLE CACHE 10;
ALTER TABLE stk_user ADD CONSTRAINT uidx_user_name UNIQUE (name);



---
create table stk_migration(
  id number(8),
  table_name varchar2(100),
  sql_text varchar2(2000),
  sql_data clob,
  migration_flag number(1) default 0, --0:initial,1:succ,2:fail
  insert_time date,
  update_time date
);


alter table STK_BILLBOARD            disable constraint FK_BILLBOARD__CODE        ;
alter table STK_BILLBOARD            disable constraint FK_BILLBOARD__DEPT_ID     ;
alter table STK_EARNINGS_FORECAST    disable constraint FK_EARNINGS_FORECAST__CODE;
alter table STK_ERROR_LOG            disable constraint FK_ERROR_LOG__CODE        ;
alter table STK_FN_DATA              disable constraint FK_FN_DATA__CODE          ;
alter table STK_FN_DATA              disable constraint FK_FN_DATA__TYPE          ;
alter table STK_HOLDER               disable constraint FK_HOLDER__CODE           ;
alter table STK_IMPORT_INFO          disable constraint FK_IMPORT_INFO__CODE      ;
alter table STK_IMPORT_INFO          disable constraint FK_IMPORT_INFO__TYPE      ;
alter table STK_INDUSTRY             disable constraint FK_INDUSTRY__CODE         ;
alter table STK_INDUSTRY             disable constraint FK_INDUSTRY__INDUSTRY     ;
alter table STK_INDUSTRY_RANK        disable constraint FK_INDUSTRY_RANK__ID      ;
alter table STK_INFO_LOG             disable constraint FK_INFO_LOG__CODE         ;
alter table STK_PE                   disable constraint FK_PE__CODE               ;

---init end---

 
/*
10	净资产收益率	financialratios59	12.00	
20	净利润增长率	financialratios44	20.00	
30	销售毛利率	financialratios36	30.00	
35	销售净利率	financialratios32	20.00	
40	主营收入增长率	financialratios43	20.00	
50	应收账款周转率	financialratios3	5.00	
60	现金流量比率	financialratios51	50.00	
70	经现流净资产比	financialratios48	0.10	
80	资产负债率	financialratios56		40.00
90	流动比率	financialratios1	2.00	
100	净利润
200	预收账款
210	应收账款
300	经营现金流
*/


----end

select * from tab where tname like 'STK%';
select * from stk_stat_data;

select * from stk_fn_type;
select * from stk_fn_type where market=1;
select * from stk_fn_data where type='200';
select * from stk_industry a,stk_industry_type b where a.industry=b.id;
select * from stk_industry_type ;
select * from stk_industry for update;

truncate table stk_error_log;
select * from stk_error_log order by insert_time desc;

select count(1) from stk;--2464
select * from stk order by code;
select * from stk order by insert_time desc;
select * from stk where to_char(company_profile) like '%%';
select * from stk where code='8080).' for update;
select * from stk_industry where code='300350' for update;
update stk set status=1,status_date=sysdate where code='601933' and status <> 1;
select code,name,total_capital,cur_price,total_capital*cur_price from stk where code in ('000423','000425','000501','000538','000780','000880','000887','000937','002081','002082','002089','002128','002304','600028','600031','600036','600066','600067','600104','600123','600139','600153','600161','600195','600252','600271','600276','600309','600315','600327','600348','600395','600436','600508','600511','600519','600547','600561','600570','600582','600594','600600','600658','600729','600739','600742','600743','600745','600779','600805','600809','600816','600828','600829','600845','600875','600880','600887','600970','600971','601006','601166','601169','601328','601398','601666','601699','601988','000006','000012','000028','000069','000157','000338','000411','000417','000506','000527','000537','000540','000550','000568','000581','000596','000623','000631','000650','000651','000661','000671','000708','000718','000732','000792','000848','000858','000861','000869','000895','000900','000933','000963','000979','000983','000987','000999','002001','002007','002022','002024','002038','002051','002063','002065','002069','002091','002096','002104','002140','002146','002152','002153','002191','002223','002242','002244','002250','002252','002269','300003','600115','600216','600223','600225','600375','600406','600505','600660','600785','600794','600897','600983','601088','601111','601899','601939') order by total_capital*cur_price;
select * from stk where name like '%N%';
update stk set status = 0 where name like '%ST%';
select * from stk where status=1 for update;
select * from stk_fn_type for update;

select count(1) from stk_fn_data;
select * from stk where code not in (select code from stk_fn_data);
select * from stk_fn_data order by code,fn_date desc;
select * from stk_fn_data where code='600706' order by fn_date desc;
select * from stk_import_info where code='600643';
select * from stk_import_info where id not in (
select * from stk_import_info where info like '%净利润%增%');
select * from stk_fn_data where code='002230' and type in (111,303) order by type desc,fn_date desc;
select * from stk_fn_type where market=1;

select * from stk_fn_data where fn_date in ('20121231') and fn_value is not null order by code asc,fn_date desc;
select * from stk_fn_data where fn_date like '%20111231' and fn_value>=5;--
select * from stk_fn_data where fn_date='20130331';
select * from stk_fn_data order by insert_time desc;

delete from stk_info_log where source='wind_yd';
select count(1) from stk_info_log;
select * from stk_info_log where code='002275';
select * from stk_info_log order by code;

select * from stk_error_log order by insert_time desc;
delete from stk_error_log;

update stk set status=1,status_date=sysdate where code in (300262) and status <> 1;
select * from stk where status=1 order by status_date;

select * from stk_industry_type order by id desc;
select * from stk_industry_type where source='hexun_conception';
select * from stk_industry_type where id in (1150);
select * from stk a,stk_industry b where a.code=b.code and b.industry=1210;

select * from stk_industry where code='600396';
select code,count(code) from stk_industry group by code having count(code)>1;
select a.* from stk a,stk_industry b,stk_industry_type c where a.code=b.code and b.industry=c.id and c.source='wind' order by b.industry;
select * from stk_industry where code='600396' and industry in (select id from stk_industry_type where source='wind');

select * from stk_billboard where /*dept_id=127 and*/ buy_amount>sell_amount order by trans_date desc;
select * from stk_billboard order by trans_date desc;
select * from stk_dept_type;
select count(1) from stk_billboard order by trans_date desc;---1811


select * from stk_info_log where code='002554' and source='hexun';

select * from stk_report order by id desc;
select * from t_ui_validator_rule where pattern is not null or custom_validate_rule is not null;

select * from stk where code not in (
select distinct code from stk_kline) order by code;
select * from stk_kline where code='601999' order by kline_date desc;
select * from stk_kline where code='600140';
select * from stk_kline where code='600278' order by kline_date desc;
select count(1) from stk_kline;

select count(1) from stk a, stk_industry b, stk_industry_type c 
where a.code=b.code and b.industry=c.id and c.source='hexun_conception'
and a.code='300150';

--number of every industry for hexun_conception
select industry,count(industry) from stk_industry a 
where a.industry in (select id from stk_industry_type where source='hexun_conception')
group by industry having count(industry)>1; 

select * from stk_industry where industry=1150;

select code,count(code) from stk_industry a where a.industry in (select id from stk_industry_type where source='hexun_conception')
group by code having count(code)>1 ;

select * from stk_industry_type where id in (select industry from stk_industry where code='000063');
select * from stk_industry_rank where rank_date='20120918';

select kline_date,close_change from (select * from stk_kline where code='600687' order by kline_date desc) where rownum<=60;

select * from stk_kline where code='002275' order by kline_date desc;
delete from stk_kline where code='002275';
select count(1) from stk_kline where kline_date='20130930' order by code desc;
select * from stk_kline where close_change is null;
select * from (select * from stk_kline where code='002447' order by kline_date desc) where rownum <=2;
select a.*,rank() over(order by a.kline_date desc) rank from stk_kline a where code='002447';
select * from (select * from stk_kline where code='002447' and close_change is null order by kline_date desc)
union all
select * from (select * from stk_kline where code='002447' and close_change is not null order by kline_date desc) where rownum <=1;

select * from (select * from stk_kline where code=? and close_change is null order by kline_date desc) union all select * from (select * from stk_kline where code=? and close_change is not null order by kline_date desc) where rownum <=1;


select * from stk_error_log order by insert_time desc;
delete from stk_error_log where error like '%at task.InitialData.setFnData(InitialData.java:44%';

select * from stk_holder a,stk b where a.code=b.code and holder<=5000 and fn_date='20120630' order by holder;
select * from stk_holder where code='300080';
select * from stk_import_info_type for update;
select * from stk_import_info;
select * from stk_import_info where care_flag=1 order by code asc,id desc;

select sum(bytes)/(1024*1024) as "size(M)" from user_segments where segment_name=upper('stk_fn_data');
select table_name,cache from user_tables where instr(cache,'Y') > 0;

select * from stk_holder where code='300244' order by fn_date desc;


select * from stk where market=1 order by code desc;
select * from stk where code='5173' for update;
select * from stk_fn_data where code='600587' and type=100 order by fn_date desc;
select * from stk_fn_type for update;
/*
毛利 = 总收入 - 成本总计
*/
insert into stk_fn_type select 3004,'现金净增减额',null,null,null,12,1,2 from dual;
select * from stk_industry where code='5173' for update;
select * from stk_industry_type order by id desc;
select * from stk_error_log order by insert_time desc;
truncate table stk_error_log;
select * from stk_kline where code='300110' and kline_date like '201104%';
--truncate table stk_kline;
select count(1) from stk_kline where kline_date='20130923';
select count(1) from stk_kline where kline_date='20130919';
--19911019

select * from stk_kline where code='000333' order by kline_date desc;
select distinct code from stk_kline where close_change is null;
select * from stk_kline where last_close=0;
select * from stk_kline where code<>'999999' and close_change is null order by kline_date desc;
select * from stk_fn_data where code='600587' and fn_value is not null order by fn_date desc;
select s.code,s.name from stk s,stk_industry i,stk_industry_type t where s.code=i.code and i.industry=t.id and t.source='wind' order by t.id,s.code;

select * from stk_industry_rank where rank_date='20121205' order by rank_date desc, period desc, rank asc;
select * from stk_billboard where dept_id=127 order by trans_date desc;
select * from stk_dept_type where dept_name like '%机构%';--127
select * from stk_billboard where code='002443' order by trans_date desc;


select * from stk_report order by report_date desc;
select * from stk_fn_data where code='002400' and type=100;
select * from stk_import_info order by insert_time desc;

select * from stk_fn_data where fn_date='20130630';
select * from stk_fn_data where update_time >= sysdate-100 order by update_time desc;

select * from stk_kline where code='300091' order by kline_date desc;
select * from stk_kline where close_change is null for update;
select count(1) from stk_kline where  hsl is not null ;

select * from stk_kline order by code asc;
select * from stk where market=1;
select name,code,next_quarter_earning from stk where market=1 and code<>'999999' 
 and next_quarter_earning like '%2013-12-31%' and next_quarter_earning like '%增长%' order by code;

select * from stk_earnings_forecast order by insert_time desc;
select distinct code from stk_earnings_forecast;

select * from stk_internet_search order by search_source desc;
select * from stk_internet_search where search_source=1 for update;
update stk_internet_search set last_search_text=null where search_source=10;
insert into stk_internet_search
select 10,'',null,null,1,'' from dual;

select * from stk where market=2 and code='WBAI' order by code asc;
select * from stk where company_profile like '%多晶硅%';
select * from stk where code = '.DJI';
select * from stk where code like '.%';
select * from stk_industry_type where source='xueqiu_meigu';
select * from stk_industry_type where source='sina_meigu';
select * from stk_industry where code='IHC';


select segment_name, sum(bytes)/1024/1024 Mbytese from user_segments where segment_type='TABLE' group by segment_name order by Mbytese desc;
select * from stk_trans_account order by week_start_date desc;
select * from stk_trans_account where rownum <= 20 order by week_start_date desc;

select count(1) from STK_KLINE_US where pe_ttm is not null;
select count(1) from STK_KLINE_US where kline_date='20131011';
select * from stk_kline_us where code='GIVN' order by kline_date desc;

select * from stk where code in ('601268','600700');
select * from stk_fn_type where market=2;
select a.code,a.type,b.re_calc,b.name,a.fn_date,a.fn_value from stk_fn_data a , stk_fn_type b 
  where a.type=b.type and a.code='300082' and a.type='106' order by a.type,a.fn_date desc;
select distinct type from stk_fn_data;
select * from stk_fn_type where market=1 order by disp_order for update;

select * from stk_fn_data where fn_value='91.06'

select * from stk where code='002038';
create table stk_fn_data_bak as select * from stk_fn_data;
select * from stk_fn_data_bak;


select * from stk_trans_account;
select * from stk_internet_search;

select * from stk_kline_us where code='TFG' order by kline_date desc;
select * from STK_FN_DATA_US where fn_date='20130930';
select b.type,b.name,a.fn_date,a.fn_value from stk_fn_data_us a, stk_fn_type b 
  where a.type=b.type and code='NTES' order by type,fn_date desc;
select * from stk where code in ('NTES');

--NTES (网易)
--Gross Margin (毛利率ttm = 毛利ttm/总收入ttm)
SUM(1697.61	1546.08	1608.27	1361.91)/SUM(2264.53	2152.59	2270.87	2001.96)=0.7150

select * from stk_fn_type;
select * from stk_organization_type;
select * from stk_organization where type in (18);
select count(1) from stk_ownership;

select * from stk_industry_type where id=1261;
select * from stk_industry where industry='1261';
select * from stk_kline_us where kline_date > '20140101';
select * from stk_kline_us where code='.DJI' order by kline_date desc;
select * from stk_kline_us where code='KNDI' order by kline_date desc;
update stk_kline_us set volumn=volumn/100 where code='.DJI' and kline_date<='20131004';
select * from stk_kline_us where kline_date='20131028';

select * from stk_report order by report_date desc for update;
select * from stk_kline where code='601933' order by kline_date desc;
select * from stk_kline where kline_date='20140214' and code<=700000;

select * from stk_earnings_forecast where code='300258';
select * from stk where year_end <> '1231'



select * from stk_industry_type order by id desc;
select * from stk_industry_type where name='风能概念';

select s.* from stk_industry s,stk_industry_type t where s.industry=t.id and t.source='cnindex' order by t.source,s.industry;

select * from stk  order by code;
select * from stk_earnings_forecast;

select * from stk_keyword for update;
select * from stk_keyword_link;

select * from stk_text order by id desc;
select id,type,code,code_type,title,text,nvl(update_time,insert_time) update_time from stk_text;


select * from stk_industry_type where source='my_industry_fntype';
select * from stk_industry where industry in (select id from stk_industry_type where source='my_industry_fntype');
select * from stk_industry where industry=1783;


select a.keyword_id,b.name from stk_keyword_link a, stk_keyword b where a.keyword_id=b.id and a.code='600422' and a.code_type=1;

select * from stk_kline where code='000004' order by kline_date desc;
select * from stk_kline where kline_date>=to_char(sysdate,'yyyymmdd') and pe_ttm is null;

--delete from stk_kline where kline_date>=to_char(sysdate,'yyyymmdd');


select * from stk_fn_data where fn_date is null;
select * from stk_error_log order by insert_time desc;
select count(1) from stk_error_log;

select * from stk_internet_search where search_source=1 for update;
select * from stk_text where title like '%伙伴资本看公司%';

select * from stk_kline_rank_industry where rank_date='20131101' and rank_days=2 order by change_percent desc;
select * from stk_kline_rank_industry where rank_date='20131101' and rank <= 5 order by rank_days asc,rank asc;

select * from stk_kline_rank_industry where rank_days=2 and industry_id=1699;
select * from stk_kline_rank_industry_stock where rank_id=129558;
delete from stk_kline_rank_industry;
delete from stk_kline_rank_industry_stock;
select * from stk_kline_rank_industry a,stk_kline_rank_industry_stock b where a.rank_id=b.rank_id order by a.rank_days, a.change_percent desc, b.change_percent desc;


select report_date from (select report_date from stk_report_daily order by report_date desc) where rownum <= 1;
select * from stk_report_daily where report_date='20131127' and type=2 order by to_number(remark_2) desc;
select * from stk_error_log order by insert_time desc;


select * from stk where company_profile like '%IDC%';
select * from stk_data_eastmoney_guba;
select insert_date,sum(num_click),sum(num_total),sum(num_reply) from stk_data_eastmoney_guba group by insert_date order by insert_date desc;
select * from stk_fn_type where market=1;
select * from stk_fn_data where code='300104' and type=303 order by fn_date desc;

select * from stk_pe order by report_date desc ;
update stk_pe set average_pe=average_pe-2 where report_date<='20131018';


select * from stk_keyword where status=-1 order by insert_time desc for update;
update stk_keyword set status =-1 where status=1;
select * from stk_keyword where name='生产';
select * from stk_keyword_link where keyword_id=104423;

select distinct sk.name from stk_keyword sk,stk_keyword_link skl where sk.deleted=0 and sk.id=skl.keyword_id and skl.link_type=0;


select * from stk_data_ppi_type;
select * from stk_data_ppi order by ppi_date desc;
delete from stk_data_ppi;

select * from stk_data_ppi_type where name like '%氨纶%';
select distinct b.name,a.code,a.fn_date,a.stk_num,a.rate from stk_ownership a, stk b where a.code=b.code 
and a.org_id in (47676) order by fn_date desc,rate desc;
select * from stk_organization_type;
select * from stk_organization where name like '%李嘉鑫%';
select * from stk_organization where name like '%泽熙%';--55541,55542,55547,64558,45511,45522,47311,47318,79473,79522
select * from stk_organization where name like '%赵建平%';--56763
select * from stk_organization where name like '%许磊%';--51575
select * from stk_organization where name like '%宋聿倩%';--45671
select * from stk_organization where name like '%濮文%';
select * from stk_organization where name like '%沈昌宇%';
select * from stk_organization where name like '%张利明%';
select * from stk_organization where name like '%黄木顺%';--43029
select * from stk_organization where name like '%吴鸣霄%';--62505
select * from stk_organization where name like '%黄木顺%';--43029
select * from stk_organization where name like '%刘世强%';--57768
select * from stk_organization where name like '%叶光%';--61694
select * from stk_organization where name like '%吴旗%';--46970
select * from stk_organization where name like '%韩常乐%';--47676

select * from stk_ownership;
select code,count(org_id) from stk_ownership where fn_date='20130930' group by code;


select * from stk_kline_rank_industry where rank_date='20131111';
select * from stk_kline_rank_industry where rank_date='20131111' and rank <= 5 order by rank_days asc,rank asc;
select * from stk_kline_rank_industry_stock where rank_id=138331;

select * from stk_capital_flow where code='300037' order by flow_date desc;
select sum(main_amount+super_large_amount),sum(large_amount+middle_amount+small_amount) from stk_capital_flow 
   where code='002582' order by flow_date desc;
select flow_date,sum(main_amount+super_large_amount),sum(large_amount+middle_amount+small_amount) from stk_capital_flow  group by flow_date order by flow_date desc;
select flow_date,sum(main_amount+super_large_amount+large_amount+middle_amount+small_amount),sum(main_amount+super_large_amount),sum(main_amount+small_amount) from stk_capital_flow  group by flow_date order by flow_date desc;
   
select * from stk_capital_flow where flow_date='20131118' and main_percent>=15 and super_large_percent>=15 order by main_percent asc;

select * from stk_monitor for update;
select * from stk_dictionary where type=5;
insert into stk_dictionary select 5,5964068708,'小小辛巴',null from dual;
insert into stk_dictionary select 5,3875738003,'可燃冰',null from dual;
insert into stk_dictionary select 5,8510627167,'利弗莫尔一平',null from dual;
insert into stk_dictionary select 5,6011012030,'重力加速度(风生水起)',null from dual;



select * from stk_text order by insert_time desc for update;
select * from stk_text where text like '%***+2%';
select * from stk_text where code='000997';
select * from stk where replace(name,' ','')='新和成';
insert into stk_text select s_text_id.nextval,1,'',1,null,'',sysdate,null;

select * from (select * from stk_text where type=2 and code=? and title=? order by insert_time desc) where rownum<=1;

create table stk_care(
  type number(4),
  code varchar2(10),
  insert_time date
);

select name,code,next_earning from stk where market=1 and next_quarter_earning like '%2013-12-31%' order by next_earning desc;


select * from stk_kline where kline_date='20131212' and code like '3%' order by pe_ttm asc;
select * from stk_earnings_forecast order by insert_time desc;

select * from stk_report_daily where type in (100,101) order by insert_time desc;


select * from stk_industry_type;


select * from stk_pe where report_date>='20140227' order by id asc for update;

ALTER PROFILE DEFAULT LIMIT PASSWORD_LIFE_TIME UNLIMITED;
select * from dba_profiles where profile='DEFAULT' and resource_name='PASSWORD_LIFE_TIME';
alter user system identified by system;

select * from v$process;
select value from v$parameter where name = 'processes';
alter system set processes = 300 scope = spfile;
shutdown immediate;

 --查看当前有哪些用户正在使用数据
    SELECT osuser, a.username,cpu_time/executions/1000000||'s', sql_fulltext,machine
    from v$session a, v$sqlarea b
    where a.sql_address =b.address order by cpu_time/executions desc;

select * from stk_industry_type t where t.id=1723;
select * from stk_data_industry_pe t where t.industry_id=1723 order by pe_date desc;
select * from stk_data_industry_pe order by pe_date desc;

select * from stk_monitor;
select * from stk_kline where kline_date>=to_char(sysdate,'yyyymmdd') and pe_ttm is null;

select * from stk_data_eastmoney_guba order by insert_time desc;

select * from stk_error_log order by insert_time desc;
select * from stk_internet_search where status=1 and search_source=1 for update;

select * from stk where code in ('603328','600406') for update;
select * from stk_industry where code in ('5173','8080).') for update;

select * from stk_industry_type order by id desc;
select * from stk_kline_us where code='CCIH';
select * from stk_fn_data_us where code='CCIH';
select * from stk_fn_type where market=2;


select distinct b.kline_date "date",a.average_pe pe,b.close "value",a.ene_upper_cnt eneupper,a.ene_lower_cnt enelower from stk_pe a, stk_kline b where b.code='999999' and b.kline_date>='20130520' and a.report_date(+)=b.kline_date  order by b.kline_date asc
select * from stk_industry_type where source='hexun_conception';
select * from stk_kline where code='002273' order by kline_date desc;
select avg(main_amount+super_large_amount) flow,flow_date sum,count(1) from stk_capital_flow where main_amount!=0 and super_large_amount!=0 group by flow_date order by flow_date desc;

select round(avg(main_amount+super_large_amount),2) flow,flow_date,count(1) from stk_capital_flow a,stk_cn b where main_amount!=0 and super_large_amount!=0 and a.code = b.CODE and b.TOTAL_CAPITAL <= 30000 group by flow_date order by flow_date desc;
select round(avg(main_amount+super_large_amount),2) flow,flow_date,count(1) from stk_capital_flow a,stk_cn b where main_amount!=0 and super_large_amount!=0 and a.code = b.CODE and b.TOTAL_CAPITAL >= 100000 group by flow_date order by flow_date desc;

select * from stk where cate=2 for update;
select * from stk where market=5;
select * from stk where code='399101' for update;
select * from stk_cn where total_capital>=100000;
select * from stk_kline where code='01000010' order by kline_date desc;
select * from stk_kline where code='399905' order by kline_date desc;
select * from stk_kline where code='999999' order by kline_date desc;
--delete from stk_kline where code='USDHKD';

select * from stk_data_eastmoney_guba order by insert_date desc;
select avg(num_click)/1000 click,sum(num_reply),insert_date from stk_data_eastmoney_guba group by insert_date order by insert_date desc;

select * from stk_capital_flow;
select round(avg(main_amount+super_large_amount),2) flow,flow_date from stk_capital_flow a,stk_cn b where a.code=b.code and b.TOTAL_CAPITAL<=50000 and main_amount!=0 and super_large_amount!=0 group by flow_date order by flow_date desc;

select * from stk_organization where name like '%中邮战略新兴产业股票%';
select * from stk_ownership a,stk_organization b where a.org_id=b.id and code='002657' order by fn_date desc;
select * from stk_text  order by insert_time desc;

select * from stk_fn_data where code='300024' and type=210 order by fn_date desc;
select * from stk_fn_type where market=1 order by type;
select * from stk_kline where code='601989' order by kline_date desc;

SELECT name,F_TRANS_PINYIN_CAPITAL(name) FROM stk_cn where code=601988;
SELECT case when market = 1 then name||','||F_TRANS_PINYIN_CAPITAL(name)||','||code when market = 2 then code end "activity",code val FROM stk order by code asc;

select * from stk_pe order by report_date;

select * from stk_text where text like '%开尔新材%' or code='300105';
select * from stk_industry_type where source='cnindex';
select * from stk_data_industry_pe a, stk_industry_type b where a.type=3 and a.industry_id=b.id and b.id=1769 order by pe_date desc;

select * from stk_text order by id desc;
select * from stk_text where code='002038';
select * from stk_text where text like '%我是这样划分股票和选股的%';

select * from stk where code='AGQ' for update;
select * from stk_kline_us where code='CLWR' order by kline_date desc;

select * from stk_dictionary where type =3 for update;
select * from stk_internet_search where search_source=11 for update;
insert into stk_dictionary select 10,1,'新浪博客',null from dual;
insert into stk_dictionary select 10,11,'国务院',null from dual;


select * from stk_label;
select * from stk_label_text where text_id=10610440;

select * from stk_data_ppi_type where name like '%橡胶%';
select * from stk_data_ppi where type_id=1336 order by ppi_date desc;

select * from stk_industry_type where id=1798;
select * from stk_keyword where name='预告';

select * from stk_fn_data where code='600268' and type=303 order by fn_date desc;


with 
t as (select distinct fn_date from stk_fn_data where code='000001'),
t1 as (select fn_date,type,fn_value from stk_fn_data where code='000001' and type=103),
t2 as (select fn_date,type,fn_value from stk_fn_data where code='000001' and type=303)
select t.fn_date, t1.fn_value "103", t2.fn_value "303" from t,t1,t2 where t.fn_date=t1.fn_date and t.fn_date=t2.fn_date order by t.fn_date

select * from stk_user;
select * from stk_text;

create table test(
  id int(6),
  name varchar(100),  
  company_desc text,
  insert_time DATETIME
);



{table:'stk',sql:'',data:['','','']}
