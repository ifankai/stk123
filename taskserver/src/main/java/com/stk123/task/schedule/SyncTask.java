package com.stk123.task.schedule;

import cn.hutool.extra.ssh.JschUtil;
import com.jcraft.jsch.Session;
import com.stk123.common.CommonUtils;
import com.stk123.common.util.ScpUtils;
import com.stk123.task.tool.TaskUtils;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@CommonsLog
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@PropertySource("classpath:password.properties")
public class SyncTask extends AbstractTask {

    public static String host = "81.68.255.181";
    public static int port = 22;
    public static String username = "root";
    @Value("${server.password}")
    public static String password;
    private String localDir = "D:/share/workspace/stk123/oracle/";
    private String remoteDir = "/var/stk/oracle/";

    @Setter
    private String table;
    @Setter
    private String whereClause;

    private static Map<String, String> SYNC_TABLES = new LinkedHashMap<>();
    static {
        SYNC_TABLES.put("stk_text", "where insert_time>sysdate-1");
        SYNC_TABLES.put("stk_task_log", "where insert_time>sysdate-1");
        SYNC_TABLES.put("stk_report_header", "where insert_time>sysdate-1");
        SYNC_TABLES.put("stk_report_detail", "where header_id in (select id from stk_report_header where insert_time>sysdate-1)");
    }

    @Override
    public void register() {
        this.run(this::execute);
    }

    public void execute() {
        try {
            for (Map.Entry<String, String> e : SYNC_TABLES.entrySet()) {
                String tableName = e.getKey();
                if (StringUtils.containsIgnoreCase(tableName, table)) {
                    String whereClause = this.whereClause == null ? e.getValue() : this.whereClause;
                    syncTable(tableName, whereClause);
                }
            }
            if(table == null){
                log.info("begin to sync database.");
                syncDatabase();
                log.info("end to sync database.");
            }
        } catch (Exception e) {
            log.error("XueqiuStockArticleTask", e);
        }
    }

    public void syncDatabase() throws Exception {
        String dpFile = "DB_STK.DP";
        String dateStartUS = CommonUtils.formatDate(CommonUtils.addDay(new Date(), -100), CommonUtils.sf_ymd2);
        String dateStart = CommonUtils.formatDate(CommonUtils.addDay(new Date(), -800), CommonUtils.sf_ymd2);
        //PARALLEL=4  //ORA-39094: 在此数据库版本中不支持并行执行。
        //expdp stk/stkpwd@XE directory=DPUMP_DIR dumpfile=db_stk.dp REUSE_DUMPFILES=Y SCHEMAS=stk QUERY=STK_ERROR_LOG:\"WHERE 1<>1\",STK_KLINE_US:\"WHERE kline_date>=\'20210101\'\",STK_KLINE:\"WHERE kline_date>=\'20210101\'\",STK_DATA_EASTMONEY_GUBA:\"WHERE 1<>1\",STK_FN_DATA_BAK:\"WHERE 1<>1\",STK_DATA_PPI:\"WHERE 1<>1\",STK_CAPITAL_FLOW:\"WHERE 1<>1\",STK_MONITOR:\"WHERE 1<>1\"
        String expdp = "expdp stk/stkpwd@XE directory=DPUMP_DIR dumpfile="+dpFile+" REUSE_DUMPFILES=Y SCHEMAS=stk QUERY=STK_ERROR_LOG:\\\"WHERE 1<>1\\\",STK_KLINE_US:\\\"WHERE kline_date>=\\'"+dateStartUS+"\\'\\\",STK_KLINE:\\\"WHERE kline_date>=\\'"+dateStart+"\\'\\\",STK_KLINE_HK:\\\"WHERE kline_date>=\\'"+dateStart+"\\'\\\",STK_DATA_EASTMONEY_GUBA:\\\"WHERE 1<>1\\\",STK_FN_DATA_BAK:\\\"WHERE 1<>1\\\",STK_DATA_PPI:\\\"WHERE 1<>1\\\",STK_CAPITAL_FLOW:\\\"WHERE flow_date>=\\'"+dateStart+"\\'\\\",STK_MONITOR:\\\"WHERE 1<>1\\\"";
        log.info(expdp);
        log.info("begin to expdp:"+dpFile);
        TaskUtils.cmd(expdp);
        log.info("end to expdp:"+dpFile);

        log.info("begin to upload:"+dpFile);
        ssh("rm -rf "+remoteDir+dpFile);
        uploadFile(dpFile);
        log.info("end to upload:"+dpFile);

        ssh("sh "+remoteDir+"delete_user_stk.sh");
        ssh("chmod 644 "+remoteDir+dpFile);
        ssh("sh "+remoteDir+"impdp_db.sh");
    }

    public void syncTable(String tableName, String whereClause) throws Exception {
        String command = getExpCommand(tableName, whereClause);
        log.info(command);
        TaskUtils.cmd(command);
        uploadFile(tableName+".dmp");
        command = getImpCommand(tableName);
        log.info(command);
        ssh(command);
        //TODO
        //ssh("curl localhost:8088/elasticsearch/tablename");
    }

    public void ssh(String command){
        Session session = null;
        try {
            log.info(command);
            session = JschUtil.getSession(host, port, username, password);
            String cmd = "source /etc/profile;source ~/.bash_profile;source ~/.bashrc; . oraenv; " + command;
            String output = JschUtil.exec(session, cmd, Charset.forName("UTF-8"));
            log.info(output);
        }finally {
            JschUtil.close(session);
        }
    }

    public void uploadFile(String tableName) throws Exception {
        ScpUtils scpclient = ScpUtils.getInstance(host, port, username, password);
        scpclient.putFile(localDir+ tableName, remoteDir);
    }

    public String getExpCommand(String tableName, String whereClause){
        if(!StringUtils.containsIgnoreCase(whereClause, "where")){
            throw new RuntimeException("whereClause is not start with 'where'");
        }
        String command = "exp stk/stkpwd@XE tables=(%s) query=\\\"%s\\\" file="+localDir+"%s.dmp log="+localDir+"%s.log";
        return String.format(command, tableName, whereClause, tableName, tableName);
    }
    public String getImpCommand(String tableName){
        String command = "imp userid=stk/stkpwd@localhost:1539/xepdb1 file='"+remoteDir+"%s.dmp' log='"+remoteDir+"%s.log' full=y ignore=y CONSTRAINTS=y";
        return String.format(command, tableName, tableName);
    }

    public static void main(String[] args) {
        /*SyncTask task = new SyncTask();
        task.execute();*/

        Session session = null;
        try {
            session = JschUtil.getSession(host, port, username, "Kevin181302");
            String cmd = "source /etc/profile;source ~/.bash_profile;source ~/.bashrc; " + " curl -A 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 QIHU 360EE' http://www.aastocks.com/sc/stocks/analysis/moneyflow.aspx?symbol=01801\\&type=h";
            String output = JschUtil.exec(session, cmd, Charset.forName("UTF-8"));
            log.info(output);
        }finally {
            JschUtil.close(session);
        }
    }
}
