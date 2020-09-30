package com.stk123.spring.support.checkpoints;

import com.stk123.model.Index;
import com.stk123.spring.dto.checkpoints.CheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class CheckCashRevenueRatio extends AbstractCheck {

    public CheckCashRevenueRatio(){
        this.name = "现金收入比（是否大于50%）";
    }

    @Override
    public Result execute(String code) throws Exception {
        Connection conn = null;
        try {
            conn = ds.getConnection();
            Index index = new Index(conn, code);
            return new ResultTrueOrFalse(true);
        }finally {
            conn.close();
        }
    }

}
