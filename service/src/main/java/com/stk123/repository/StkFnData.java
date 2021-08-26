package com.stk123.repository;

import com.stk123.entity.StkFnDataEntity;
import com.stk123.entity.StkNewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

public interface StkFnData<E extends com.stk123.entity.StkFnData> {

    List<E> findAllByCodeAndFnDateAfterOrderByFnDateDescTypeAsc(String code, String fnDate);

    List<E> findAllByCodeInAndFnDateAfterOrderByCodeAscFnDateDescTypeAsc(List<String> codes, String fnDate);

    default Map<String, List<E>> getAllByCodeInAndFnDateAfterOrderByCodeAscFnDateDescTypeAsc(List<String> codes, String fnDate){
        List<E> data = findAllByCodeInAndFnDateAfterOrderByCodeAscFnDateDescTypeAsc(codes, fnDate);
        Map<String, List<E>> result = new LinkedHashMap<>();
        for(com.stk123.entity.StkFnData n : data){
            List<E> list = result.get(n.getCode());
            if(list == null){
                list = new ArrayList<>();
                result.put(n.getCode(), list);
            }
            list.add((E) n);
        }
        return result;
    }
}
