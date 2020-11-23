package com.stk123.repository;

import com.stk123.entity.StkTextEntity;
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
public interface StkTextRepository extends JpaRepository<StkTextEntity, Integer> {

    List<StkTextEntity> findAllByCodeAndTypeOrderByInsertTimeDesc(String code, Integer type);

    //转化为 Map
    default Map<Integer, StkTextEntity> findAllMap(String code, Integer type) {
        return findAllByCodeAndTypeOrderByInsertTimeDesc(code, type)
                .stream().collect(Collectors.toMap(StkTextEntity::getId, v -> v));
    }

    //https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections
    //https://www.baeldung.com/jpa-queries-custom-result-with-aggregation-functions

    @Query(value = "select p.* from stk_text t left join stk_xq_post p " +
            "on t.id=p.id where t.code=:code and t.type=:type order by t.insert_time desc", nativeQuery = true)
    <T> Collection<T> findAllByCodeAndTypeOrderByInsertTimeDesc(@Param("code")String code, @Param("type")Integer type, Class<T> clazz);

    default List<Post> findAllPost() {
        BaseService baseService = new BaseService();
        return baseService.list("", Post.class);
    }


    @Query("select t from StkTextEntity t left join StkXqPostEntity p " +
            "on t.id=p.id where t.code=:code and t.type=:type order by t.insert_time desc")
    List<Map<String, Object>> findAllWithMapResult(@Param("code")String code, @Param("type")Integer type);

}
