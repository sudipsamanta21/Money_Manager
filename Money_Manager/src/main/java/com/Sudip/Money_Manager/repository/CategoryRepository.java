package com.Sudip.Money_Manager.repository;

import com.Sudip.Money_Manager.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByProfileEntity_Id(Long profileId);

    Optional<CategoryEntity> findByIdAndProfileEntity_Id(Long id, Long profileId);

    List<CategoryEntity> findByTypeAndProfileEntity_Id(String type, Long profileId);

    Boolean existsByNameAndProfileEntity_Id(String name, Long profileId);
}