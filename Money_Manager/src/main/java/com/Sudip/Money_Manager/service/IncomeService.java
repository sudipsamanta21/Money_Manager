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

        private final ExcelService excelService;
        private final EmailDownloadService emailDownloadService;

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



        public byte[] downloadIncomeExcel() {
             ProfileEntity profile = profileService.getCurrentUserProfile();
             return excelService.exportIncomeExcel(profile.getId());
        }


        public void emailIncomeExcel() {
        ProfileEntity profile = profileService.getCurrentUserProfile();
        byte[] excel = excelService.exportIncomeExcel(profile.getId());
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>
            <body style="margin:0;padding:0;background:#f4f6f9;font-family:Arial,Helvetica,sans-serif;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="padding:30px 0;">
                    <tr>
                        <td align="center">

                            <table width="600" cellpadding="0" cellspacing="0"
                                   style="background:#ffffff;border-radius:12px;
                                          overflow:hidden;border:1px solid #e5e7eb;">

                                <tr>
                                    <td style="background:#10b981;padding:20px;text-align:center;">
                                        <h2 style="margin:0;color:white;">
                                            💰 Money Manager
                                        </h2>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding:30px;">

                                        <h3 style="margin-top:0;color:#111827;">
                                            Hello, %s 👋
                                        </h3>

                                        <p style="color:#4b5563;font-size:15px;line-height:1.7;">
                                            Your requested <strong>Income Report</strong> has been generated successfully.
                                        </p>

                                        <p style="color:#4b5563;font-size:15px;line-height:1.7;">
                                            Please find the attached Excel file containing your income transactions.
                                        </p>

                                        <table cellpadding="0" cellspacing="0" style="margin:25px 0;">
                                            <tr>
                                                <td style="background:#10b981;border-radius:6px;padding:12px 24px;">
                                                    <span style="color:white;font-weight:bold;text-decoration:none;">
                                                        📄 income_details.xlsx
                                                    </span>
                                                </td>
                                            </tr>
                                        </table>

                                        <hr style="border:none;border-top:1px solid #e5e7eb;margin:25px 0;">

                                        <p style="color:#6b7280;font-size:13px;">
                                            This is an automatically generated email from
                                            <strong>Money Manager</strong>.
                                        </p>

                                        <p style="color:#6b7280;font-size:13px;">
                                            Thank you for using Money Manager ❤️
                                        </p>

                                    </td>
                                </tr>

                            </table>

                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(profile.getFullName());

        emailDownloadService.sendEmailWithAttachment(
                profile.getEmail(),
                "Income Report",
                htmlContent,
                excel,
                "income_details.xlsx"
        );

    }


}

