package com.stk123.repository;

import com.stk123.entity.StkTextEntity;
import com.stk123.model.text.TextDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 */

@Repository
public interface StkTextRepository extends JpaRepository<StkTextEntity, Long> {

    List<StkTextEntity> findAllByCodeAndTypeOrderByInsertTimeDesc(String code, Integer type);

    List<StkTextEntity> queryTop5ByReadDateNullOrderByInsertTimeAsc();

    List<StkTextEntity> findTop20ByReadDateNotNullOrderByInsertTimeDesc();

    List<StkTextEntity> findAllByFavoriteDateNotNullOrderByFavoriteDateDesc();

    @Query(value = "select count(1) from stk_text where code=:code and post_id=:postId", nativeQuery = true)
    Integer existingByCodeAndPostId(@Param("code")String code, @Param("postId")Long postId);


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


    final String sql_findAllTextByDto = "select * from stk_text t left join stk_xq_post p " +
            "on t.id=p.id where t.code=? and t.type=? order by t.insert_time desc";
    default List<TextDto> findAllByCodeAndTypeOrderByInsertTimeDesc2Dto(String code, Integer type) {
        return BaseRepository.getInstance().list(sql_findAllTextByDto, TextDto.class, code, type);
    }

//    @Query(value = "select t.* from stk_text t left join stk_xq_post p " +
//            "on t.id=p.id where t.code=:code and t.type=:type order by t.insert_time desc", nativeQuery = true)
//    <T> Collection<T> findAllByCodeAndTypeOrderByInsertTimeDesc(@Param("code")String code, @Param("type")Integer type, Class<T> clazz);

}
