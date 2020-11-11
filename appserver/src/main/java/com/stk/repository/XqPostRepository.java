package com.stk.repository;

import com.stk.model.XqPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XqPostRepository extends JpaRepository<XqPost, Long> {

    List<XqPost> findAllOrderByInsertDateDesc();

    List<XqPost> findByIsReadOrderByInsertDateDesc(Boolean isRead);

    List<XqPost> findByIsFavoriteOrderByFavoriteDateDesc(Boolean isFavorite);

}