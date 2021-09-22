update stk_keyword set insert_time=sysdate-1000 where INSERT_TIME is null;

delete from STK_KEYWORD_LINK where KEYWORD_ID in (select id from STK_KEYWORD where name is null);
delete from STK_KEYWORD where name is null;

