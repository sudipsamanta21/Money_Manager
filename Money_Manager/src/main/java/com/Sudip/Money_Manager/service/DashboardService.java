package com.Sudip.Money_Manager.service;


import com.Sudip.Money_Manager.dataTransferObject.ExpenseDTO;
import com.Sudip.Money_Manager.dataTransferObject.IncomeDTO;
import com.Sudip.Money_Manager.dataTransferObject.RecentTransactionDTO;
import com.Sudip.Money_Manager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardService {


    private final IncomeService incomeService;
    private final ProfileService profileService;
    private final ExpenseService expenseService;


    public Map<String,Object> getDashboardData() {
        ProfileEntity profileEntity = profileService.getCurrentUserProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();
         List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomeForCurrentUser();
         List<ExpenseDTO> latestExpenses= expenseService.getLatest5ExpenseForCurrentUser();

         List<RecentTransactionDTO>recentTransactions = concat(latestIncomes.stream().map(incomeDTO ->
                 RecentTransactionDTO.builder()
                         .id(incomeDTO.getId())
                         .profileId(profileEntity.getId())
                         .icon(incomeDTO.getIcon())
                         .name(incomeDTO.getName())
                         .amount(incomeDTO.getAmount())
                         .date(incomeDTO.getDate())
                         .createdAt(incomeDTO.getCreatedAt())
                         .updatedAt(incomeDTO.getUpdatedAt())
                         .type("income")
                         .build()),
                 latestExpenses.stream().map(expenseDTO ->
                      RecentTransactionDTO.builder()
                                 .id(expenseDTO.getId())
                                 .profileId(profileEntity.getId())
                                 .icon(expenseDTO.getIcon())
                                 .name(expenseDTO.getName())
                                 .amount(expenseDTO.getAmount())
                                 .date(expenseDTO.getDate())
                                 .createdAt(expenseDTO.getCreatedAt())
                                 .updatedAt(expenseDTO.getUpdatedAt())
                                 .type("expense")
                                 .build()) )

                 .sorted((a,b)->{
                     int compare =b.getDate().compareTo(a.getDate());
                     if(compare == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null){
                         return b.getCreatedAt().compareTo(a.getCreatedAt());
                     }
                     return compare;
                 }).collect(Collectors.toList());
        returnValue.put("totalBalance" ,incomeService.getTotalIncomeForCurrentUser()
                .subtract(expenseService.getTotalExpenseForCurrentUser()));
        returnValue.put("totalIncome", incomeService.getTotalIncomeForCurrentUser());
        returnValue.put("totalExpense", expenseService.getTotalExpenseForCurrentUser());
        returnValue.put("recent5Expenses", latestExpenses);
        returnValue.put("recent5Incomes", latestIncomes);
        returnValue.put("recentTransactions", recentTransactions);
        return  returnValue;
    }
}
