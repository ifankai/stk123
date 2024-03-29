package com.stk123.repository;

import com.stk123.entity.StkTextEntity;
import com.stk123.model.dto.TextDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 * https://blog.csdn.net/xiang__liu/article/details/80900817
 */

@Repository
public interface StkTextRepository extends JpaRepository<StkTextEntity, Long> {

    //all
    List<StkTextEntity> findAllByInsertTimeGreaterThanEqual(Date date);
    List<StkTextEntity> findAllByInsertTimeGreaterThanEqualOrderByInsertTimeDescIdDesc(Date date, Pageable pageable);
    List<StkTextEntity> findAllByIdGreaterThanOrderByInsertTimeDescIdDesc(Long id);
    List<StkTextEntity> findAllByIdLessThanOrderByInsertTimeDescIdDesc(Long id, Pageable pageable);

    //favorite
    List<StkTextEntity> findAllByFavoriteDateNotNullAndInsertTimeGreaterThanEqualOrderByInsertTimeDescIdDesc(Date date, Pageable pageable);
    List<StkTextEntity> findAllByFavoriteDateNotNullAndIdGreaterThanOrderByInsertTimeDescIdDesc(Long id);
    List<StkTextEntity> findAllByFavoriteDateNotNullAndIdLessThanOrderByInsertTimeDescIdDesc(Long id, Pageable pageable);

    List<StkTextEntity> findAllByCodeOrderByInsertTimeDesc(String code, Pageable pageable);

    List<StkTextEntity> findAllByCodeAndCreatedAtGreaterThanOrderByInsertTimeDesc(String code, Date date);

    List<StkTextEntity> findAllByCodeAndInsertTimeGreaterThanOrderByInsertTimeDesc(String code, Date date);

    List<StkTextEntity> findAllByCodeAndTypeAndInsertTimeBetweenOrderByInsertTimeDesc(String code, Integer type, Date dateStart, Date dateEnd);

    List<StkTextEntity> findAllByCodeAndTypeAndSubTypeAndInsertTimeBetweenOrderByInsertTimeDesc(String code, Integer type, Integer subType, Date dateStart, Date dateEnd);

    List<StkTextEntity> findAllByCodeAndInsertTimeLessThanOrderByInsertTimeDesc(String code, Date date, Pageable pageable);

    List<StkTextEntity> findAllByCodeAndTypeOrderByInsertTimeDesc(String code, Integer type);

    List<StkTextEntity> queryTop5ByTypeAndReadDateNullAndInsertTimeGreaterThanOrderByInsertTimeAsc(Integer type, Date createdAt);

    Integer countByTypeAndReadDateNullAndInsertTimeGreaterThan(Integer type, Date createdAt);

    List<StkTextEntity> findTop20ByTypeAndReadDateNotNullOrderByInsertTimeDesc(Integer type);

    List<StkTextEntity> findAllByTypeAndFavoriteDateNotNullOrderByFavoriteDateDesc(Integer type);

    List<StkTextEntity> findAllByIdIn(List ids);

    @Query(value = "select count(1) from stk_text where post_id=:postId", nativeQuery = true)
    Integer existingByPostId(@Param("postId")Long postId);

    StkTextEntity findByCodeAndPostId(String code, Long postId);

    boolean existsByCodeAndPostId(String code, Long postId);

    Integer countAllByCodeAndCreatedAtGreaterThanEqual(String code, Date createAt);

    @Modifying
    @Query(value = "update stk_text set read_date = sysdate where id in (:id)", nativeQuery = true)
    void updateAll2Readed(@Param("id") List<Long> ids);

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
            "on t.id=p.id where t.code=:1 and t.type=:2 order by t.insert_time desc";
    default List<TextDto> findAllByCodeAndTypeOrderByInsertTimeDesc2Dto(String code, Integer type) {
        return BaseRepository.getInstance().list(sql_findAllTextByDto, TextDto.class, code, type);
    }

//    @Query(value = "select t.* from stk_text t left join stk_xq_post p " +
//            "on t.id=p.id where t.code=:code and t.type=:type order by t.insert_time desc", nativeQuery = true)
//    <T> Collection<T> findAllByCodeAndTypeOrderByInsertTimeDesc(@Param("code")String code, @Param("type")Integer type, Class<T> clazz);

    List<StkTextEntity> findAllByCreatedAtGreaterThanAndTextLikeOrderByCreatedAtDesc(Date date, String text, Pageable pageable);

    List<StkTextEntity> findAllByTypeAndCodeTypeAndSubTypeAndReplyPositiveAndInsertTimeGreaterThanOrderByInsertTimeDesc(Integer type, Integer codeType, Integer subType, Integer replyPositive, Date createdAt);

    StkTextEntity findFirstBySubTypeAndTitle(Integer subType, String title);

}
