package com.stk123.repository;

import com.stk123.entity.StkDictionaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public interface StkDictionaryRepository extends JpaRepository<StkDictionaryEntity, StkDictionaryEntity.CompositeKey> {

    List<StkDictionaryEntity> findAllByType(Integer type);

    default Map<String, StkDictionaryEntity> getMapByType(Integer type){
        List<StkDictionaryEntity> entities = findAllByType(type);
        return entities.stream().collect(Collectors.toMap(StkDictionaryEntity::getKey, Function.identity()));
    }
}
