show columns from STK_XQ_POST;

select * from STK_XQ_POST;

insert into STK_XQ_POST values (1, CURRENT_TIMESTAMP, false, 10, 'text', 'title', 'avatar_url', 100, null, false, null);
insert into STK_XQ_POST values (2, CURRENT_TIMESTAMP, true, 5, 'text123', 'title123', 'avatar_url', 160, null, false, null);

update STK_XQ_POST set is_read=false ;

alter table STK_XQ_POST add  followers_Count integer(10)