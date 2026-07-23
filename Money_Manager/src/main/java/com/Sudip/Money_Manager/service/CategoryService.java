package com.Sudip.Money_Manager.service;

import com.Sudip.Money_Manager.dataTransferObject.CategoryDTO;
import com.Sudip.Money_Manager.entity.CategoryEntity;
import com.Sudip.Money_Manager.entity.ProfileEntity;
import com.Sudip.Money_Manager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        ProfileEntity profileEntity= profileService.getCurrentUserProfile();
        if(categoryRepository.existsByNameAndProfileEntity_Id(categoryDTO.getName(), profileEntity.getId())){
                 throw new RuntimeException("Category with this name already exists");
        }
        CategoryEntity newCategoryEntity = toEntity(categoryDTO, profileEntity);
        newCategoryEntity= categoryRepository.save(newCategoryEntity);
        return toDTO(newCategoryEntity);
    }



    // Method to retrieve categories for the current user
    public List<CategoryDTO> getCategoriesForCurrentUser() {
        ProfileEntity profileEntity= profileService.getCurrentUserProfile();
        List<CategoryEntity> categoryEntities = categoryRepository.findByProfileEntity_Id(profileEntity.getId());
        return categoryEntities.stream().map(this::toDTO).toList();
    }

    // Method to retrieve categories by type for the current user
    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        ProfileEntity profileEntity= profileService.getCurrentUserProfile();
        List<CategoryEntity> categoryEntities =
                categoryRepository.findByTypeAndProfileEntity_Id(type, profileEntity.getId());
        return categoryEntities.stream().map(this::toDTO).toList();
    }



    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        ProfileEntity profileEntity= profileService.getCurrentUserProfile();
        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileEntity_Id(categoryId, profileEntity.getId())
                .orElseThrow(() -> new RuntimeException("Category not found or not accessible by the current user"));

        // Update the fields of the existing category
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setType(categoryDTO.getType());
        existingCategory.setIcon(categoryDTO.getIcon());

        // Save the updated category
        CategoryEntity updatedCategory = categoryRepository.save(existingCategory);
        return toDTO(updatedCategory);
    }



    //helper methods to convert between DTO and Entity
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profileEntity) {
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .type(categoryDTO.getType())
                .icon(categoryDTO.getIcon())
                .profileEntity(profileEntity)
                .build();
    }



    private CategoryDTO toDTO(CategoryEntity entity) {
        return CategoryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .icon(entity.getIcon())
                .profileId(entity.getProfileEntity() != null ? entity.getProfileEntity().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }


}