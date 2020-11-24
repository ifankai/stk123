package com.stk123.entity;

import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

public class CustomOracleDialect extends Oracle12cDialect {

//    public CustomOracleDialect(){
//        super();
//        System.out.println("CustomOracleDialect instance");
//        registerHibernateType( Types.NUMERIC, StandardBasicTypes.LONG.getName() );
//    }

    @Override
    public String getQuerySequencesString() {
        return "select SEQUENCE_OWNER, SEQUENCE_NAME, greatest(MIN_VALUE,         -9223372036854775807) MIN_VALUE,\n"+
                "Least(MAX_VALUE, 9223372036854775808) MAX_VALUE, INCREMENT_BY,     CYCLE_FLAG, ORDER_FLAG, CACHE_SIZE,\n"+
                "Least(greatest(LAST_NUMBER, -9223372036854775807), 9223372036854775808) LAST_NUMBER\n"+
//                ",PARTITION_COUNT, SESSION_FLAG, KEEP_VALUE\n"+
                "from all_sequences";
    }

}
