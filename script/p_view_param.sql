create or replace package p_view_param is   
  function set_param(param_name varchar2, param_value varchar2) return number;   
  function get_param(param_name varchar2) return varchar2;   
end p_view_param;   
   


create or replace package body p_view_param is   
  function set_param(param_name varchar2, param_value varchar2) return number is   
  begin   
    dbms_session.set_context( 'my_ctx', param_name, param_value );  
    return 0;   
  end;   
   
  function get_param(param_name varchar2) return varchar2 is   
  begin   
    return sys_context( 'my_ctx', param_name );   
  end;   
end p_view_param;   

/*
Caused by: java.sql.SQLException: ORA-01031: 权限不足
ORA-06512: 在 "SYS.DBMS_SESSION", line 101
ORA-06512: 在 "STK.P_VIEW_PARAM", line 4
*/
--解决方案：
create OR REPLACE context my_ctx using stk.p_view_param;

-----------
   
create or replace view viewname as   
select * from tablename where aa = nvl(p_view_param.get_param('param'), aa);   
   
   
select * from viewname where p_view_param.set_param('param','123')=0;
