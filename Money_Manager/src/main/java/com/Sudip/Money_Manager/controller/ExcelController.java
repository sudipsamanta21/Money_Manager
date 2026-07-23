package com.Sudip.Money_Manager.controller;

import com.Sudip.Money_Manager.service.ExpenseService;
import com.Sudip.Money_Manager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
@CrossOrigin(origins = "${money.manager.frontend.url}", allowCredentials = "true")
public class ExcelController {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @GetMapping("/download/income")
    public ResponseEntity<byte[]> downloadIncomeExcel() {

        byte[] excel = incomeService.downloadIncomeExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Income_Report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    @PostMapping("/email/download/income")
    public ResponseEntity<String> emailIncomeExcel() {

        incomeService.emailIncomeExcel();

        return ResponseEntity.ok("Income report sent successfully.");
    }

    @GetMapping("/download/expense")
    public ResponseEntity<byte[]> downloadExpenseExcel() {

        byte[] excel = expenseService.downloadExpenseExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Expense_Report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    @PostMapping("/email/download/expense")
    public ResponseEntity<String> emailExpenseExcel() {

        expenseService.emailExpenseExcel();

        return ResponseEntity.ok("Expense report sent successfully.");
    }
}