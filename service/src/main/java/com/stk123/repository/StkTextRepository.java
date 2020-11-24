package com.stk123.repository;

import com.stk123.entity.StkTextEntity;
import com.stk123.model.dto.TextDto;
import com.stk123.model.xueqiu.Post;
import com.stk123.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface StkTextRepository extends JpaRepository<StkTextEntity, Long> {

    List<StkTextEntity> findAllByCodeAndTypeOrderByInsertTimeDesc(String code, Integer type);

    //转化为 Map
    default Map<Long, StkTextEntity> findAllMap(String code, Integer type) {
        return findAllByCodeAndTypeOrderByInsertTimeDesc(code, type)
                .stream().collect(Collectors.toMap(StkTextEntity::getId, v -> v));
    }

    //https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections
    //https://www.baeldung.com/jpa-queries-custom-result-with-aggregation-functions


    @Query("select t.id,t.title from StkTextEntity t left join StkXqPostEntity p " +
            "on t.id=p.id where t.code=:code and t.type=:type order by t.insertTime desc")
    List<Object> findAllWithMapResult(@Param("code")String code, @Param("type")Integer type);


    default List<TextDto> findAllTextByDto(String code, Integer type) {
        String sql = "select t.id,t.title,t.insert_time from stk_text t left join stk_xq_post p " +
                     "on t.id=p.id where t.code=? and t.type=? order by t.insert_time desc";
        return BaseService.getInstance().list(sql, TextDto.class, code, type);
    }

    @Query(value = "select t.* from stk_text t left join stk_xq_post p " +
            "on t.id=p.id where t.code=:code and t.type=:type order by t.insert_time desc", nativeQuery = true)
    <T> Collection<T> findAllByCodeAndTypeOrderByInsertTimeDesc(@Param("code")String code, @Param("type")Integer type, Class<T> clazz);

}
