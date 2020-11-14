package com.stk.repository;

import com.stk.model.XqPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XqPostRepository extends JpaRepository<XqPost, Long> {

    List<XqPost> findAllByOrderByInsertDateDesc();

    List<XqPost> findByIsReadOrderByInsertDateDesc(Boolean isRead);

    List<XqPost> findByIsFavoriteOrderByFavoriteDateDesc(Boolean isFavorite);

    // 查找未读的最早的n条记录
    List<XqPost> queryTop5ByIsReadFalseOrderByInsertDateAsc();

}