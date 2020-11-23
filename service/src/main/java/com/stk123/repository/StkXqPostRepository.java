package com.stk123.repository;

import com.stk123.entity.StkXqPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StkXqPostRepository extends JpaRepository<StkXqPostEntity, Long> {

    List<StkXqPostEntity> findAllByOrderByInsertDateDesc();

    List<StkXqPostEntity> findTop20ByIsReadOrderByInsertDateDesc(Boolean isRead);

    List<StkXqPostEntity> findByIsFavoriteOrderByFavoriteDateDesc(Boolean isFavorite);

    // 查找未读的最早的n条记录
    List<StkXqPostEntity> queryTop5ByIsReadFalseOrderByInsertDateAsc();

}