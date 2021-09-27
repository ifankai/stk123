create table stk_status(
  id number(10),
  code varchar2(10),
  valid number(1) default 1,
  type number(2),
  sub_type number(3),
  quantity number(10,2),
  start_time date,
  end_time date,
  comments varchar2(1000),
  insert_time date,
  update_time date
);
create sequence s_status_id INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE CACHE 10;
alter table stk_status add constraint pk_status_id primary key (id);
create index idx_status_code on stk_status (code);

