package com.Sudip.Money_Manager.service;

import com.Sudip.Money_Manager.dataTransferObject.ExpenseDTO;
import com.Sudip.Money_Manager.entity.CategoryEntity;
import com.Sudip.Money_Manager.entity.ExpenseEntity;
import com.Sudip.Money_Manager.entity.ProfileEntity;
import com.Sudip.Money_Manager.repository.CategoryRepository;
import com.Sudip.Money_Manager.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor

public class ExpenseService {
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;



    public ExpenseDTO addExpense(ExpenseDTO dto){
        ProfileEntity profileEntity = profileService.getCurrentUserProfile();
        CategoryEntity category =categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()-> new RuntimeException("Category not found"));
        ExpenseEntity newExpense = toEntity(dto,profileEntity,category);
        newExpense = expenseRepository.save(newExpense);
        return  toDTO(newExpense);
    }

    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profileEntity = profileService.getCurrentUserProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now .withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity>list = expenseRepository.findByProfileEntity_IdAndDateBetween(profileEntity.getId(),startDate,endDate);
        return list.stream().map(this::toDTO).toList();
    }


    public void deleteExpense(Long expenseId){
        ProfileEntity profileEntity = profileService.getCurrentUserProfile();
        ExpenseEntity expenseEntity =expenseRepository.findById(expenseId)
                .orElseThrow(()-> new RuntimeException("Expense not found"));

        if(!expenseEntity.getProfileEntity().getId().equals(profileEntity.getId())){
            throw  new RuntimeException("Unauthorized to delete this expense");
        }
        expenseRepository.delete(expenseEntity);
    }

    public List<ExpenseDTO> getLatest5ExpenseForCurrentUser(){
        ProfileEntity profileEntity= profileService.getCurrentUserProfile();
        List<ExpenseEntity> list = expenseRepository.findTop5ByProfileEntity_IdOrderByDateDesc(profileEntity.getId());
        return list.stream().map(this::toDTO).toList();
    }


    public BigDecimal getTotalExpenseForCurrentUser(){
        ProfileEntity profileEntity = profileService.getCurrentUserProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profileEntity.getId());
        return total != null ? total: BigDecimal.ZERO;
    }

    public List<ExpenseDTO> filterExpenses(LocalDate startDate,
                                           LocalDate endDate,
                                           String keyword,
                                           Sort sort) {

        ProfileEntity profileEntity = profileService.getCurrentUserProfile();
        List<ExpenseEntity> list =
                expenseRepository.findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(
                        profileEntity.getId(),
                        startDate,
                        endDate,
                        keyword,
                        sort
                );

        return list.stream()
                .map(this::toDTO)
                .toList();
    }




    //Notification
//    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId, LocalDate date){
//        List<ExpenseEntity> list = expenseRepository.findByProfileEntity_IdAndDate(profileId,date);
//        return list.stream().map(this::toDTO).toList();
//    }



    @Transactional(readOnly = true)
    public List<ExpenseDTO> findByProfileEntity_IdAndDateWithCategory(Long profileId, LocalDate date) {

        List<ExpenseEntity> list =
                expenseRepository.findByProfileEntity_IdAndDateWithCategory(profileId, date);

        return list.stream()
                .map(this::toDTO)
                .toList();
    }

    private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile, CategoryEntity categoryEntity){
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .profileEntity(profile)
                .category(categoryEntity)
                .date(dto.getDate())
                .build();

    }

    private ExpenseDTO toDTO(ExpenseEntity entity){
       return  ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId():null)
                .categoryName(entity.getCategory() !=null ? entity.getCategory().getName():"N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();


    }
}
