package com.Sudip.Money_Manager.service;

import com.Sudip.Money_Manager.entity.ExpenseEntity;
import com.Sudip.Money_Manager.entity.IncomeEntity;
import com.Sudip.Money_Manager.repository.ExpenseRepository;
import com.Sudip.Money_Manager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    public byte[] exportIncomeExcel(Long profileId) {

        List<IncomeEntity> incomes =
                incomeRepository.findByProfileEntity_IdOrderByDateDesc(profileId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Income Report");

            // Header Style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Amount Style
            CellStyle amountStyle = workbook.createCellStyle();
            amountStyle.setDataFormat(
                    workbook.createDataFormat().getFormat("#,##0.00")
            );

            // Header Row
            Row header = sheet.createRow(0);
            String[] columns = {
                    "Name",
                    "Category",
                    "Amount",
                    "Date"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            int rowNum = 1;

            for (IncomeEntity income : incomes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(income.getName());
                row.createCell(1).setCellValue(
                        income.getCategory() != null
                                ? income.getCategory().getName()
                                : "N/A"
                );

                Cell amountCell = row.createCell(2);
                amountCell.setCellValue(income.getAmount().doubleValue());
                amountCell.setCellStyle(amountStyle);
                row.createCell(3).setCellValue(
                        income.getDate().format(formatter)
                );
            }

            // Auto Size Columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Income Excel.", e);
        }
    }





    public byte[] exportExpenseExcel(Long profileId) {

        List<ExpenseEntity> expenses =
                expenseRepository.findByProfileEntity_IdOrderByDateDesc(profileId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Expense Report");

            // Header Style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Amount Style
            CellStyle amountStyle = workbook.createCellStyle();
            amountStyle.setDataFormat(
                    workbook.createDataFormat().getFormat("#,##0.00")
            );

            // Header Row
            Row header = sheet.createRow(0);

            String[] columns = {
                    "Name",
                    "Category",
                    "Amount",
                    "Date"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            int rowNum = 1;

            for (ExpenseEntity expense : expenses) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(expense.getName());
                row.createCell(1).setCellValue(
                        expense.getCategory() != null
                                ? expense.getCategory().getName()
                                : "N/A"
                );

                Cell amountCell = row.createCell(2);
                amountCell.setCellValue(expense.getAmount().doubleValue());
                amountCell.setCellStyle(amountStyle);
                row.createCell(3).setCellValue(
                        expense.getDate().format(formatter)
                );
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Expense Excel.", e);
        }
    }

}