package com.stk123.util;

import com.stk123.util.ik.StringSimilarUtils;
import com.stk123.model.bo.StkErrorLog;
import com.stk123.common.util.JdbcUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Deprecated //use ErrorService to replace
public class ExceptionUtils {

    public final static String ERROR_CODE_999998 = "999998";
    public final static String ERROR_CODE_999997 = "999997";

	public static void insertLog(Connection conn, Exception e){
		ExceptionUtils.insertLog(conn,"999999", e);
	}

	public static boolean insertLogWithSimilarCheck(Connection conn, String specialCode, Exception e) {
        List<StkErrorLog> errors = ExceptionUtils.queryErrors(conn, specialCode);
        String sException = ExceptionUtils.getExceptionAsString(e);
        boolean hasSimilar = false;
        for(StkErrorLog error : errors){
            if(StringSimilarUtils.getSimilarRatio(sException, error.getError()) >= 0.95){
                hasSimilar = true;
                break;
            }
        }
        if(!hasSimilar) {
            ExceptionUtils.insertLog(conn, specialCode, e);
            return true;
        }
        return false;
    }

	public static void insertLog(Connection conn, String code, Exception e){
		List params = new ArrayList();
		params.add(code);
		StringWriter aWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(aWriter));
		params.add(JdbcUtils.createClob(aWriter.getBuffer().toString()));
		params.add(new Timestamp(new Date().getTime()));
		JdbcUtils.insert(conn, "insert into stk_error_log(id,code,error,insert_time) values(s_error_log_id.nextval,?,?,?)", params);
	}

	public static Exception exception(String msg, Exception e){
		StringWriter aWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(aWriter));
		return new Exception(msg +"\r"+ aWriter.getBuffer().toString());
	}

	public static String getExceptionAsString(Throwable e){
		StringWriter aWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(aWriter));
		return aWriter.getBuffer().toString();
	}

	public static List<StkErrorLog> queryErrors(Connection conn, String code) {
		return JdbcUtils.list(conn,"select * from stk_error_log where code=? order by insert_time desc", code, StkErrorLog.class);
	}
}
