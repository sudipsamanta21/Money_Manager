package com.Sudip.Money_Manager.service;


import com.Sudip.Money_Manager.dataTransferObject.IncomeDTO;
import com.Sudip.Money_Manager.entity.CategoryEntity;


import com.Sudip.Money_Manager.entity.IncomeEntity;
import com.Sudip.Money_Manager.entity.ProfileEntity;
import com.Sudip.Money_Manager.repository.CategoryRepository;
import com.Sudip.Money_Manager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

        private final CategoryRepository categoryRepository;
        private final IncomeRepository incomeRepository;
        private final ProfileService profileService;

        public IncomeDTO addIncome(IncomeDTO dto){
             ProfileEntity profileEntity = profileService.getCurrentUserProfile();
             CategoryEntity category =categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(()-> new RuntimeException("Category not found"));
             IncomeEntity newExpense = toEntity(dto,profileEntity,category);
             newExpense = incomeRepository.save(newExpense);
             return  toDTO(newExpense);
        }


        public void deleteIncome(Long expenseId){
              ProfileEntity profileEntity = profileService.getCurrentUserProfile();
              IncomeEntity incomeEntity=incomeRepository.findById(expenseId)
                    .orElseThrow(()-> new RuntimeException("Income not found"));

              if(!incomeEntity.getProfileEntity().getId().equals(profileEntity.getId())){
                    throw  new RuntimeException("Unauthorized to delete this income");
              }
        incomeRepository.delete(incomeEntity);
    }



        public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser(){
             ProfileEntity profileEntity = profileService.getCurrentUserProfile();
             LocalDate now = LocalDate.now();
             LocalDate startDate = now.withDayOfMonth(1);
             LocalDate endDate = now .withDayOfMonth(now.lengthOfMonth());
             List<IncomeEntity>list = incomeRepository.findByProfileEntity_IdAndDateBetween(profileEntity.getId(),startDate,endDate);
             return list.stream().map(this::toDTO).toList();
        }


        public List<IncomeDTO> getLatest5IncomeForCurrentUser(){
              ProfileEntity profileEntity= profileService.getCurrentUserProfile();
              List<IncomeEntity> list = incomeRepository.findTop5ByProfileEntity_IdOrderByDateDesc(profileEntity.getId());
              return list.stream().map(this::toDTO).toList();
        }


        public BigDecimal getTotalIncomeForCurrentUser(){
             ProfileEntity profileEntity = profileService.getCurrentUserProfile();
             BigDecimal total = incomeRepository.findTotalIncomeByProfileId(profileEntity.getId());
             return total != null ? total: BigDecimal.ZERO;
        }




    public List<IncomeDTO> filterExpenses(LocalDate startDate,
                                           LocalDate endDate,
                                           String keyword,
                                           Sort sort) {

        ProfileEntity profileEntity = profileService.getCurrentUserProfile();

        List<IncomeEntity> list =
                incomeRepository.findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(
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




        private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity categoryEntity){
            return IncomeEntity.builder()
                    .name(dto.getName())
                    .icon(dto.getIcon())
                    .amount(dto.getAmount())
                    .profileEntity(profile)
                    .category(categoryEntity)
                    .date(dto.getDate())
                    .build();

        }

        private IncomeDTO toDTO(IncomeEntity entity){
            return  IncomeDTO.builder()
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

