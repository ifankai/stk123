package com.stk123.service.support;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;
import java.util.Map;

public class CustomOracleDialect extends Oracle12cDialect {

    public CustomOracleDialect(){
        super();
//        registerHibernateType( Types.NUMERIC, StandardBasicTypes.LONG.getName() );
    }

    public String getHibernateTypeName(int code, int length, int precision, int scale) throws HibernateException {
        String result = super.getHibernateTypeName( code, length, precision, scale );
        if(code == Types.NUMERIC){
            if(scale == 0) result = "long";
            else if(scale > 0 && precision < 10) result = "double";
        }
        return result;
    }

    @Override
    public String getQuerySequencesString() {
        return "select SEQUENCE_OWNER, SEQUENCE_NAME, greatest(MIN_VALUE,         -9223372036854775807) MIN_VALUE,\n"+
                "Least(MAX_VALUE, 9223372036854775808) MAX_VALUE, INCREMENT_BY,     CYCLE_FLAG, ORDER_FLAG, CACHE_SIZE,\n"+
                "Least(greatest(LAST_NUMBER, -9223372036854775807), 9223372036854775808) LAST_NUMBER\n"+
//                ",PARTITION_COUNT, SESSION_FLAG, KEEP_VALUE\n"+
                "from all_sequences";
    }

}
