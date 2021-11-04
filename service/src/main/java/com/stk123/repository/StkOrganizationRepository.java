package com.stk123.repository;

import com.stk123.entity.StkOrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StkOrganizationRepository extends JpaRepository<StkOrganizationEntity, Integer> {

    List<StkOrganizationEntity> findAllByNameLike(String name);

}
