package com.Sudip.Money_Manager.repository;

import com.Sudip.Money_Manager.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findByProfileEntity_IdOrderByDateDesc(Long profileId);

    List<ExpenseEntity> findTop5ByProfileEntity_IdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profileEntity.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    List<ExpenseEntity> findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    List<ExpenseEntity> findByProfileEntity_IdAndDateBetween(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate
    );

//    List<ExpenseEntity> findByProfileEntity_IdAndDate(Long profileId, LocalDate date);





    @Query("""
    SELECT e
    FROM ExpenseEntity e
    JOIN FETCH e.category
    WHERE e.profileEntity.id = :profileId
      AND e.date = :date
    
    """)
    List<ExpenseEntity> findByProfileEntity_IdAndDateWithCategory(
            @Param("profileId") Long profileId,
            @Param("date") LocalDate date);
}