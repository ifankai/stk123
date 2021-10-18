SELECT * FROM DBA_TABLESPACES;
SELECT * FROM DBA_DATA_FILES;
SELECT * FROM DBA_TEMP_FILES;
select name from v$tablespace;

select sum(bytes)/1024/1024/1024 from dba_segments;
Select Segment_Name,Sum(bytes)/1024/1024 M From User_Extents Group By Segment_Name order by M desc;

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
alter table stk modify earning_expect varchar2(4000 char);
alter table stk add earning_expect_date date;
alter table stk add company_profile clob;
alter table stk add sale_limit clob;
alter table stk add market number(1); --1：A�?2：美�?3:港股 5：外�?
alter table stk modify name varchar2(100);
alter table stk add year_end varchar2(4);
alter table stk add next_earning number(10,2);
alter table stk modify next_quarter_earning varchar2(4000 char);
alter table stk add cate number(1) default 1;--1:个股 2:指数  3:基金  4:同花顺板块指�?
alter table stk add f9 clob;
alter table stk add hot number(8) default 0; -- xueqiu follows
alter table stk add fn_currency varchar2(4); --财务币种
alter table stk add place number(1); --1:sh 2:sz 3:

create or replace view stk_cn as select * from stk where market=1 and cate=1;
create or replace view stk_us as select * from stk where market=2 and cate=1 and status=0;
create or replace view stk_hk as select * from stk where market=3 and cate=1;

create index idx_stk__market_cate on stk (market,cate);

create table stk_fn_type(
  type  number(2),
  name  varchar2(100)
);
alter table stk_fn_type add constraint pk_stk_fn_type primary key (type);
alter table stk_fn_type add sina_typecode varchar2(20);
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
alter table stk_fn_type add tab number(1) default 0;
alter table stk_fn_type add precision number(1) default 2;
alter table stk_fn_type add colspan number(1);
alter table stk_fn_type add code varchar2(20);


create table stk_fn_data(
  code   varchar2(10),
  type   number(2),
  fn_date varchar2(8),
  fn_value number(10,2),
  insert_time date
);
alter table stk_fn_data modify type number(4);
alter table stk_fn_data modify fn_value number(22,4);
alter table stk_fn_data add update_time date;
alter table stk_fn_data
  add constraint fk_fn_data__code foreign key (code)
  references stk (code);
alter table stk_fn_data
  add constraint fk_fn_data__type foreign key (type)
  references stk_fn_type (type);
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
alter table STK_FN_DATA_US modify fn_value number(22,4);
alter table STK_FN_DATA_US add Fiscal_Year_Ends NUMBER(1);

alter table STK_FN_DATA_US
  add constraint FK_FN_DATA_US__CODE foreign key (CODE)
  references STK (CODE);
alter table STK_FN_DATA_US
  add constraint FK_FN_DATA_US__TYPE foreign key (TYPE)
  references STK_FN_TYPE (TYPE);

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


create table STK_FN_DATA_HK
(
  CODE        VARCHAR2(10),
  TYPE        NUMBER(4),
  FN_DATE     VARCHAR2(8),
  FN_VALUE    NUMBER(20,4),
  INSERT_TIME DATE,
  UPDATE_TIME DATE
)
tablespace STK_TABLESPACE_2;

alter table STK_FN_DATA_HK
  add constraint FK_FN_DATA_HK__CODE foreign key (CODE)
  references STK (CODE);
alter table STK_FN_DATA_HK
  add constraint FK_FN_DATA_HK__TYPE foreign key (TYPE)
  references STK_FN_TYPE (TYPE);

create index IDX_FN_DATA_HK__CODE_TYPE on STK_FN_DATA_HK (CODE, TYPE, FN_DATE)
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
  error  clob,
  insert_time date
);
-- alter table stk_error_log
--   add constraint fk_error_log__code foreign key (code)
--   references stk (code);
alter table stk_error_log disable constraint fk_error_log__code ;
alter table stk_error_log add id number(8);
alter table stk_error_log add text varchar2(1000);
alter table stk_error_log add constraint pk_error_log_id primary key (id);
create sequence s_error_log_id
　　INCREMENT BY 1
　　START WITH 100000
　　NOMAXVALUE
　　NOCYCLE
　　CACHE 10;
alter table stk_error_log modify code varchar2(200);


create table stk_industry_type(
  id number(6),
  name varchar2(200),
  source varchar2(20)
);
alter table stk_industry_type add care_flag number(2) default 0;
alter table stk_industry_type add constraint pk_id primary key (id);
alter table stk_industry_type add parent_id number(6);
alter table stk_industry_type add us_name varchar2(200);
alter table stk_industry_type add code varchar2(20);
alter table stk_industry_type add parent_code varchar2(20);
create index idx_industry_type_name on stk_industry_type (name);
create index idx_industry_type_source on stk_industry_type (source);

create sequence s_industry_type_id
　　INCREMENT BY 1
　　START WITH 100000
　　NOMAXVALUE
　　NOCYCLE
　　CACHE 10;

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
  buy_ratio number(6,2),
  sell_amount number(18,2) default 0.0,
  sell_ratio number(6,2),
  net_amount number(18,2) default 0.0
);
alter table stk_billboard add buy_sell number(1);
alter table stk_billboard add seq number(2);
alter table stk_billboard
  add constraint fk_billboard__code foreign key (code)
  references stk (code);
create index idx_billboard__code on stk_billboard (code);
create index idx_billboard__trans_date on stk_billboard (trans_date);
create index idx_billboard__dept_id on stk_billboard (dept_id);

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
create index idx_kline__date on stk_kline (kline_date);
create index idx_kline__code_date on stk_kline (code,kline_date);
alter table stk_kline add hsl number(8,2);
alter table stk_kline add pe_ttm number(10,2);
alter table stk_kline add pe_lyr number(10,2);
alter table stk_kline add percentage number(6,2);
alter table stk_kline add ps_ttm number(10,2);
alter table stk_kline add pb_ttm number(10,2);
alter table stk_kline add pe_ntile number(3);
alter table stk_kline add pb_ntile number(3);
alter table stk_kline add ps_ntile number(3);

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
alter table stk_holder add holding_amount number(10,2);
alter table stk_holder add holder_change number(10,2);
alter table stk_holder add ten_owner_change number(10,2);
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
alter table stk_import_info modify info varchar2(4000 char);
alter table stk_import_info modify id number(10);
alter table stk_import_info add title varchar2(1000);
alter table stk_import_info modify title varchar2(1000 char);
alter table stk_import_info add url_source varchar2(1000);
alter table stk_import_info add url_target varchar2(1000);
alter table stk_import_info add info_create_time date;
alter table stk_import_info add constraint pk_import_info primary key (id);
alter table stk_import_info
  add constraint fk_import_info__code foreign key (code)
  references stk (code);
create sequence s_import_info_id
　　INCREMENT BY 1
　　START WITH 100000
　　NOMAXVALUE
　　NOCYCLE
　　CACHE 10;
create index idx_import_info__code on stk_import_info (code);
create index idx_import_info__type on stk_import_info (type);


create table stk_news as select * from stk_import_info where 1=1;
alter table stk_news add constraint pk_news primary key (id);
alter table stk_news
  add constraint fk_new__code foreign key (code)
  references stk (code);
create sequence s_news_id
　　INCREMENT BY 1
　　START WITH 400000
　　NOMAXVALUE
　　NOCYCLE
　　CACHE 10;
create index idx_news__code on stk_news (code);
create index idx_news__type on stk_news (type);


create table stk_import_info_type(
  type   number(4),
  name   varchar2(200)
);
alter table stk_import_info_type add match_pattern varchar2(400);
alter table stk_import_info_type add not_match_pattern varchar2(400);
alter table stk_import_info_type add constraint pk_import_info_type primary key (type);
alter table stk_import_info
  add constraint fk_import_info__type foreign key (type)
  references stk_import_info_type (type);


create table STK_PE
(
  REPORT_DATE   VARCHAR2(10) not null,
  REPORT_TEXT   CLOB,
  AVERAGE_PE    NUMBER(8,2),
  ENE_UPPER_CNT NUMBER(6),
  ENE_LOWER_CNT NUMBER(6),
  UPPER_1       NUMBER(6),
  LOWER_1       NUMBER(6),
  BIAS          NUMBER(6,2),
  ENE_UPPER     NUMBER(6,2),
  ENE_LOWER     NUMBER(6,2),
  RESULT_1      NUMBER(8,2), -- 二品抄底-买入时机
  RESULT_2      NUMBER(8,2), -- 一品抄�?大势已去
  AVG_PB        NUMBER(8,2), -- 平均pb
  TOTAL_PE      NUMBER(8,2), --整体pe
  TOTAL_PB      NUMBER(8,2), --整体pb
  MID_PB        NUMBER(8,2), --中位pb
  MID_PE        NUMBER(8,2), --中位pe
  RESULT_3      NUMBER(8,2), 
  RESULT_4      NUMBER(8,2),
  RESULT_5      NUMBER(8,2),
  RESULT_6      NUMBER(8,2),
  RESULT_7      NUMBER(8,2), --HK average pe
  RESULT_8      NUMBER(8,2), --HK mid pe
  RESULT_9      NUMBER(8,2), --US average pe
  RESULT_10     NUMBER(8,2), --US mid pe
  RESULT_11     NUMBER(8,2),
  RESULT_12     NUMBER(8,2)
);
alter table stk_pe add constraint pk_pe primary key (report_date);
alter table stk_pe add stock_count number(8);


create table stk_earnings_forecast(
  code varchar2(10),
  forecast_year varchar2(10),
  forecast_net_profit number(16,2),
  insert_time date
);
alter table stk_earnings_forecast add pe number(8,2);
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
alter table stk_kline_us add percentage number(6,2);
alter table stk_kline_us add pb_ttm number(10,2);
alter table stk_kline_us add ps_ttm number(10,2);
create index IDX_KLINE_M__DATE on STK_KLINE_US (KLINE_DATE) tablespace STK_TABLESPACE_2;
create index IDX_KLINE_M__CODE_DATE on STK_KLINE_US (CODE, KLINE_DATE) tablespace STK_TABLESPACE_2;
alter table stk_kline_us modify amount number(20,2);

CREATE TABLE STK_KLINE_HK
   (	"CODE" VARCHAR2(10),
	"KLINE_DATE" VARCHAR2(8),
	"OPEN" NUMBER(8,3),
	"CLOSE" NUMBER(8,3),
	"LAST_CLOSE" NUMBER(8,3),
	"HIGH" NUMBER(8,3),
	"LOW" NUMBER(8,3),
	"VOLUMN" NUMBER(16,2),
	"AMOUNT" NUMBER(16,2),
	"CLOSE_CHANGE" NUMBER(12,6),
	"HSL" NUMBER(8,2),
	"PE_TTM" NUMBER(10,2),
	"PE_LYR" NUMBER(10,2),
	"PS_TTM" NUMBER(10,2),
	"PB_TTM" NUMBER(10,2),
	 CONSTRAINT "FK_KLINE_HK__CODE" FOREIGN KEY ("CODE")
	  REFERENCES "STK"."STK" ("CODE")
   )
TABLESPACE "STK_TABLESPACE_2" ;
create index IDX_KLINE_HK__CODE on STK_KLINE_HK (CODE) tablespace STK_TABLESPACE_2;
create index IDX_KLINE_HK__DATE on STK_KLINE_HK (KLINE_DATE) tablespace STK_TABLESPACE_2;
create index IDX_KLINE_HK__CODE_DATE on STK_KLINE_HK (CODE, KLINE_DATE) tablespace STK_TABLESPACE_2;

alter table stk_kline_hk add percentage number(6,2);


/*
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
*/

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
alter table stk_ownership add num_change_rate number(8,2);
alter table stk_ownership add constraint fk_ownership__org_id foreign key (org_id) references stk_organization (id);
create index idx_ownership__code on stk_ownership (code);


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
alter table stk_keyword_link add link_type number(6) default 0;  -- default(manually add):0, 主营业务:1  主营产品:2
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
  type number(4),  --0:收藏文章; 短文:1; 长文:2; 雪球评论:3
	code varchar2(20),
	code_type number(6), -- 1: stock  2: industry ...
  title varchar2(1000),
	text clob,
	insert_time date,
  update_time date
);
comment on column stk_text.type is '0:收藏文章; 短文:1; 长文:2; 雪球评论:3';
alter table stk_text add constraint pk_text_id primary key (id);
alter table stk_text add disp_order number(4) default 0;
alter table stk_text add user_id number(8) default 1;
alter table stk_text add sub_type number(4) default 0; -- see Text.java sub type
create index idx_text__user_id on stk_text (user_id);
create index idx_text__sub_type on stk_text (sub_type);
create index idx_text_insert_time on stk_text (insert_time);
create sequence s_text_id INCREMENT BY 1 START WITH 100000 NOMAXVALUE NOCYCLE CACHE 10;

alter table stk_text modify user_id number(12);
comment on column stk_text.user_id is '发帖人id';
alter table stk_text add user_name varchar2(50);
comment on column stk_text.user_name is '发帖人名�?;
alter table stk_text add user_avatar varchar2(200);
comment on column stk_text.user_avatar is '发帖人头像url';
alter table stk_text add post_id number(12);
comment on column stk_text.post_id is '帖子id';
alter table stk_text add followers_count number(8);
comment on column stk_text.followers_count is '粉丝�?;
alter table stk_text add created_at date;
comment on column stk_text.created_at is '发帖时间';
alter table stk_text add reply_count number(6);
comment on column stk_text.reply_count is '评论�?;
alter table stk_text add favorite_date date;
comment on column stk_text.favorite_date is '收藏时间';
alter table stk_text add read_date date;
comment on column stk_text.favorite_date is '阅读时间';
alter table stk_text add text_desc varchar2(4000);
comment on column stk_text.text_desc is '帖子摘要';
alter table stk_text add reply_positive number(1);
comment on column stk_text.reply_positive is '评论是否正面';

create table stk_data_industry_pe(
  industry_id number(6),
  pe_date varchar2(8),
  type number(1),   -- 1：中小板�?：创业板�?：沪�?
  pe  number(10,2),
  pe_ttm number(10,2),
  insert_time date
);
alter table stk_data_industry_pe add pb number(10,2);
alter table stk_data_industry_pe add adr number(10,2);
comment on column stk_data_industry_pe.adr is '股息�?;
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
  code varchar2(10) not null,
  flow_date varchar2(8) not null,
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
alter table stk_dictionary add param varchar2(200);
alter table stk_dictionary add param_2 varchar2(100);
alter table stk_dictionary add param_3 varchar2(100);
alter table stk_dictionary add param_4 varchar2(100);
alter table stk_dictionary add param_5 varchar2(100);
create index idx_dictionary_type on stk_dictionary (type);
alter table stk_dictionary modify param varchar2(400);
alter table stk_dictionary modify param_2 varchar2(400);
alter table stk_dictionary modify param_3 varchar2(400);
alter table stk_dictionary modify param_4 varchar2(400);
alter table stk_dictionary modify param_5 varchar2(400);
alter table stk_dictionary modify remark varchar2(4000);

create table stk_label(
  id number(6),
  name varchar2(40),
  insert_time date,
  update_time date
);
alter table stk_label add constraint pk_label_id primary key (id);
alter table stk_label add user_id number(8) default 1;
create index idx_label__user_id on stk_label (user_id);
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
  nickname varchar2(100),
  password varchar2(40),
  email varchar2(200)
);
alter table stk_user add earning_search_params varchar2(2000);
alter table stk_user add constraint pk_user_id primary key (id);
--create sequence s_user_id INCREMENT BY 1 START WITH 10000000 NOMAXVALUE NOCYCLE CACHE 10;
ALTER TABLE stk_user ADD CONSTRAINT uidx_user_nickname UNIQUE (nickname);
ALTER TABLE stk_user ADD CONSTRAINT uidx_user_email UNIQUE (email);


CREATE TABLE stk_sequence (
  seq_name varchar2(30) NOT NULL,
  seq_value number(10) DEFAULT 10000
);
alter table stk_sequence add constraint pk_sequence_name primary key (seq_name);
insert into stk_sequence(seq_name, seq_value) values ('s_keyword_id',20000000);
insert into stk_sequence(seq_name, seq_value) values ('s_keyword_link_id',20000000);
insert into stk_sequence(seq_name, seq_value) values ('s_label_id',200000);
insert into stk_sequence(seq_name, seq_value) values ('s_label_text_id',20000000);
insert into stk_sequence(seq_name, seq_value) values ('s_text_id',20000000);
insert into stk_sequence(seq_name, seq_value) values ('s_data_ppi_type_id',20000);
insert into stk_sequence(seq_name, seq_value) values ('s_organization_id',200000);
insert into stk_sequence(seq_name, seq_value) values ('s_organization_type_id',2000);
--production start with '3' number


create table stk_index_node(
  id number(8),
  parent_id number(8),
  name varchar2(200),
  disp_order number(4) default 0
);
alter table stk_index_node add node_level number(2);
alter table stk_index_node add chart_template varchar2(1000);
alter table stk_index_node add constraint pk_index_node primary key (id);


create table stk_investigation(
  id number(8),
  code varchar2(10),
  title varchar2(1000),
  investigator varchar2(4000),
  investigator_count number(8),
  text clob,
  text_count number(8),
  invest_date date,
  insert_date date
);
alter table stk_investigation add source_url varchar2(1000);
alter table stk_investigation add source_type varchar2(10);
create sequence s_investigation_id INCREMENT BY 1 START WITH 100000 NOMAXVALUE NOCYCLE CACHE 10;
alter table stk_investigation add constraint pk_investigation_id primary key (id);
create index idx_investigation_code on stk_investigation (code);
create index idx_investigation_invest_date on stk_investigation (invest_date);

create table stk_care(
  code varchar2(10),
  type varchar2(100),
  info varchar2(1000),
  url varchar2(1000),
  memo varchar2(4000),
  insert_time date,
  info_create_time date,
  param1 varchar2(200),
  param2 varchar2(200)
);


create table stk_word(
 id number(10),
 type number(1) not null, -- 1:基本面词�? 2:情绪面类词库, 3:概念类词�?
 sub_type number(2), -- 1:正面, 2:负面
 name varchar2(40) not null,
 weight number(4) default 1,
 parent_id number(10),
 child_id number(10),
 description varchar2(200)
);
create sequence s_word_id INCREMENT BY 1 START WITH 100000 NOMAXVALUE NOCYCLE CACHE 10;
alter table stk_word add constraint pk_word_id primary key (id);
create unique index idx_word_type_sub_name on stk_word (type,sub_type,name);

create table stk_restricted(
    code varchar2(10),
    report_date varchar2(10),
    listing_date varchar2(10),
    ban_amount number(12,2),
    ban_market_value number(10,4)
);

create index idx_restricted_code on stk_restricted(code);

--业绩预告
create table stk_earnings_notice (
  code varchar2(10),
  fn_date varchar2(10),
  detail varchar2(2000),
  er_low number(8,2),
  er_high number(8,2),
  er_type varchar2(10),
  last_amount number(12,2),
  notice_date varchar2(10), --业绩预告公告�?
  real_date varchar2(10) --季报实际公告�?
);
alter table stk_earnings_notice add insert_time date;
alter table stk_earnings_notice
  add constraint fk_earnings_notice__code foreign key (code)
  references stk (code);
create index idx_earnings_notice__code on stk_earnings_notice (code);


create table stk_daily_report_us(
  report_date varchar2(10),
  result_1 number(8,2),
  result_2 number(8,2),
  result_3 number(8,2),
  result_4 number(8,2),
  result_5 number(8,2)
);


create table stk_xueqiu_user(
  id number(10),
  user_id varchar2(12),
  name varchar2(100)
);
create sequence s_xueqiu_user_id INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE CACHE 10;
alter table stk_xueqiu_user add constraint pk_xueqiu_user_id primary key (id);


create table stk_search_condition (
  id number(6),
  type varchar2(20),
  name varchar2(200),
  text clob,
  insert_time date,
  update_time date
);

--drop table stk_strategy;
create table stk_strategy(
 id number(6),
 strategy_date varchar(10),
 name varchar2(400),
 text varchar2(4000),
 insert_time date
);
create sequence s_strategy_id INCREMENT BY 1 START WITH 1000 NOMAXVALUE NOCYCLE CACHE 10;
alter table stk_strategy add constraint pk_strategy_id primary key (id);

create table stk_xq_post(
  id number(8),
  title varchar2(1000),
  text varchar2(4000),
  created_at date,
  reply_count number(6),
  insert_date date,
  is_favorite number(1) default 0,
  favorite_date date,
  is_read number(1) default 0,
  read_date date,
  user_id number(10),
  user_name varchar2(100),
  user_avatar varchar2(200),
  followers_count number(8)
);
alter table stk_xq_post add constraint pk_xq_post_id primary key (id);


drop table stk_task_log;
create table stk_task_log(
  id number(8),
  task_code varchar2(40), 
  task_name varchar2(100), 
  task_date varchar2(10),
  code varchar2(2000),
  strategy_code varchar2(400),
  strategy_name varchar2(100),
  strategy_pass_date varchar2(10),
  strategy_start_date varchar2(10),
  strategy_end_date varchar2(10),
  insert_time date,
  update_time date,
  status number(1),
  error_msg varchar2(1000),
  task_log clob
);
alter table stk_task_log add constraint pk_task_log_id primary key (id);
create index idx_task_log_task_co_da_st_co on stk_task_log (task_code,task_date,strategy_code);
create sequence s_task_log_id INCREMENT BY 1 START WITH 100000 NOMAXVALUE NOCYCLE CACHE 10;


create table stk_report_header(
    id number(6),
    report_date varchar2(10), --20210802
    type varchar2(20), -- mystocks, allstocks, bks, mass
    realtime number(1),  --0,1
    name varchar2(200),
    status number(1),
    error_msg varchar2(1000),
    insert_time date
);
alter table stk_report_header add constraint pk_report_header_id primary key (id);
create index idx_report_header_date_type on stk_report_header (report_date,type);
create sequence s_report_header_id INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE CACHE 10;
alter table stk_report_header add checked_time date;


create table stk_report_detail(
    id number(6),
    header_id number(8),
    strategy_date varchar2(10),
    strategy_code varchar2(20),
    strategy_output varchar2(2000),
    code varchar2(10),
    rps_code  varchar2(100), --rps_01;rps_03
    rps_percentile  varchar2(100), --98.03;91.42
    rps_bk_code  varchar2(100), --BK0695;BK0696
    rps_stock_code  varchar2(1000), --600468,600622,000737,601069,600362,688456,601020,000975,002009,603399,600367,000603,002297,300340,300409
    text varchar2(2000)
);
alter table stk_report_detail add constraint pk_report_detail_id primary key (id);
alter table stk_report_detail
    add constraint fk_report_detail__header_id foreign key (header_id)
        references stk_report_header (id);
create sequence s_report_detail_id INCREMENT BY 1 START WITH 100000 NOMAXVALUE NOCYCLE CACHE 10;

alter table stk_report_detail modify rps_stock_code varchar2(2000);
alter table stk_report_detail add output_1 varchar2(2000);
alter table stk_report_detail add output_2 varchar2(2000);
alter table stk_report_detail add checked_time date;
alter table stk_report_detail add output_volume_highest varchar2(2000); --量能创历史新高
alter table stk_report_detail add output_down_longtime varchar2(2000); --从高点调整很久（大于24周/120天）

drop table stk_status;
create table stk_status(
  id number(10),
  code varchar2(10),
  valid number(1) default 1,
  type number(2),
  sub_type varchar2(10),
  quantity number(10),
  start_time date,
  end_time date,
  comments varchar2(1000),
  insert_time date,
  update_time date
);
create sequence s_status_id INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE CACHE 10;
alter table stk_status add constraint pk_status_id primary key (id);
create index idx_status_code on stk_status (code);



select * from tab where tname like 'STK_%';

----------------------------------
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
10	净资产收益�?financialratios59	12.00
20	净利润增长�?financialratios44	20.00
30	销售毛利率	financialratios36	30.00
35	销售净利率	financialratios32	20.00
40	主营收入增长�?financialratios43	20.00
50	应收账款周转�?financialratios3	5.00
60	现金流量比率	financialratios51	50.00
70	经现流净资产�?financialratios48	0.10
80	资产负债率	financialratios56		40.00
90	流动比率	financialratios1	2.00
100	净利润
200	预收账款
210	应收账款
300	经营现金�?
*/


----end

select * from stk_pe order by report_date desc;

declare
  today varchar2(8);
  totalPe number;
  totalPb number;
  midPb   number;
  midPe   number;
begin
  today := '20200309';
  select avg(pe_ttm) into totalPe from stk_kline where kline_date=today and pe_ttm is not null and pe_ttm>3 and pe_ttm<200;
  select avg(pb_ttm) into totalPb from stk_kline where kline_date=today and pb_ttm is not null and pb_ttm>0 and pb_ttm<30;
  select median(pb_ttm) into midPb from stk_kline where kline_date=today and pb_ttm is not null and pb_ttm>0 and pb_ttm<30;
  select median(pe_ttm) into midPe from stk_kline where kline_date=today and pe_ttm is not null and pe_ttm>3 and pe_ttm<200;
  update stk_pe set total_pe=totalPe,total_pb=totalPb,mid_pb=midPb,mid_pe=midPe where report_date=today;
end;
/





select * from tab where tname like 'STK%';
select * from stk_stat_data;

select * from stk_fn_type;
select * from stk_fn_type where market=1;
select * from stk_fn_data where code='300293' and type=303 order by fn_date desc;
select * from stk_industry a,stk_industry_type b where a.industry=b.id;
select * from stk_xueqiu_user ;
select * from stk_industry for update;

delete from stk_error_log;
select * from stk_error_log order by insert_time desc;
select * from stk_holder where code='002131' order by fn_date desc;
select * from stk_ownership where code='002131' order by fn_date desc,rate desc;

select count(1) from stk;--2464
select * from stk order by code;
select * from stk order by insert_time desc;
select * from stk where to_char(company_profile) like '%%';
select * from stk where code='8080).' for update;
select * from stk_industry where code='300350' for update;
update stk set status=1,status_date=sysdate where code='601933' and status <> 1;
select code,name,total_capital,cur_price,total_capital*cur_price from stk where code in ('000423','000425','000501','000538','000780','000880','000887','000937','002081','002082','002089','002128','002304','600028','600031','600036','600066','600067','600104','600123','600139','600153','600161','600195','600252','600271','600276','600309','600315','600327','600348','600395','600436','600508','600511','600519','600547','600561','600570','600582','600594','600600','600658','600729','600739','600742','600743','600745','600779','600805','600809','600816','600828','600829','600845','600875','600880','600887','600970','600971','601006','601166','601169','601328','601398','601666','601699','601988','000006','000012','000028','000069','000157','000338','000411','000417','000506','000527','000537','000540','000550','000568','000581','000596','000623','000631','000650','000651','000661','000671','000708','000718','000732','000792','000848','000858','000861','000869','000895','000900','000933','000963','000979','000983','000987','000999','002001','002007','002022','002024','002038','002051','002063','002065','002069','002091','002096','002104','002140','002146','002152','002153','002191','002223','002242','002244','002250','002252','002269','300003','600115','600216','600223','600225','600375','600406','600505','600660','600785','600794','600897','600983','601088','601111','601899','601939') order by total_capital*cur_price;
select * from stk where name like '%N%';
select * from stk where status=1 for update;
select * from stk_fn_type for update;

select count(1) from stk_fn_data;
select * from stk where code not in (select code from stk_fn_data);
select * from stk_fn_data order by code,fn_date desc;
select * from stk_fn_data where code='600706' order by fn_date desc;
select * from stk_import_info where code='600643';
select * from stk_import_info where id not in (
select * from stk_import_info where info like '%净利润%�?');
select * from stk_fn_data where code='002230' and type in (300) order by type desc,fn_date desc;
select * from stk_fn_type where market=1 and status=1 order by disp_order;



select * from stk_fn_data where fn_date in ('20121231') and fn_value is not null order by code asc,fn_date desc;
select * from stk_fn_data where fn_date like '%20111231' and fn_value>=5;--
select * from stk_fn_data where fn_date='20130331';
select * from stk_fn_data order by insert_time desc;

delete from stk_info_log where source='wind_yd';
select count(1) from stk_info_log;
select * from stk_info_log where code='002275';
select * from stk_info_log order by code;

select * from stk_error_log order by insert_time desc;
truncate table stk_error_log;
select count(1) from stk_error_log;
select * from STK_KLINE_US where kline_date;

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
select * from stk_holder where code='300359';
select * from stk_import_info_type for update;
select * from stk_import_info;
select * from stk_import_info where care_flag=1 order by code asc,id desc;

select sum(bytes)/(1024*1024) as "size(M)" from user_segments where segment_name=upper('stk_fn_data');
select table_name,cache from user_tables where instr(cache,'Y') > 0;

select * from stk_holder where code='300244' order by fn_date desc;


select * from stk where market=1 order by code desc;
select * from stk where code='002555';
select * from stk_fn_data where code='600587' and type=100 order by fn_date desc;
select * from stk_fn_type for update;
/*
毛利 = 总收�?- 成本总计
*/
insert into stk_fn_type select 3004,'现金净增减',null,null,null,12,1,2 from dual;
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
select * from stk where company_profile like '%多晶�?';
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
select * from stk_keyword_link where code_type=2;

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
select * from stk_text where title like '%伙伴资本看公�?';

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

select * from stk_ownership where fn_date is not null order by fn_date asc;
select count(1) from stk_ownership where fn_date is null;

select * from stk_data_ppi_type where name like '%氨纶%';
select distinct b.name,a.code,a.fn_date,a.stk_num,a.rate from stk_ownership a, stk b where a.code=b.code
and a.org_id in (56763,218922) order by fn_date desc,rate desc;
select * from stk_organization_type;
select * from stk_organization where name like '%李嘉%';
select * from stk_organization where name like '%泽熙%';--55541,55542,55547,64558,45511,45522,47311,47318,79473,79522
select * from stk_organization where name like '%赵建%';--56763,218922
select * from stk_organization where name like '%许磊%';--51575
select * from stk_organization where name like '%宋聿%';--45671
select * from stk_organization where name like '%濮文%';
select * from stk_organization where name like '%沈昌%';
select * from stk_organization where name like '%张利%';
select * from stk_organization where name like '%黄木%';--43029
select * from stk_organization where name like '%吴鸣%';--62505
select * from stk_organization where name like '%刘世%';--57768
select * from stk_organization where name like '%叶光%';--61694
select * from stk_organization where name like '%吴旗%';--46970
select * from stk_organization where name like '%韩常%';--47676
select * from stk_organization where name like '%景华%';
select * from stk_organization where name like '%何雪%';--62325

select * from stk_dictionary where type=20 for update;
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
select * from stk_dictionary where type=300 for update;
insert into stk_dictionary select 5,5964068708,'小小辛巴',null from dual;
insert into stk_dictionary select 5,3875738003,'可燃�?,null from dual;

select * from stk_text order by insert_time desc for update;
select * from stk_text where text like '%***+2%';
select * from stk_text where code='300338' order by insert_time desc;
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


select * from stk_pe where report_date>='20140227' order by id desc for update;
select * from stk_daily_report_us order by report_date desc;
select * from stk_kline_us where kline_date='20170711';

select * from stk_industry_type where source='my_industry_fntype';
select * from stk_industry where industry=1783;


ALTER PROFILE DEFAULT LIMIT PASSWORD_LIFE_TIME UNLIMITED;
select * from dba_profiles where profile='DEFAULT' and resource_name='PASSWORD_LIFE_TIME';
alter user system identified by system;

select * from v$process;
select value from v$parameter where name = 'processes';
alter system set processes = 300 scope = spfile;
shutdown immediate;

 --查看当前有哪些用户正在使用数�?
    SELECT osuser, a.username,cpu_time/executions/1000000||'s', sql_fulltext,machine
    from v$session a, v$sqlarea b
    where a.sql_address =b.address order by cpu_time/executions desc;

select * from stk_industry_type where source='csindex_zjh';
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
select * from stk_kline where code='600600' order by kline_date desc;
select * from stk_kline where code='999999' order by kline_date desc;
--delete from stk_kline where code='USDHKD';

select * from stk_data_eastmoney_guba order by insert_date desc;
select avg(num_click)/1000 click,sum(num_reply),insert_date from stk_data_eastmoney_guba group by insert_date order by insert_date desc;

select * from stk_capital_flow;
select round(avg(main_amount+super_large_amount),2) flow,flow_date from stk_capital_flow a,stk_cn b where a.code=b.code and b.TOTAL_CAPITAL<=50000 and main_amount!=0 and super_large_amount!=0 group by flow_date order by flow_date desc;

select * from stk_organization where name like '%中邮战略新兴产业股票%';

--查询十大股东
select * from stk_ownership a,stk_organization b where a.org_id=b.id and code='002587' order by fn_date desc;

select * from stk_text  order by insert_time desc;

select * from stk_fn_data where code='300024' and type=110 order by fn_date desc;
select * from stk_fn_type where market=1 order by type;
select * from stk_kline where code='300236' order by kline_date desc;

SELECT name,F_TRANS_PINYIN_CAPITAL(name) FROM stk_cn where code=002614;
SELECT case when market = 1 then name||','||F_TRANS_PINYIN_CAPITAL(name)||','||code when market = 2 then code end "activity",code val FROM stk order by code asc;


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

select * from stk_user for update;
select * from stk_fn_type where market=1;

select * from stk_fn_type where market=1 and disp_order <> 1000 order by disp_order asc for update;

--{table:'stk',sql:'',data:['','','']}

select * from stk_pe order by id desc;
select * from stk_text where id=10610675;

select * from stk_keyword order by id desc;
select * from stk_keyword_link order by id desc;
select * from stk_label;
select * from stk_label_text order by id desc;

select  from stk_text where id=10610672;
select * from STK_INDUSTRY_RANK ;

select * from stk_text order by id desc;
select * from stk_organization_type;
select count(1) from stk_capital_flow where flow_date>='20140401' and flow_date<='20140701';

select * from stk_sequence;

---
create table stk_sync_table(
  name varchar2(200),
  pk varchar2(200)
);
create table stk_sync_task(
  id number(8),
  name varchar2(200),
  task_start_time date,
  task_end_time date
);
alter table stk_sync_task add sync_start_time date;
alter table stk_sync_task add constraint pk_sync_task_id primary key (id);
create index idx_sync_task__name on stk_sync_task (name);



create table stk_sync(
  id number(8),
  task_id number(8) default 0,
  table_name varchar2(100),
  sql_type number(1), --1:insert, 2:update, 3:delete
  sql_text varchar2(2000),
  sql_params clob,
  status number(1) default 0, --0:initial,1:succ,2:fail
  error_msg clob,
  insert_time date,
  sync_time date
);
alter table stk_sync add constraint pk_sync_id primary key (id);
alter table stk_sync
  add constraint fk_sync__task_id foreign key (task_id)
  references stk_sync_task (id);
create index idx_sync__task_id on stk_sync (task_id);
create index idx_sync__table_name on stk_sync (table_name);
create index idx_sync__status on stk_sync (status);


create table sync_log(
  id number(8),
  insert_time date,
  log_text clob
);
alter table sync_log add constraint pk_sync_log_id primary key (id);

create table sync_test(
  id number(8),
  name varchar2(100),
  msg clob,
  insert_time date
);
insert into stk_sync_table select 'sync_test',null from dual;


select * from sync_test;
select * from stk_sync_table;
select * from stk_sync_task;
select * from stk_sync order by id desc;


select * from stk_cn a where (select count(1) from stk_kline b where b.code=a.code)=0;
select * from stk_kline where kline_date>=to_char(sysdate,'yyyyMMdd');
select * from stk_error_log order by insert_time desc;

select * from stk_monitor where trigger_date is null;
select * from stk_error_log order by insert_time desc;
truncate table stk_error_log;
select * from stk_error_log where code='999999' order by insert_time desc;
select * from stk_text order by id desc;
select * from stk_keyword order by insert_time desc;
select * from stk_text where code='000997';
select * from stk_keyword_link order by id desc for update;

select * from stk_pe order by report_date desc;
select * from stk_kline where code='399905' order by kline_date desc;

select * from stk_industry_type;
select * from stk_label for update;
select * from stk_label_text where label_id=100002;

select count(1) from stk_cn;
select * from stk_fn_type where market=1;

select a.code from stk a, stk_kline b where a.code=b.code and b.kline_date = '20140912' and b.percentage is not null order by b.percentage desc;

select * from stk_dictionary where type = 200 for update;

select * from stk_organization_type;
select * from stk_ownership where type=7 order by fn_date desc;
select * from stk_label where user_id=1 order by F_TRANS_PINYIN_CAPITAL(name);



select * from stk_dictionary where type=1000 for update;

select * from stk_index_node for update;
select * from stk_data_industry_pe

select * from stk where code='002572';
select * from stk_organization where name like '景顺长城精选蓝�?;
select * from stk_pe order by report_date desc for update;

select * from stk_industry_type where id=1767;
select * from stk_industry_type where source='cnindex';

with
t1 as (select pe_date,pe_ttm from stk_data_industry_pe where industry_id=1767 and type=1),
t2 as (select pe_date,pe_ttm from stk_data_industry_pe where industry_id=1767 and type=2),
t3 as (select pe_date,pe_ttm from stk_data_industry_pe where industry_id=1767 and type=3),
t4 as (select avg(pe_ttm) pe_ttm,pe_date from stk_data_industry_pe where industry_id=1767 group by pe_date)
select t1.pe_date d,t1.pe_ttm a,t2.pe_ttm b,t3.pe_ttm c,trunc(t4.pe_ttm,2) v from t1,t2,t3,t4 where t1.pe_date=t2.pe_date and t2.pe_date=t3.pe_date and t3.pe_date=t4.pe_date order by t1.pe_date asc;

select * from stk_data_industry_pe where industry_id=1767 order by pe_date ;
select avg(pe_ttm),pe_date from stk_data_industry_pe where industry_id=1767 group by pe_date order by pe_date ;

select * from stk_user for update;

select id,title,disp_order from stk_text where type=0 and user_id=2 order by disp_order desc,insert_time desc
select length(text),id from stk_text order by insert_time desc;
select * from stk_text where id=10610650;

select * from stk_industry where code='000997' and industry in (select id from stk_industry_type where source='my_industry_fntype')

select * from stk_text;
update stk_text set text=replace(text,'http://localhost:8888','');

select * from stk_label order by insert_time desc;
select * from stk_industry where industry=1826;

select sum(length(text))/1024/1024 from stk_text where insert_time >= to_date(to_char(sysdate,'yyyymm'),'yyyymm') and insert_time < to_date(to_char(add_months(sysdate,1),'yyyymm'),'yyyymm');
select length('中文test') from dual;
select to_date(to_char(add_months(sysdate,1),'yyyymm'),'yyyymm') from dual;

select * from stk_fn_type where market=1;
select * from stk_data_ppi_type where name='环氧丙烷';
select * from stk_data_ppi  order by ppi_date desc;

select * from stk_keyword  where name='磷肥';
select * from stk_keyword_link where keyword_id=101232;
select * from stk_index_node for update;

select * from (select * from stk_kline where code='999999' order by kline_date desc) where rownum<=1;
select * from stk_kline where code='300260' order by kline_date desc;

select * from stk_text order by insert_time desc;
select * from stk_kline where kline_date>=to_char(sysdate-2,'yyyymmdd');

select distinct source from stk_industry_type;
select * from stk_industry_type where source='my_industry_fntype' for update;
select * from stk_dictionary where type=400 for update;

select * from stk_label_text where label_id=100090 and exists (select 1 from stk_label where label_id=100090 and user_id=1);

select * from stk_data_ppi_type order by name asc;
select * from stk_data_ppi order by ppi_date desc;

select * from stk_text where id=20004501 for update;
select * from stk_text order by id desc;
select * from stk_text where text like '%一年期牛基%';
select * from stk_text where code is null;
select * from stk_text where text like '%股东人数减少%';
update stk_text set sub_type=50 where text like '%股东人数减少%' and type=1;

select * from stk_dept_type;
select * from stk_billboard;

select * from stk_monitor where type=1 and trigger_date is null order by insert_date asc
select * from stk_user for update;

select * from stk_import_info_type for update;
select * from stk_import_info where code='002060' order by id desc;
select * from stk_import_info where code='300208' and type=200 order by id desc for update;
select * from stk_import_info order by info_create_time desc;
select * from stk_import_info order by insert_time desc;
select * from stk_import_info where type=1 for update;

select * from stk_us a where a.code like 'N%';
select * from stk_us a, stk_kline_us b where a.code like 'FA%' and a.CODE=b.code and b.close=25.06;

select * from stk_cn where code='002747' for update;
select s_import_info_id.nextval from dual;

select * from stk_sequence;
insert into stk_import_info(id,code,type,insert_time,info,title,url_source,url_target) values
(s_import_info_id.nextval,'000001',140,sysdate,null,'平安银行首秀三季�?重组、逾期贷款集中江浙地区','http://www.windin.com/Tools/NewsDetail.aspx?windcode=000001.SZstart=end=pid=21ajax=','http://snap.windin.com/ns/findsnap.php?ad=0')

select count(1) from stk_cn;

select s_import_info_id.nextval from dual;

select * from stk_investigation where  investigator_count > 0;
select count(1) from stk_investigation;
select * from stk_investigation where code='300032';
select * from stk_investigation where investigator like '%泽熙%' order by invest_date desc;
select * from stk_investigation where investigator like '%重阳%' order by invest_date desc;
select * from stk_investigation where investigator like '%景顺长城%' order by invest_date desc;

select * from stk_cn where code='002750';
select * from stk where cate=2;

select * from stk_organization_type;
select * from stk_earnings_forecast order by insert_time desc;

select code from stk_cn where code not in (select code from stk_kline where kline_date=to_char(sysdate,'yyyymmdd'));
select count(1) from stk_cn s, stk_kline k where s.code=k.code and k.kline_date='20150413';

select * from stk_kline where code in ('999999','002038','002002') and kline_date='20150312';
select * from stk_capital_flow order by flow_date desc;

select * from stk_kline k where k.kline_date='20150323' and k.close=20.66;
select * from stk_kline k where k.code='01000905' order by kline_date desc;

select * from stk_report_daily order by insert_time desc;
select count(1) from stk_cn;

select * from stk_fn_data where type=111 and fn_date='20141231' and fn_value>300;
select * from stk_text where code in ('900940','200056') for update;

select * from stk where code='603386';
update stk set hot =0 where hot is null;
select * from stk_us where hot is null;
select * from stk_kline where code='603386' order by kline_date desc;


select * from stk_kline_us where code='NOAH' order by kline_date desc;
select count(1) from stk_kline_us where kline_date='20150424';
select * from stk_industry;
select * from stk_industry_type where source='sina_meigu' and name like '%�?';
select * from stk_industry_type where name like '%�?';

select * from stk_industry_type where source='qq_conception';
select * from stk_industry i,stk_industry_type t where i.industry=t.id and t.source='qq_conception' and i.code='603126';

select code,name from stk_us where market=2 and code>0 order by code;

select * from stk_cn where hot > 0 order by hot desc;

select distinct sub_type from stk_text;
select * from stk_text where code='002094' order by id desc;

select * from stk_capital_flow where code='601818' order by flow_date desc;
select round((main_percent+super_large_percent)/2,2) from stk_capital_flow where code='601818' and flow_date='20150605' order by flow_date desc;

select * from stk_organization where name like '罗斌%'; -- 风生水起
select * from stk_organization order by id desc;
update stk_organization set name = replace(name,' ','');
select * from stk_ownership where code='002587' order by fn_date desc;
select * from stk_ownership a,stk_organization b where a.org_id=b.id and code='002587' order by fn_date desc;
select * from stk_ownership where org_id=206895;

select * from stk_dictionary for update;

select * from stk_import_info_type where type < 100 order by type;
select * from stk_import_info_type order by type for update;
update stk_import_info_type set not_match_pattern=replace(not_match_pattern,',',';')
select * from stk_import_info where code='300032' order by insert_time desc for update;

select * from stk_organization where name like '%泽熙%';
select distinct b.name,a.code,a.fn_date,a.stk_num,a.rate,o.name from stk_ownership a,stk_organization o, stk b
where a.code=b.code and a.org_id = o.id
  and o.name like '%社保%' order by fn_date desc,rate desc;

select * from stk_pe order by id desc for update;

select * from stk_care for update;
select * from stk_capital_flow where code='601336' order by flow_date desc;

select * from stk_fn_data where code='002119' and type=210 order by fn_date desc;
select * from stk_fn_type where type=210;

select * from stk where code='002781';
select * from stk_dictionary where type=300 for update;


select * from stk_monitor where type =2 order by param_5 asc;
select s.name,m.* from stk_monitor m,stk s where m.code=s.code and m.type=2 and m.code in ('600089','600239','600321','600330','600562','600571','600673','600706','600745','600818','601929','603618','000058','000063','000158','000413','000417','000425','000430','000506','000507','000509','000536','000543','000544','000551','000571','000581','000593','000615','000636','000657','000681','000687','000701','000710','000726','000727','000790','000798','000831','000837','000901','000911','000918','000925','000926','000939','000948','000955','000973','600963','000090','000415','000523','002006','002009','002010','002016','002022','002029','002034','002042','002045','002050','002055','002060','002063','002077','002085','002093','002111','002114','002118','002123','002125','002135','002156','002160','002169','002172','002181','002182','002193','002197','002218','002264','002274','002279','002280','002281','002298','002305','002317','002321','002322','002327','002330','002331','002335','002344','002349','002356','002357','002361','002363','002364','002379','002382','002397','002408','002422','002431','002437','002439','002441','002442','002447','002448','002449','002453','002476','002483','002485','002486','002488','002502','002505','002508','002511','002513','002520','002532','002536','002538','002539','002551','002562','002567','002579','002580','002597','002603','002609','002610','002611','002612','002618','002626','002631','002632','002634','002640','002644','002645','002656','002659','002674','002679','002680','002683','002684','002688','002690','002695','002698','002729','002733','002739','002750','002751','002756','000592')
order by param_5 asc;
select * from stk_error_log;

select * from stk_earnings_forecast where forecast_net_profit=0 ;
select s.name,e.* from stk_earnings_forecast e,stk s where e.code=s.code and e.forecast_year=2017 and s.code='002587' order by pe asc;
select * from stk_us where code like 'N%' and hot<100;
select * from stk_earnings_forecast where code='002587';

select s.code,s.name,"2015_pe"/(("2015_np"-"2014_np"+0.00001)/"2014_np")/100 "2015_peg" from stk s,(
select distinct ef.code,
  (select a.forecast_net_profit from stk_earnings_forecast a where a.code = ef.code and a.forecast_year=2014) "2014_np",
  (select a.pe from stk_earnings_forecast a where a.code = ef.code and a.forecast_year=2014) "2014_pe",
  (select a.forecast_net_profit from stk_earnings_forecast a where a.code = ef.code and a.forecast_year=2015) "2015_np",
  (select a.pe from stk_earnings_forecast a where a.code = ef.code and a.forecast_year=2015) "2015_pe",
  (select a.forecast_net_profit from stk_earnings_forecast a where a.code = ef.code and a.forecast_year=2016) "2016_np",
  (select a.pe from stk_earnings_forecast a where a.code = ef.code and a.forecast_year=2016) "2016_pe",
  nvl((select a.forecast_net_profit from stk_earnings_forecast a where a.code = ef.code and a.forecast_year=2017),0) "2017_np",
  nvl((select a.pe from stk_earnings_forecast a where a.code = ef.code and a.forecast_year=2017),0) "2017_pe"
from stk_earnings_forecast ef where ef.forecast_net_profit>0) e where s.code=e.code/* and s.code='300267' */
order by "2015_peg" desc;

select * from stk_earnings_forecast where code=002118;
select * from stk_cn order by hot desc;
select * from stk_us  order by insert_time desc;

select avg(pe_ttm),count(1) from stk_kline where kline_date='20150615' and pe_ttm is not null and pe_ttm>3 and pe_ttm<200;
select avg(pe_ttm),count(1) from stk_kline where kline_date='20150814' and pe_ttm is not null and pe_ttm>3 and pe_ttm<200;
select avg(pb_ttm),count(1) from stk_kline where kline_date='20150817' and pb_ttm is not null and pb_ttm>0 and pb_ttm<30;
select avg(pe_ttm),count(1) from stk_kline_us where kline_date='20150727' and pe_ttm is not null and pe_ttm>3 and pe_ttm<200;
select * from stk_daily_us;
select * from stk_kline where kline_date='20180906' order by pb_ttm;
select pb_ttm,rownum from stk_kline where kline_date='20150803' and pb_ttm is not null and pb_ttm>0 and pb_ttm<30 order by pb_ttm;
select median(pb_ttm),count(1) from stk_kline where kline_date='20180912' and pb_ttm is not null and pb_ttm>0 and pb_ttm<30;
select median(pe_ttm),count(1) from stk_kline where kline_date='20180905' and pe_ttm is not null and pe_ttm>3 and pe_ttm<200;

select * from stk_internet_search where status=1 order by search_source for update;
select * from stk_trans_account order by id desc for update;

select * from stk_data_eastmoney_guba order by insert_time desc;

select * from stk_dept_type  order by dept_id desc;
select distinct code from stk_billboard where trans_date='20150916' order by code asc, trans_date desc,buy_sell,seq asc;
select count(1) from stk_billboard;
select * from stk_billboard where code='600634';

select sum(buy_amount)-sum(sell_amount) from stk_billboard where trans_date='20150916';
select trans_date,trunc(sum(buy_amount)-sum(sell_amount)) from stk_billboard
group by trans_date order by trans_date desc;

select trans_date,trunc(sum(buy_amount)-sum(sell_amount)) from stk_billboard where dept_id=127
group by trans_date order by trans_date desc;
select trans_date,trunc(sum(buy_amount)-sum(sell_amount)) from stk_billboard where dept_id=1027
group by trans_date order by trans_date desc;--国泰君安证券股份有限公司上海福山路证券营业部：泽熙基金惯用营业部

select trans_date,trunc(sum(buy_amount)-sum(sell_amount)) from stk_billboard where dept_id in (485,1027,7597)
group by trans_date order by trans_date desc;
select trans_date,trunc(sum(buy_amount)-sum(sell_amount)) from stk_billboard where dept_id in (7597)
group by trans_date order by trans_date desc;

select to_char(to_date('20081121','yyyymmdd'),'yyyyww') from dual;

select to_char(to_date(trans_date,'yyyymmdd'),'yyyyww'),trunc(sum(buy_amount)-sum(sell_amount)) from stk_billboard
--where dept_id in (1027,7608)
where dept_id in (485,1027,7597)
group by to_char(to_date(trans_date,'yyyymmdd'),'yyyyww') order by to_char(to_date(trans_date,'yyyymmdd'),'yyyyww') desc;

select * from stk_dept_type where dept_name like '%杭州四季路证券营业部%';
select * from stk_dept_type where dept_id=1027;

select dept_id,trans_date,trunc(sum(buy_amount)-sum(sell_amount)) amount from stk_billboard
where trans_date >='20150915' and trans_date <='20150930'
group by trans_date,dept_id having trunc(sum(buy_amount)-sum(sell_amount)) >0 and count(dept_id)>10
order by trans_date desc;

select * from stk_industry_type where source='qq_conception';
select * from stk_industry where industry in (select id from stk_industry_type where source='qq_conception');

--资金流入股，可以用来分析板块，抓到启动最强板�?-- step 1: 资金流入�?
select s.code,s.name,f.flow_date,f.main_amount,f.main_percent+f.super_large_percent
from stk_capital_flow f, stk_cn s, stk_kline k
where f.code=s.code and f.code=k.code and f.flow_date=k.kline_date
and k.open!=k.close and k.open!=k.high
and f.main_percent+f.super_large_percent>=20
and f.flow_date='20150916'
order by f.flow_date,f.main_percent+f.super_large_percent desc;

--资金流入股，可以用来分析板块，抓到启动最强板�?-- step 2: 可以用来分析板块
select z.ind_name,count(z.ind_name) from (
select s.code,s.name,f.flow_date,f.main_amount,f.main_percent+f.super_large_percent, t.name ind_name, t.source
from stk_capital_flow f, stk_cn s, stk_kline k, stk_industry i, stk_industry_type t
where f.code=s.code and f.code=k.code and f.flow_date=k.kline_date
and k.open!=k.close and k.open!=k.high and s.CODE = i.code and i.industry=t.id
and f.main_percent+f.super_large_percent>=25
and f.flow_date>='20151210' and f.flow_date<='20151214'
order by f.flow_date,f.main_percent+f.super_large_percent desc) z
where z.ind_name not in ('融资融券','转融�?,'再融�?,'深成500','沪股�?,'沪深300','中证500','大盘','上证180',
'参股金融','股权激�?,'定向增发','资本货物','高价','基金增仓','重组概念','重组并购','券商重仓','社保重仓','新股改革')
and z.source in ('qq_conception','hexun_conception')
group by z.ind_name having count(z.ind_name)>=5 order by count(z.ind_name) desc;

--资金流入股，可以用来分析板块，抓到启动最强板块及个股 -- step 3: 抓到启动最强板�?-> 电气部件与设�?-> 充电�?
select distinct s.code,s.name,f.flow_date,f.main_amount,f.main_percent+f.super_large_percent, t.name ind_name, s.HOT
from stk_capital_flow f, stk_cn s, stk_kline k, stk_industry i, stk_industry_type t
where f.code=s.code and f.code=k.code and f.flow_date=k.kline_date
and k.open!=k.close and k.open!=k.high and s.CODE = i.code and i.industry=t.id
and f.main_percent+f.super_large_percent>=25 and s.TOTAL_CAPITAL <= 50000
and f.flow_date>='20151210' and f.flow_date<='20151214'
and t.name in ('股权投资','原材�?,'云计�?,'机械、设备、仪�?,'金属、非金属')
order by f.main_percent+f.super_large_percent desc;

--step 4: 再从选出的个股里查看最强概念板�?
select t.name,count(t.name) from stk_industry i, stk_industry_type t
where i.industry=t.id and i.code in (
select distinct s.code
from stk_capital_flow f, stk_cn s, stk_kline k, stk_industry i, stk_industry_type t
where f.code=s.code and f.code=k.code and f.flow_date=k.kline_date
and k.open!=k.close and k.open!=k.high and s.CODE = i.code and i.industry=t.id
and f.main_percent+f.super_large_percent>=25 and s.TOTAL_CAPITAL <= 50000
and f.flow_date>='20150914' and f.flow_date<='20150918'
and t.name in ('机械、设备、仪�?,'电子商务','云计�?,'信息技术业','锂电�?))
group by t.name having t.name not in ('融资融券','转融�?,'再融�?,'深成500','沪股�?,'沪深300','中证500','大盘','上证180',
'参股金融','股权激�?,'定向增发','资本货物','高价','基金增仓','重组概念','重组并购','券商重仓','社保重仓','新股改革')
order by count(t.name) desc;



--资金流入
select * from stk_capital_flow where code='300417' order by flow_date desc;

select code,type,fn_date,fn_value from stk_fn_data where code=601908  and fn_value is not null order by fn_date desc
select s.* from stk_industry s,stk_industry_type t where s.industry=t.id and s.code='000002'
order by s.industry,t.source;

select * from stk_industry_type order by id desc;
select count(1) from stk_industry order by industry desc;

select * from stk_keyword;
select * from stk_keyword_link;
select * from stk_word order by type,sub_type;
select * from stk_word for update;

insert into stk_word select s_word_id.nextval,1,1,'增长',1,null,null,null from dual;
insert into stk_word select s_word_id.nextval,3,null,'物流',1,null,null,null from dual;


--资金流入股，可以用来分析板块，抓到启动最强板�?-- step 1: 分析板块
select z.ind_name,count(z.ind_name) from (
select s.code,s.name,f.flow_date,f.main_amount,f.main_percent+f.super_large_percent, t.name ind_name, t.source
from stk_capital_flow f, stk_cn s, stk_kline k, stk_industry i, stk_industry_type t
where f.code=s.code and f.code=k.code and f.flow_date=k.kline_date
and k.open!=k.close and k.open!=k.high and s.CODE = i.code and i.industry=t.id
and f.main_percent+f.super_large_percent>=25
and f.flow_date>='20160105' and f.flow_date<='20160111'
order by f.flow_date,f.main_percent+f.super_large_percent desc) z
where z.ind_name not in ('融资融券','转融�?,'再融�?,'深成500','沪股�?,'沪深300','中证500','大盘','上证180',
'参股金融','股权激�?,'定向增发','资本货物','高价','基金增仓','重组概念','重组并购','券商重仓','社保重仓','新股改革',
'证金持股','汇金持股','股权投资','广东','北京','浙江','业绩预升','金融参股','上证380','金融改革','预盈预增','小盘')
and z.source in ('qq_conception','hexun_conception')
group by z.ind_name having count(z.ind_name)>=5 order by count(z.ind_name) desc;

--资金流入股，可以用来分析板块，抓到启动最强板块及个股 step 2: 抓到启动最强板�?
select t.name,count(t.name) from stk_industry i, stk_industry_type t
where i.industry=t.id and i.code in (
select distinct s.code
from stk_capital_flow f, stk_cn s, stk_kline k, stk_industry i, stk_industry_type t
where f.code=s.code and f.code=k.code and f.flow_date=k.kline_date
and k.open!=k.close and k.open!=k.high and s.CODE = i.code and i.industry=t.id
and f.main_percent+f.super_large_percent>=25 and s.TOTAL_CAPITAL <= 50000
and f.flow_date>='20160105' and f.flow_date<='20160111'
and t.name in ('成渝特区','电子商务','信息技术业','地方国资整合','机械、设备、仪�?))
group by t.name having t.name not in ('融资融券','转融�?,'再融�?,'深成500','沪股�?,'沪深300','中证500','大盘','上证180',
'参股金融','股权激�?,'定向增发','资本货物','高价','基金增仓','重组概念','重组并购','券商重仓','社保重仓','新股改革',
'证金持股','汇金持股','股权投资','广东','北京','浙江','业绩预升','金融参股','上证380','金融改革','预盈预增','小盘')
order by count(t.name) desc;

--资金流入股，可以用来分析板块，抓到启动最强板块及个股 -- step 3: 抓到启动最强个�?
select distinct s.code,s.name,f.flow_date,f.main_amount,f.main_percent+f.super_large_percent, t.name ind_name, s.HOT, trunc(s.TOTAL_CAPITAL/10000,2) captial
from stk_capital_flow f, stk_cn s, stk_kline k, stk_industry i, stk_industry_type t
where f.code=s.code and f.code=k.code and f.flow_date=k.kline_date
and k.open!=k.close and k.open!=k.high and s.CODE = i.code and i.industry=t.id
and f.main_percent+f.super_large_percent>=25 and s.TOTAL_CAPITAL <= 50000
and f.flow_date>='20160105' and f.flow_date<='20160111'
and t.name in (select name from stk_word where type=3)
--and t.name in ('云计�?,'4G概念','智慧城市','大数�?,'锂电�?,'健康中国')
--and s.code in ('002295','000683','002455','600614','002474','001696','002079','002576','600685','600326','002276','600435','300161','600378','603021','002552','000561','300346','000547','300337','002542','002527','002329','002363','002309','002581','002397','600316','600282','600017','601886','300221','002253','000599','000893','600829','002522','000961','002686','002113','600062','600303','002007','000068','300267','000411','600711','002073','600255','002444','002171','002050','000919','002118','002448','002498','600436','002395','002242','002669','002665','300248','002031','300029','603111','002258','600483','600706','603123','300086','300427','000585','000409','601677','002417','002130','600079','600339','000937','300136','002475','002611','600398','002123','300390','000985','600678','601699','002451','600008','300229','002048','300376','000554','600425','002421','600986','300400','300378','002035','600486','300340','002195','300026','600322','600642','002740','300166','300462')
order by f.main_percent+f.super_large_percent desc;

select code,name from stk where market=1 and cate=2 order by code;

select * from stk_ownership where org_id in (select id from stk_organization where name like '中邮%')
 and code='300468' order by fn_date desc and fn_date='20150930';

select * from stk_ownership where code='300468'


select * from stk_organization where name like '中邮%';

select * from stk_import_info where title like '%中科招商%';
select * from stk_import_info where type>=100 and type not in (190) order by info_create_time desc,insert_time desc
select * from (select * from stk_pe a where report_date<>'20151203' order by report_date desc) where rownum=1;
select count(1) from stk_import_info where type>=100 order by info_create_time desc


select * from stk_industry_type where name='虚拟现实';
insert into stk_industry_type(id,name,source) values(s_industry_type_id.nextval,'虚拟现实','hexun_conception');

select next_quarter_earning from stk where code='002301';
select * from stk_capital_flow where code in (select code from stk where cate=4) order by flow_date desc;


select * from stk_capital_flow where code='885598' ;
update stk_capital_flow set flow_date='20160816' where code in (select code from stk where cate=4) and flow_date != '20160815';

select * from stk where code='885640';
select * from stk_kline k where k.code='885598' order by kline_date desc;
select * from stk_kline k where k.code='399006' order by kline_date desc;

select * from (select * from stk_pe order by report_date desc) a where rownum=1
select * from stk_kline where code='300431' order by kline_date desc;
select kline_date from (select kline_date from stk_kline where code='999999' order by kline_date desc) where rownum=1

select * from stk where market=1 and cate=2;
select * from stk where code like '99%';
select * from stk_kline where code='999999' order by kline_date desc;

select * from stk_kline where code='000001' order by kline_date desc;
delete from stk_kline where code='999999' and kline_date>='20151221';

select * from stk_fn_data where code='300190' order by fn_date desc;
select distinct source from stk_industry_type;
select * from stk_industry_type order by id desc;
select * from stk_industry where industry=117132;
delete from stk_kline k where k.code in (
select code from stk where market=1 and cate=4 ) and k.close is null;

select * from stk_data_industry_pe order by insert_time desc;

select a.name,b.close-b.open from stk a, stk_kline b where a.code=b.code and a.market=1 and a.cate=4 and b.kline_date='20160613';

select * from stk_kline_us where code='TWER' order by kline_date desc;
select * from stk_cn where code='603309' for update;

select * from stk_import_info where type=21 and code='300327';
select * from stk_restricted where code='300212' order by listing_date desc;
select * from stk_import_info where code='300212' and type=190 order by insert_time desc;


select * from stk_fn_type where market=1 and disp_order <> 1000 order by disp_order asc for update;

select * from (select * from stk_import_info where code='002119' and type>=100 and id<168228 order by info_create_time desc) where rownum=1;


select * from stk_ownership order by code, fn_date desc, rate desc;
select * from stk_organization order by name;
select * from stk_monitor order by insert_date desc;
select * from stk_kline where code='300015' order by kline_date desc for update;

select * from stk_capital_flow where code='300379' order by flow_date desc;

select * from stk where code='603611' for update;
select * from stk where next_earning >= 100;

select * from stk_earnings_notice where code='600487';
select * from stk_fn_type;
select * from stk_fn_data where code='600487' and type=303 order by fn_date desc;

select s.name,e.* from stk_earnings_notice e, stk s where e.code=s.code
and e.er_high>=100 and e.fn_date in ('20160930') and e.last_amount>0;

select * from stk_dictionary where type=1005 order by type desc,to_number(param) asc for update;
select * from stk_user for update;

select source, count(1) from stk_industry_type group by source;
select * from stk_industry i,stk_industry_type t where i.code='600497' and i.industry=t.id;

select * from stk_kline where code='01000905' order by kline_date desc;
select * from stk_kline where code='399905' order by kline_date desc;
select * from stk_kline where code='300246' order by kline_date desc;

select * from stk_capital_flow where code='603067' order by flow_date desc;
select * from stk_capital_flow where super_large_percent>large_percent
and large_percent>middle_percent and middle_percent>small_percent and small_percent>=0
 order by flow_date desc;

select * from stk where cate=2;
select report_date, result_3, result_4, result_5 from stk_pe order by report_date desc for update;


select distinct b.kline_date "date",
                b.kline_date,
                a.average_pe "pe",
                b.close "value",
                a.bias,
                a.ene_upper_cnt eneupper,
                a.ene_lower_cnt enelower,
                c.flow flowlarge,
                d.flow flowsmall,
                e.close sh180,a.result_3,a.result_4,a.result_5
  from stk_kline b
  left join stk_pe a on b.kline_date = a.report_date
  left join (select main_amount flow,
                    flow_date
               from stk_capital_flow a, stk_cn b
              where a.code = b.code
                and b.TOTAL_CAPITAL >= 100000
                and main_amount != 0) c on b.kline_date = c.flow_date
  left join (select round(avg(main_amount), 2) flow,
                    flow_date
               from stk_capital_flow a, stk_cn b
              where a.code = b.code
                and b.TOTAL_CAPITAL <= 50000
                and main_amount != 0
                and super_large_amount != 0
              group by flow_date) d on b.kline_date = d.flow_date
  left join stk_kline e on b.kline_date = e.kline_date
                       and e.code = '01000010'
 where b.code = '01000905'
   and b.kline_date >= '20130520'
 order by b.kline_date desc;

select * from stk_earnings_forecast where code='300121' order by insert_time desc;
select * from stk_pe order by report_date desc for update;

select * from stk_industry_type where name like '%180';
select * from stk_industry where industry = 117132;

select * from stk_restricted;

select * from stk_kline_us where code='DIG' order by kline_date desc;

select * from stk_kline_us order by kline_date desc;

select * from stk_industry_type where source='easymoney_meigu';
select distinct source from stk_industry_type;
select * from stk_industry;
select * from stk_industry where industry=885739;

select code,name,hot,cate from stk_us order by code ;
select * from stk where market=2 and cate!=1;
select code,name from stk where market=2 and cate=2 order by code;

select avg(pe_ttm) from stk_kline_us where kline_date='20161014' and code in (select code from stk_industry where industry=885739) and pe_ttm is not null and pe_ttm>3 and pe_ttm<200;

select kline_date,avg(pe_ttm),count(1) from stk_kline_us
where code in (select code from stk_industry where industry=885739) and pe_ttm is not null and pe_ttm>3 and pe_ttm<200 group by kline_date order by kline_date desc;

select * from stk_daily_report_us for update;

select pb_ttm,code from stk_kline where code in (select code from stk_industry where industry=117132)
and pe_ttm is not null and pb_ttm>0 and pb_ttm<10 and kline_date='20161102';

select * from (
select avg(pb_ttm),round(avg(pb_ttm),2),kline_date,count(1) from stk_kline k, stk_industry i where k.code=i.code and i.industry=117132
and k.pb_ttm is not null and pb_ttm>0 and pb_ttm<10 group by k.kline_date ) order by kline_date desc;

select * from (
select avg(pe_ttm),round(avg(pe_ttm),2),kline_date,count(1) from stk_kline k, stk_industry i where k.code=i.code and i.industry=117132
and k.pe_ttm is not null and pe_ttm>0 and pe_ttm<30 group by k.kline_date ) order by kline_date desc;


select * from stk_us where name like '%ETF%';
select * from stk_us where name like '%柬埔�?';

select * from stk_monitor;
select * from stk_import_info_type for update;

select * from stk_internet_search;

select * from stk_kline where code='601718' order by kline_date desc;
select code from stk_cn where code not in (select code from stk_kline where kline_date=to_char(sysdate,'yyyymmdd'));


select * from stk_xueqiu_user where name='gdlz';
select count(1) from stk_xueqiu_user;

select avg(pe_ttm),count(1) from stk_kline_us where kline_date='20170110' and pe_ttm is not null and pe_ttm>3 and pe_ttm<200;
select avg(pe_ttm),kline_date from stk_kline_us where pe_ttm is not null and pe_ttm>3 and pe_ttm<200 group by kline_date order by kline_date desc;

select * from stk_daily_report_us order by report_date desc;

select (k.hsl*s.TOTAL_CAPITAL)/100 from stk_kline k, stk_cn s where k.code=s.CODE and kline_date='20170112';

--上证换手�?
select k.kline_date,sum(k.hsl*s.TOTAL_CAPITAL)/sum(s.TOTAL_CAPITAL),count(k.kline_date) from stk_kline k, stk_cn s
where k.code=s.CODE and k.hsl is not null and s.code >=600000 and s.CODE<602000
 group by k.kline_date order by k.kline_date desc;

select k.kline_date,avg(k.pb_ttm),count(1) from stk_kline k, stk_cn s
where k.code=s.CODE and k.pb_ttm is not null and pe_ttm>3 and pe_ttm<200
 and s.code >=600000 and s.CODE<602000
 group by k.kline_date order by k.kline_date desc;

 select avg(pb_ttm),kline_date,count(1) from stk_kline
 where pb_ttm is not null and pe_ttm>3 and pe_ttm<200
  and code>=600000 and CODE<602000
 group by kline_date
 order by kline_date desc;

select * from stk_earnings_notice where code='000826';

select * from stk_earnings_notice where fn_date='20170331' and code = '300412' and last_amount>0 order by insert_time desc

select * from stk_fn_data order by fn_date desc;

select * from stk_user;

select substr(listing_date,0,7) from stk_restricted where code='002743' order by report_date desc;
select a.*,substr(a.listing_date,0,7) m_date,k.close from stk_restricted a, stk_kline k where a.code=k.code and k.kline_date='20170203';

select m_date,sum(ban_amount) from (select a.*,substr(a.listing_date,0,7) m_date from stk_restricted a) r
group by m_date order by m_date desc;

--未来解禁总市�?- 判断大盘高低�? https://xueqiu.com/1652627245/80263715
select m_date,round(sum(ban_amount*close),0) from (select a.*,substr(a.listing_date,0,7) m_date,k.close from stk_restricted a, stk_kline k where a.code=k.code and k.kline_date='20170203') r
group by m_date order by m_date desc;

select * from stk_ownership order by fn_date desc;

select code,name from stk where market=1 and cate=2 and name='昨日换手前十';

select s.name, e.* from stk_earnings_notice e , stk_cn s
where e.code=s.code and e.real_date is not null and e.real_date<='20170415' order by e.real_date desc;


select * from (
select k.*,(s.total_capital*k.close)/10000 mv from stk_kline k, stk s,
(select max(kline_date) kdate from stk_kline where code in ('01000852','399006')) d
where k.code=s.code and k.kline_date = d.kdate) c
left join
(select a.* from stk_earnings_notice a,
(select code,max(fn_date) fn_date from stk_earnings_notice group by code) b
where a.code=b.code and a.fn_date=b.fn_date) e on c.code=e.code
where c.code='002849'
;


select * from stk_earnings_notice where fn_date='20161231' and er_low >= 100 and er_high >= 50 and notice_date >= 20160921
and notice_date <= 20170328 and code='002106';

select * from stk_earnings_notice where (code, fn_date) in (select code,max(fn_date) from stk_earnings_notice where 1=1
and code in ('300415','600479','002196','002182','300484','300246','002136') group by code) order by insert_time desc
action=com.stk123.web.action.EarningAction,method=getEarningNotice

select * from (
select * from ( select k.*,(s.total_capital*k.close)/10000 mv from stk_kline k, stk s, (select max(kline_date) kdate
from stk_kline where code in ('01000852','399006')) d where k.code=s.code and k.kline_date = d.kdate) c left join (select a
.* from stk_earnings_notice a, (select code,max(fn_date) fn_date from stk_earnings_notice group by code) b where a.code=
b.code and a.fn_date=b.fn_date) e on c.code=e.code)  where pe_ttm >= 50 and pe_ttm <= 54


select * from stk_fn_data a,
(select code,max(fn_date) fn_date from stk_fn_data where type=111 group by code) b
where a.type=111 and a.code=b.code and a.fn_date=b.fn_date;


select * from stk_industry_type;
select * from stk_fn_data where code='000527' and type=111 order by fn_date desc;

select * from stk where cate=2 and address='10jqka_gn';
select * from stk_kline where code='600265' order by kline_date desc;


select * from stk_capital_flow where code='885611' order by flow_date desc;

select *　from stk_earnings_notice where code='300450';

select * from stk_earnings_notice where fn_date='20170331' and er_low >= 50 and er_high >= 50
and notice_date >= 20170220 and notice_date <= 20200201 order by insert_time desc;

select * from stk_text where code='60006' for update;
update stk_text set text=replace(text,'[股东人数减少]','') where sub_type=10;
select * from stk_dictionary where type=400 for update;

insert into stk_text(id,type,code,code_type,title,text,insert_time,update_time,sub_type)
values (s_text_id.nextval,2,'000826',1,'启迪桑德研究报告：海通证�?启迪桑德-000826-公司研究报告：环境综合服务龙头，进军环卫更上层楼-170308',null,sysdate,null,100)
select * from stk_text where code='000688' and type!=3 order by insert_time desc;

select * from stk_text where sub_type=110 order by insert_time desc;

select * from stk_earnings_notice where code='300450' order by fn_date desc;
select er_low from stk_earnings_notice e, (select max(fn_date) fdate from stk_earnings_notice where code='300450') n where e.fn_date=n.fdate and e.code='300450' ;

select * from stk_pe order by report_date desc for update;

select * from stk_fn_type for update;

select * from stk_kline where open=0;
select * from stk_holder order by fn_date desc;

select * from stk_earnings_notice order by notice_date desc;

select s.name, e.* from stk_earnings_notice e , stk_cn s
where e.code=s.code and e.real_date is not null and e.notice_date<='20170415' order by e.notice_date desc;

select * from stk_text where sub_type=110 order by insert_time desc;

--delete from stk_text where Id in(select max(Id) from stk_text where sub_type=110 group by Title having COUNT(*) > 1);
--commit;

select * from stk where code='000728';
select count(*) from stk_cn where hot>10000;
select * from stk_hk where hot>1000;

select * from (
select code,name from stk_cn a where hot>3000 and not exists (select 1 from stk_cn b where a.code=b.code and b.name like '%ST%')
union all
select code,name from stk_hk where hot>1000 ) order by reverse(code)

select * from stk_earnings_forecast where code='002536' order by forecast_year desc;
select * from stk_dictionary where type=1005 for update;


select * from stk_earnings_notice where (code, fn_date) in (select code,max(fn_date) from stk_earnings_notice where 1=1
and code in ('002106') group by code) order by insert_time desc;



select * from ( select * from ( select k.*,(s.total_capital*k.close)/10000 mv from stk_kline k, stk s, (select max(kline_date) kdate from stk_kline where code in ('01000852','399006')) d where k.code=s.code and k.kline_date = d.kdate) c
left join (select a.* from stk_earnings_notice a, (select code,max(fn_date) fn_date from stk_earnings_notice group by code)
 b where a.code=b.code and a.fn_date=b.fn_date) e on c.code=e.code) where 1=1  and pe_ttm >= 30 and pe_ttm <= 35 and mv
>= 50 and mv <= 100 and code='002106';

select * from stk_fn_data where code='002106';


select *
  from (select code,
               type,
               fn_date,
               fn_value,
               ROW_NUMBER() over(PARTITION by type order by fn_date desc) as num
          from stk_fn_data where code='000997') t
 where t.num = 1;


select * from stk_earnings_notice where (code, fn_date) in (select code,max(fn_date) from stk_earnings_notice where 1=1
and code in ('300415','600479','002196','002182','300484','300246','002106','603168','300253','002449','300429','002195'
,'300306','600742','300497','002376','002536','002106','000997') group by code) order by insert_time desc


select *
  from stk_earnings_notice e
  left join (select t.fn_value,t.type,t.code
    from (select s.*,
                 ROW_NUMBER() over(PARTITION by code,type order by fn_date desc) as num
            from stk_fn_data s ) t
       where t.num = 1) fn on e.code=fn.code and fn.type=109
 where (e.code, e.fn_date) in
       (select code, max(fn_date)
          from stk_earnings_notice
         where 1 = 1
           and code in ('000997')
         group by code)
 order by insert_time desc;


select * from stk where market=3;
select * from stk_fn_data where code='002139' and type=101 order by fn_date desc;
select * from stk_fn_type where market=1 and source=2 order by disp_order asc, tab asc for update;
select * from stk_fn_type where market=1 for update;

select * from stk_fn_type where market=2 and status=1 ;

select code from stk_fn_data_us where type=1008 group by code order by fn_date desc;
select * from stk_fn_data_us where code='XOMA' order by fn_date desc;
select * from stk_kline_us where code='IRBT' order by kline_date desc;


create or replace view stk_fn_data_table_view as
select * from (select type,fn_value,code,fn_date from stk_fn_data) pivot (sum(fn_value) for type in (121,400)) where code='000001';



create or replace view stk_fn_data_search_view as
with currentDate
as
(select t.code,t.fn_date,t.num
    from (select s.code,s.fn_Date,
                 ROW_NUMBER() over(PARTITION by code order by fn_date desc) as num
            from (select distinct code,fn_date from stk_fn_data where fn_date>to_char(sysdate-1000,'yyyyMMDD')) s ) t
       where t.num = 1),
lastDate
as
(select t.code,t.fn_date,t.num
    from (select s.code,s.fn_Date,
                 ROW_NUMBER() over(PARTITION by code order by fn_date desc) as num
            from (select distinct code,fn_date from stk_fn_data where fn_date>to_char(sysdate-1000,'yyyyMMDD')) s ) t
       where t.num = 5),
currentdata
as
(select a.* from (select type, fn_value,code,fn_date from stk_fn_data)
pivot (sum(fn_value) for type in (121,400)) a, currentDate m
where a.code=m.code and a.fn_date=m.fn_date ),
lastdata
as
(select a.* from (select type,fn_value,code,fn_date from stk_fn_data)
pivot (sum(fn_value) for type in (121,400)) a, lastDate m
where a.code=m.code and a.fn_date=m.fn_date )
select c.* from currentdata c, lastdata d where c.code=d.code
;



select f.code,k.name,f.fn_value from stk_fn_data f, stk k
where f.code=k.code and fn_date='20161231' and type=109 order by fn_value desc;

select * from stk_fn_type where source=100 ;
select * from stk_fn_type where market=2 and status=1 for update;
select * from stk_fn_data_us where code='IRBT' and fn_date='20170401' and type<4000 order by fn_date desc;
delete from stk_fn_data_us where type in (1007,1010,2004)

--create or replace view stk_fn_data_us_table_view as
with todayk as
(select t.close,t.code,t.kline_date
    from (select s.*,
                 ROW_NUMBER() over(PARTITION by code order by kline_date desc) as num
            from stk_kline_us s where code='FNSR' ) t
       where t.num = 1)
select fn.code,fn.fn_date,
"4004" as "ROE(ttm)",
"1001" as "营收总额(Q)",
round((decode(sum("1001") over (order by fn_date desc rows between 4 following and 4 following),0,0,"1001"/sum("1001") over (order by fn_date desc rows between 4 following and 4 following))-1)*100,2)||'%' as "营收增长�?qoq)",
round(decode("1001",0,0,"1002"/"1001")*100,2)||'%' as "毛利�?Q)",
"1008" as "净利润(Q)",
"1007" as "净利润(不计非经�?",
round((decode(sum("1008") over (order by fn_date desc rows between 4 following and 4 following),0,0,"1008"/sum("1008") over (order by fn_date desc rows between 4 following and 4 following))-1)*100,2)||'%' as "净利润增长�?qoq)",
round(decode((sum("1008") over (order by fn_date desc rows between 1 following and 4 following)),0,0,(sum("1008") over (order by fn_date desc rows between 0 preceding and 3 following))/(sum("1008") over (order by fn_date desc rows between 1 following and 4 following))-1)*100,2)||'%' as "净利润增长�?ttm)",
"2001" as "总资�?,
round(decode("2001",0,0,"2002"/"2001")*100,2)||'%' as "负债率",
"1004" as "运营开�?,
round(decode("1001",0,0,"1004"/"1001")*100,2)||'%' as "运营占比营收�?,
"1003" as	"研发费用",
"2005" as "应收账款",
"2010" as "应付帐款",
"2006" as "库存总额",
"2007" as "现金及现金等价物",
"2008" as	"短期投资",
"2009" as	"长期投资",
"3001" as	"运营活动所产生现金",
"3002" as	"投资活动所产生现金",
"3003" as	"融资活动所产生现金",
"3004" as	"现金净增减�?,
"4014" as "速动比率",
"4012" as "流动比率(mrq)",
"4008" as "EPS(ttm)",
"4005" as "每股收入(ttm)",
"4013" as "每股价�?mrq)",
round(decode("4008",0,0,k.close/"4008"),2) as "PE",
round(decode("4005",0,0,k.close/"4005"),2) as "PS",
round(decode("4013",0,0,k.close/"4013"),2) as "PB",
"4018" as "PEG"
from (
select * from (select type,fn_value,code,fn_date,fiscal_year_ends from stk_fn_data_us where code='FNSR'
) pivot (sum(fn_value)
for type in (
4004, /*ROE(ttm)*/
1002, --毛利
1001, --营收总额
1008,  --净利润
1007, --归属普通股东收�?不计非经常项�?
2001, --资产总额
2002, --债务总额
1004, --运营开支总额
1003,	--研发费用
2005, --应收账款总计(净�?
2010, --应付帐款
2006, --库存总额
2007, --现金及现金等价物
2008,	--短期投资
2009,	--长期投资
3001,	--运营活动所产生现金
3002,	--投资活动所产生现金
3003,	--融资活动所产生现金
3004,	--现金净增减�?
4014, --速动比率
4012, --流动比率(mrq)
4008, --EPS(ttm)
4005, --每股收入(ttm)
4018, --PEG
4013 --帐面价�?mrq)
))
order by fn_date desc) fn, todayk k where fn.code=k.code;

select * from stk_fn_data_us_table_view where code='MOMO';

select * from stk_kline_us where code='IRBT' order by kline_date desc;
select avg(pe_ttm) from stk_kline_us where kline_date='20170602' and code in
(select code from stk_industry where industry=885739) and pe_ttm is not null and pe_ttm>3 and pe_ttm<200

select * from stk_error_log order by insert_time desc;
select * from stk where status=1;

select * from stk_kline where code='000001'  order by kline_date desc;
select * from stk_kline where kline_date ='20180912' and ps_ttm is null;

select type,nvl(disp_name,name),re_calc from stk_fn_type where market=1 order by disp_order asc;


select kline_date,pe_ttm,ntile(100) over(order by pe_ttm) pe_ntile,pb_ttm, ntile(100) over(order by pb_ttm) pb_ntile
from stk_kline where kline_date>=to_char(sysdate-1825,'yyyymmdd') and code='600482' order by kline_date desc;


select * from stk_kline where pe_ntile is not null;
select * from stk_kline where kline_date='20170707';

select * from stk where code='885496';
select trunc(sysdate-to_date(listing_date,'yyyymmdd')) from stk where code='600337';

select * from stk_pe order by report_date desc for update;
select * from stk_earnings_notice;
select * from stk_error_log order by insert_time desc;
select * from stk_capital_flow order by flow_date desc;


--3263 3197
select avg(pe_ttm) from stk_kline where kline_date='20200313' and pe_ttm is not null and pe_ttm>3 and pe_ttm<200

select * from stk where code='600718';
select * from stk_earnings_notice a,
(select code,max(fn_date) fn_date from stk_earnings_notice group by code) b
where a.code=b.code and a.fn_date=b.fn_date and a.code='002652';

select * from stk_earnings_forecast where code='000001';

select * from stk_search_condition for update;
select * from stk_kline where kline_date='20170710';

select * from stk_capital_flow where code='000001' order by flow_date desc;
select count(1) from stk_rrrrcapital_flow where code is null;

select * from stk_data_industry_pe order by pe_date desc;
select * from stk_data_industry_pe order by insert_time desc;
select distinct source from stk_industry_type;
select * from stk_industry_type where source in ('10jqka_gn','10jqka_thshy') order by source,name;

select * from stk_earnings_notice where code='002245' order by notice_date desc;
select * from stk_fn_type where market=1 for update;

select * from stk_data_ppi order by insert_time desc;
select * from stk_industry;
select * from stk_industry_type where name like '%水泥%';
select * from stk_search_mview;

select * from stk_investigation order by insert_date desc;
select * from stk_cn where code='603728'

select * from stk_capital_flow;

select * from(
select f.code,k.name,round(avg(f.main_amount),2) hot from stk_capital_flow f, stk k where f.code=k.code and k.cate=4
and f.flow_date>=to_char(sysdate-30,'yyyymmdd')
group by f.code,k.name) order by hot desc;


select avg(f.main_amount) hot,f.code,k.name from stk_capital_flow f, stk k where f.code=k.code and k.cate=4
and f.flow_date<=to_char(sysdate-5,'yyyymmdd') and f.flow_date>=to_char(sysdate-35,'yyyymmdd')
group by f.code,k.name;

select avg(f.main_amount),f.code,k.name from stk_capital_flow f, stk k where f.code=k.code and k.cate=4
and f.flow_date<=to_char(sysdate-30,'yyyymmdd') and f.flow_date>=to_char(sysdate-60,'yyyymmdd')
group by f.code,k.name;


select * from stk_search_condition order by nvl(update_time, insert_time);

select * from stk_search_mview v where 1=1  and code in ('300344') and code not in ('300344');

select * from  stk_capital_flow order  by flow_date desc;
select flow_date,name,hot from(select f.code,k.name,round(avg(f.main_amount),2) hot,max(f.flow_date) flow_date from stk_capital_flow f, stk k where f.code=k.code and k.cate=4 and f.flow_date between to_char(sysdate-30,'yyyymmdd') and to_char(sysdate-0,'yyyymmdd') group by f.code,k.name) order by hot desc;

select * from stk where cate=4 and name like '%网约�?';
select * from stk_industry_type where id=885623;
select * from stk_industry_type where source='10jqka_gn' for update;
select * from stk_industry where industry=885623;

select * from stk where code='300452';

select * from stk_hk where code='01195';
select null as test from dual;

select * from stk_hk where f9 like '%钢铁%'
select * from stk_kline;
select * from stk_import_info where code='300438' and title like '%回购%' and title not like '%注销%' order by id desc;
select * from stk_import_info where title like '%回购%' and title not like '%注销%' and insert_time >= add_months(sysdate,-1);
select * from stk_fn_type where market=1 order by disp_order for update;

select * from stk_fn_data where code='600525' and type=209;

select * from stk_import_info_type for update;
select * from stk_kline_hk;


select * from stk_fn_type where market=1 order by type for update;
select * from stk_fn_data_us where code='JMEI' and type =2020;
select * from stk_kline_us  where code='JMEI' order by kline_date desc;

select * FROM stk_ownership where code='603999' order by fn_date desc;
select * from stk_error_log order by insert_time desc;
select count(1) from stk_error_log;
select * from stk_fn_data where code='603998';

select * from stk_restricted where code='002732';

select * from stk_text order by insert_time desc;


select * from stk where code='300145';
select * from stk_kline where  code='600007' order by kline_date desc;
select count(1) from stk_kline where kline_date='20180830';
select median(pb_ttm) from stk_kline where kline_date='20180905';
select median(pe_ttm) from stk_kline where kline_date='20180905';
select median(pe) from stk_search_mview where market=1 and listing_days>120;
select median(pb) from stk_search_mview where market=1 and listing_days>120;
select count(1) from stk_search_mview where market=1 and listing_days is null;

select * from stk_strategy where text like '%000671%' order by strategy_date desc;

select * from  stk_industry_type where id=885759 ;
select * from stk_dictionary where type=1006 for update;

select * from stk_ownership where code='000559' order by fn_date desc for update;
select * from stk_investigation where invest_date = (select max(invest_date) from stk_investigation) order by investigator_count desc;
select * from stk_investigation order by insert_date desc;

select * from stk_text where code='002214' order by insert_time desc;
select * from stk_user;

select * from stk_earnings_notice order by notice_date desc;
select * from stk_earnings_notice where code='000004' order by insert_time desc;
select * from stk_import_info where code='002798' order by insert_time desc;
select * from stk_organization where name like '%一一�?';
select * from stk_ownership where org_id=237543 order by fn_date desc;

select * from stk_organization where name like '%�?'
select * from stk_cn s,stk_kline k where s.code=k.code and s.code like '00%4' and k.low=6.92 order by k.kline_date desc;

select * from stk_investigation order by insert_date desc;
select * from stk_investigation where investigator like '%高瓴%' order by insert_date desc;

select code,name from stk where market=1 and cate=4 order by code;

Select 'ok' From Dual Where 1 / 0 = 1 And 1 = 2;
Select 'ok' From Dual Where 1 = 2 And 1 / 0 = 1;
select * from dual where 1?=2 and 1=1 and 3?=2

select count(1) from stk_kline where kline_date='20180815';
select * from stk_kline where kline_date='20190510';
select * from STK_SEARCH_MVIEW where code='002131';
select * from stk_earnings_notice where code='002624' order by fn_date desc;


select median(pb_ttm),count(1) from stk_kline where kline_date='20180914' and pb_ttm is not null and pb_ttm>0 and pb_ttm<30;
select median(pe_ttm),count(1) from stk_kline where kline_date='20180905' and pe_ttm is not null and pe_ttm>3 and pe_ttm<200;

select * from stk_us order by insert_time desc;


Select username,PROFILE FROM dba_users;
Select * FROM dba_profiles s Where s.profile='DEFAULT' AND resource_name='PASSWORD_LIFE_TIME';
Alter PROFILE DEFAULT LIMIT PASSWORD_LIFE_TIME UNLIMITED;
alter user stk identified by stkpwd;


select id,type,code,code_type,title,text,nvl(update_time,insert_time) update_time,disp_order,sub_type from stk_text
where user_id=1 and code='002531' and code_type=? order by nvl(update_time,insert_time) desc;

select * from stk_text where sub_type is null;
update stk_text set sub_type=0 where sub_type is null;



select * from stk_organization where name like '%摩根士丹�?';
select * from stk_ownership where org_id=239897 order by fn_date desc;

select b.name,a.stk_num,a.rate,a.num_change,a.num_change_rate from stk_ownership a, stk_organization b where a.org_id=b.id and a.code='000002' order by a.fn_date desc,a.stk_num desc;
select fn_date from(select distinct fn_date from stk_ownership where code='000002' order by fn_date desc) where rownum <= 16


select * from stk_dictionary where type=500;
insert into stk_dictionary select 21,'睿远','睿远',null,12,null,null,null,null from dual;


select a.code,a.fn_date,b.name,a.stk_num,a.rate,a.num_change,a.num_change_rate from stk_ownership a, stk_organization b where a.org_id=b.id and b.name like '%景顺长城内需增长%' order by a.fn_date desc,a.stk_num desc;


select * from stk_cn order by code desc;
select * from stk where code='600928';

select * from stk_cn where listing_date < to_char(add_months(sysdate, -12),'yyyymmdd');


select a.code,a.fn_date,count(a.num_change_rate) cnt,listagg(c.name,', ') within group (order by a.num_change_rate) name from stk_ownership a,stk_cn b, stk_organization c
where a.code=b.CODE and length(c.name)>3 and a.org_id=c.id and b.listing_date < to_char(add_months(sysdate, -12),'yyyymmdd') and a.fn_date > to_char(add_months(sysdate, -5),'yyyymmdd') and a.num_change_rate>0
group by a.code,a.fn_date having count(a.num_change_rate) >= 8 order by a.fn_date desc

select * from stk_industry_type;
select source,count(1) from stk_industry_type group by source;
select * from stk_organization;

select * from stk_dictionary where type=21;

select * from (
select a.name,d.fn_date,trunc(sum(d.stk_num)/10000),count(distinct c.id) cnt_org,count(distinct b.code) cnt_code
from stk_industry_type a, stk_industry b, stk_organization c, stk_ownership d
where a.id=b.industry and d.code=b.code and d.org_id=c.id and a.source='hexun_conception' and length(c.name)>5
and d.fn_date in ('20200331','20191231','20190930','20190630','20190331')
group by a.name,d.fn_date )
order by name,fn_date desc;


select * from stk_holder where code='000883' order by fn_date desc;


select a.code,name,count(*) cnt from (
select code,fn_date,holder,
sum(holder) over (order by code,fn_date desc rows between 1 following and 1 following) last_holder /*前面一期人�?/,
sum(holder) over (order by code,fn_date desc rows between 5 following and 5 following) last5_holder,/*前面�?期人�?/
row_number() over (partition by code order by fn_date desc) rown
from stk_holder where fn_date >=20190630 order by code,fn_date desc
) a,stk_cn s where a.code=s.code and rown <= 5 and holder < last_holder
and holder/last5_holder<=0.9 /*最近期人数至少比前�?期人数少10%*/
group by a.code,s.name having count(*)=5 order by a.code asc;

--增加and条件：股东户数小�?�?or 最近人数至少比�?期人数少10%


--近一年，股东户数小于3�?and 股东人数减少比例排序
盐津铺子(SZ:002847)
江山欧派(SH:603208)
小熊电器(SZ:002959)
密尔克卫(SH:603713)

select * from stk_import_info order by id desc;
select * from stk_import_info where code='000009' order by id desc;
select * from stk_import_info where code='01610' order by id desc;
select * from stk_text where code='00853';

select * from stk_text where insert_time > sysdate -365 and text like '%困境反转%' order by id desc;

select * from stk_industry_type;

--20200331 季度十大机构对行业配置的增仓比例
--TODO 排除新上市一年的公司
select fn_date, sum(rate), name, count(*), sum(rate) / count(*) * 100
  from (select a.code, b.rate, c.name, fn_date
          from stk_industry a, stk_industry_type c, stk_cn d,
               (select code, (case when sum(num_change) > 0 then 1 else -1 end) rate, fn_date from stk_ownership where fn_date = '20200331' group by code,fn_date) b
         where a.code = b.code and a.code = d.code and a.industry = c.id
           and d.LISTING_DATE < to_char(add_months(sysdate, -12),'yyyymmdd') and c.source = 'csindex_zjh')
 group by name,fn_date having count(*) > 50 order by sum(rate) / count(*) desc;

select fn_date, sum(rate), name, count(*), sum(rate) / count(*) * 100
  from (select a.code, b.rate, c.name, fn_date
          from stk_industry a, stk_industry_type c, stk_cn d,
               (select code, (case when sum(num_change) > 0 then 1 else -1 end) rate, fn_date from stk_ownership where fn_date = '20191231' group by code,fn_date) b
         where a.code = b.code and a.code = d.code and a.industry = c.id
           and d.LISTING_DATE < to_char(add_months(sysdate, -12),'yyyymmdd') and c.source = 'csindex_zjh')
 group by name,fn_date having count(*) > 50 order by sum(rate) / count(*) desc;

select fn_date, sum(rate), name, count(*), sum(rate) / count(*) * 100
  from (select a.code, b.rate, c.name, fn_date
          from stk_industry a, stk_industry_type c, stk_cn d,
               (select code, (case when sum(num_change) > 0 then 1 else -1 end) rate, fn_date from stk_ownership where fn_date = '20190630' group by code,fn_date) b
         where a.code = b.code and a.code = d.code and a.industry = c.id
           and d.LISTING_DATE < to_char(add_months(sysdate, -12),'yyyymmdd') and c.source = 'csindex_zjh')
 group by name,fn_date having count(*) > 50 order by sum(rate) / count(*) desc;

-------------

select fn_date, sum(rate), name, count(*), sum(rate) / count(*) * 100
  from (select a.code, b.rate, c.name, fn_date
          from stk_industry a, stk_industry_type c, stk_cn d,
               (select code, (case when sum(num_change) > 0 then 1 else -1 end) rate, fn_date from stk_ownership where fn_date = '20200331' group by code,fn_date) b
         where a.code = b.code and a.code = d.code and a.industry = c.id
           and d.LISTING_DATE < to_char(add_months(sysdate, -12),'yyyymmdd') and c.source = 'csindex_zz')
 group by name,fn_date having count(*) > 50 order by sum(rate) / count(*) desc;

select fn_date, sum(rate), name, count(*), sum(rate) / count(*) * 100
  from (select a.code, b.rate, c.name, fn_date
          from stk_industry a, stk_industry_type c, stk_cn d,
               (select code, (case when sum(num_change) > 0 then 1 else -1 end) rate, fn_date from stk_ownership where fn_date = '20191231' group by code,fn_date) b
         where a.code = b.code and a.code = d.code and a.industry = c.id
           and d.LISTING_DATE < to_char(add_months(sysdate, -12),'yyyymmdd') and c.source = 'csindex_zz')
 group by name,fn_date having count(*) > 50 order by sum(rate) / count(*) desc;

select fn_date, sum(rate), name, count(*), sum(rate) / count(*) * 100
  from (select a.code, b.rate, c.name, fn_date
          from stk_industry a, stk_industry_type c, stk_cn d,
               (select code, (case when sum(num_change) > 0 then 1 else -1 end) rate, fn_date from stk_ownership where fn_date = '20190630' group by code,fn_date) b
         where a.code = b.code and a.code = d.code and a.industry = c.id
           and d.LISTING_DATE < to_char(add_months(sysdate, -12),'yyyymmdd') and c.source = 'csindex_zz')
 group by name,fn_date having count(*) > 50 order by sum(rate) / count(*) desc;


select * from stk_industry_type where source='csindex_zz';

select s.code,s.name from stk s,stk_industry i,stk_industry_type t
where s.code=i.code and i.industry=t.id and t.source='csindex_zjh' and t.code='07'
order by t.id,s.code;

select * from stk_industry_type where code='BK0900';
select * from stk_industry where industry=125000 and code='002664';

select sum(rate), name, count(*), sum(rate) / count(*) * 100
  from (select a.code, b.rate, c.name, fn_date
          from stk_industry a, stk_industry_type c, stk_cn d,
               (select code, (case when sum(num_change) > 0 then 1 else -1 end) rate, fn_date from stk_ownership where fn_date = '20200331' group by code,fn_date) b
         where a.code = b.code and a.code = d.code and a.industry = c.id
           and d.LISTING_DATE < to_char(add_months(sysdate, -12),'yyyymmdd') and c.source = 'csindex_zjh')
 group by name having count(*) > 50 order by sum(rate) / count(*) desc;

select * from stk_text where code='000671' order by id desc;
select * from stk where code in ('TSLA','885467');
select * from stk where hot is null;
select * from stk_dictionary where type=1200 for update;

select value from v$parameter where name = 'processes';
select count(*) from v$process;
alter system set processes = 300 scope = spfile;

select * from stk_index_node where disp_order >= 0 order by node_level asc,disp_order asc

select * from stk_data_industry_pe order by pe_date desc;


select * from stk_text where id=10614950 for update;
select * from stk_text order by insert_time desc;
update stk_text set read_date = null where id=10615159;


select avg(pe_ttm) from stk_kline where kline_date='20201127' and pe_ttm is not null and pe_ttm>3 and pe_ttm<200;

select * from stk_import_info order by insert_time desc;

select * from stk_xueqiu_user;
select * from (select * from stk_kline t where code='000863' order by kline_date desc) where rownum <= 1000;

select * from stk_kline t where code='600862' order by kline_date desc;
update stk_kline set percentage='6.12' where code='600862' and kline_date='20190130';
select * from stk_kline t where kline_date='20201201';
select * from stk_kline t where kline_date='20201225';
select * from stk_kline_hk where kline_date='20201224';
select * from stk_kline_us where kline_date='20201224';
select * from stk_kline_us where code='.DJI' order by kline_date desc;
select count(*) from stk_kline t where kline_date='20210201';
select * from stk where market=2 and cate=2 order by code;


select * from stk_text order by insert_time desc, id desc;
select * from stk_text where user_id=-1 order by insert_time desc, id desc;

select count(*) from stk_text where insert_time>=sysdate-350;

select s_text_id .nextval from dual;


select code,kline_date as "date",open,close,high,low,volumn as volume,amount,last_close,percentage as change,hsl 
from (select t.*, rank() over(partition by t.code order by t.kline_date desc) as rn from stk_kline t where t.code in ('600600','000863')) where rn <= 100

select * from stk_text stktextent0_ where stktextent0_.type=3 and (stktextent0_.read_date is null) and stktextent0_.created_at>sysdate-2

select * from stk_text where type=3 order by insert_time desc;
select * from stk_pe order by report_date desc for update;
select avg(pe_ttm) as avg_pe_ttm,median(pe_ttm) as mid_pe_ttm from stk_kline_hk where kline_date=? and pe_ttm is not null and pe_ttm>3 and pe_ttm<200

select avg(pe_ttm) as avg_pe_ttm,median(pe_ttm) as mid_pe_ttm from stk_kline where kline_date='20201224' and pe_ttm is not null and pe_ttm>3 and pe_ttm<200
select avg(pb_ttm),median(pb_ttm) from stk_kline where kline_date='20201224' and pb_ttm is not null and pb_ttm>0 and pb_ttm<30;


select * from stk_task_log order by id desc;
--delete from stk_task_log;



update stk_kline_us set amount=null where code='.DJI' and kline_date='20201224';
select * from stk_kline_us where code='CMRX' order by kline_date desc;
select * from stk_kline where length(code)>6;

select * from stk_kline_hk where code='00678' order by kline_date desc;
update stk_kline_hk set code='06837' where code='068371' and kline_date='20210226';

select * from stk_text where sub_type=300 order by insert_time desc;
select * from stk_text where sub_type=100 order by insert_time desc;

select * from stk where market=2 order by insert_time desc;
select * from stk where code='605395';

select * from stk_holder where code='000832';
delete from stk_fn_data where code='000832';
delete from stk where code='000832';

select s.code, t.fn_date, t.holder, t.holding_amount, t.holder_change 
        from (select code, fn_date, holder, holding_amount,holder_change, ROW_NUMBER() over(PARTITION by code order by fn_date desc) as num from stk_holder) t, stk s 
        where t.code=s.code and t.num = 1 and t.holder_change<-10

select * from stk_error_log;

2021-02-26,12.19,12.19,12.56,11.98,6361449,7798379264.00,4.47,-6.09,-0.79,3.24

2867	601899	20080425	9.98	13.92	7.13	22.00	9.61	971534170.00	10346791205.00	1.000000	0.00	79.43			13.45	37.93			
select * from stk_kline t where code='000001' order by kline_date desc;
update stk_kline set last_close=11.35 where code='601899' and kline_date='20210111'

select * from ( select * from stk_text order by insert_time desc ) where rownum <= 20

select * from stk_dictionary where text ='业绩修正';
select * from stk_import_info _type where type=270 for update;


select * from stk where market=1 and cate=2 for update and code='999999' for update;
select * from stk where market=1 and cate=5 and code='BK0896';
select * from stk_kline where code='BK0896' order by kline_date desc;
select * from stk_kline where code='999999' order by kline_date desc;
select * from stk_kline where code='399300' order by kline_date desc;
select count(*) from stk_industry_type where source='eastmoney_gn';
select * from stk_industry where industry=124950;
select * from stk_industry_type where id=124950;


select * from stk where name like '%退%';
select * from stk where code='000022';
select * from stk_holder where code='688063';
select * from (select code ,fn_date,stk_num, 100 * (stk_num / sum(stk_num) over (order by code,fn_date desc rows between 1 following and 1 following) - 1) ten_owner_change  from
(select code,fn_date,sum(stk_num) stk_num from stk_ownership group by code,fn_date having code='688063')) where fn_date='20210331';

update stk_holder a set a.ten_owner_change = (select u.ten_owner_change from
(select code ,fn_date,stk_num, 100 * (stk_num / sum(stk_num) over (order by code,fn_date desc rows between 1 following and 1 following) - 1) ten_owner_change from
(select code,fn_date,sum(stk_num) stk_num from stk_ownership group by code,fn_date))  u
 where a.code=u.code and a.fn_date=u.fn_date)

select count(1) from stk_holder where fn_date='20210331' and holding_amount>100000;
select * from stk_ownership where org_id=237543 and fn_date='20210331';


select s.code, s.name, t.fn_date, t.holder, t.holding_amount, t.holder_change
  from (select code, fn_date, holder, holding_amount,holder_change, ROW_NUMBER() over(PARTITION by code order by fn_date desc) as num from stk_holder) t, stk s
 where t.code=s.code and t.num = 1 and t.code='600600';
 
select * from stk_text where code='000761' order by insert_time desc; 
select * from stk_text where code='605009' order by insert_time desc;
select * from stk_text where reply_positive is not null;
select * from stk_text where sub_type=300 order by insert_time desc;

select * from stk_error_log order by insert_time desc;
select * from stk_import_info order by insert_time desc;

select * from stk_news order by id desc;--404410
select * from stk_news where code='00004' order by id desc;

select * from stk_import_info_type;
select * from stk_dictionary where type=1006;
select s_news_id.nextval from dual;


select count(1) from stk_text where insert_time>=sysdate-180;
select * from stk_text  order by insert_time desc;

select * from stk_fn_data where code='600107' and type=300 order by fn_date desc;
select * from stk_fn_type for update;

select * from stk_capital_flow where code='002346' order by flow_date desc;
select * from stk_capital_flow order by insert_time desc;


update stk_dictionary set text ='资产置换|转让' where type=2000 and key = '180';


select code,kline_date ,open,close,high,low,volumn as volume,amount,last_close,percentage as change,hsl,pe_ttm,pb_ttm from (select t.*, rank() over(partition by t.code order by t.kline_date desc) as rn
from stk_kline t where t.code in ('002346','600600','600601','601958','600531','000758','000060','600497','000807','000751','600395','600456','002340','002237','000612','600459','000960','600251')) where rn <= 1

select * from stk_report_header where type='allstocks_rps' order by insert_time desc;
select * from stk_report_detail where header_id in (10031) for update;
select distinct type from stk_report_header;

select * from stk_report_header order by report_date desc, insert_time desc ;


delete from stk_report_detail where header_id=10112;
delete from stk_report_header where id=10112;


select report_date from (select report_date, rownum rn from  (select report_date from stk_report_header where report_date <= '20210805'
group by report_date order by report_date desc)) where rn<=7;

select report_date from (select report_date, rownum rn from
    (select report_date from stk_report_header where report_date <= '20210805'
    group by report_date order by report_date desc)) where rn <= 7;

select d.* from stk_report_header h, stk_report_detail d where h.id=d.header_id and h.report_date in ('')
                                                           and h.type='bks' and d.strategy_code like 'strategy_08%';


select * from stk where code='BK0619';

select * from stk_kline where kline_date>'20210817' and code='600141';
select * from stk_kline where code='600141' order by kline_date desc;

select * from stk_fn_data;
select * from stk_fn_type;
select * from stk_keyword where id=101336;
select * from stk_keyword order by insert_time desc;
select * from stk_keyword where name like '%电池%';
select * from stk_keyword_link order by insert_time desc;
select s.code,s.name from stk_keyword_link k, stk s where keyword_id=100856 and k.code=s.code;
select * from stk_text where title like '%年报%' order by insert_time desc;
select * from stk_text where code='600600' order by insert_time desc for update;
delete from stk_text where code='600600' and type =4;
select * from stk_text where sub_type=110 and id>=30815064 order by insert_time desc;

select * from stk_fn_data where code='603305' order by fn_date desc;

select k.name,count(l.code) from stk_keyword k, stk_keyword_link l 
where k.id=l.keyword_id and l.link_type=1 group by k.name order by count(l.code) desc;

select k.name,count(l.code) from stk_keyword k, stk_keyword_link l
where k.id=l.keyword_id and l.link_type=2 group by k.name order by count(l.code) desc;

select * from stk_dictionary where type=5010; 

insert into stk_dictionary select 500,'xueqiu',null,null,null,null,null,null,null from dual;
insert into stk_dictionary select 500,'iwencai',null,null,null,null,null,null,null from dual;

select * from stk_investigation order by insert_date desc;
select * from stk_data_ppi_type where name like '%pvdf%';

select * from stk_status order by insert_time desc for update;

select * from stk_report_header order by id desc;

select * from stk_status for update;
select * from stk_status_label;

select * from stk_kline_hk where code='08369' order by kline_date desc;
select * from stk_kline_hk where code='02312' order by kline_date desc;

select * from stk_capital_flow where code='300987' order by flow_date desc;
select * from stk_kline where code='300987' order by kline_date desc;
select * from stk_kline where kline_date='20210922';
select * from stk where code='TSLA';
select * from stk_hk where company_profile is not null;
select * from stk_kline_hk where kline_date='20210930';
select * from stk_status for update;

select s.code,s.name,(s.TOTAL_CAPITAL*k.close)/10000 total,s.TOTAL_CAPITAL from stk_hk s, stk_kline_hk k where s.code=k.code(+) and k.kline_date='20210930' order by total;
select s.code,s.name,(s.TOTAL_CAPITAL*k.close)/10000 total,s.TOTAL_CAPITAL from stk_us s, stk_kline_us k where s.code=k.code(+) and k.kline_date='20210930' order by total;
select * from (select * from stk_kline_hk where code='999999' order by kline_date desc) where rownum<=1;

select * from stk_capital_flow where code='600638' order by flow_date desc;

select * from stk_report_detail order by strategy_date desc, id desc;
select * from stk_report_detail where id=118069;
select * from stk_report_header where id=10720;

select * from stk_text where code='300750' order by insert_time desc;
select * from stk_text where type=6  order by insert_time desc for update;
select * from stk_text where type in (2,4) and code='600600'  order by insert_time desc;

delete from stk_text where type=2  order by insert_time desc
select * from stk_capital_flow order by insert_time desc;
