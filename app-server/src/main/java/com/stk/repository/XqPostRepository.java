package com.stk.repository;

import com.stk.model.XqPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XqPostRepository extends JpaRepository<XqPost, Long> {


}