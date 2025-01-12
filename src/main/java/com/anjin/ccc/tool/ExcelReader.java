package com.anjin.ccc.tool;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExcelReader {
    private static final String excelFilePath = "C:\\Users\\Fish Leong\\Desktop\\报价系统-数据源-20250102.xlsx";

//    public static void main(String[] args) {
//        List<Map<String, String>> data = readExcel(excelFilePath, 1); // 读取第二个sheet，索引为1
//        for (Map<String, String> row : data) {
//            System.out.println(row);
//        }
//    }

    public static List<Map<String, String>> readExcel(String filePath, int sheetIndex) {
        List<Map<String, String>> data = new ArrayList<>();
        String lastNonEmptyFirstColumnValue = ""; // 用于存储上一条非空的第一列值
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            Row headerRow = sheet.getRow(0); // 获取第一行作为表头
            if (headerRow == null) {
                System.out.println("表头行为空，无法读取数据。");
                return data;
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 从第二行开始读取数据
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue; // 如果行为空，跳过该行
                }
                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    Cell headerCell = headerRow.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String headerValue = getCellValue(headerCell);
                    String cellValue = getCellValue(cell);
                    if (j == 0 && cellValue.isEmpty()) {
                        cellValue = lastNonEmptyFirstColumnValue; // 如果第一列为空，使用上一条非空的第一列值
                    } else if (j == 0) {
                        lastNonEmptyFirstColumnValue = cellValue; // 更新上一条非空的第一列值
                    }
                    rowData.put(headerValue, cellValue);
                }
                data.add(rowData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return ""; // 如果单元格为空，返回空字符串
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // 返回公式的结果，而不是公式本身
                if (cell.getCachedFormulaResultType() == CellType.NUMERIC) {
                    return String.valueOf(cell.getNumericCellValue());
                } else if (cell.getCachedFormulaResultType() == CellType.STRING) {
                    return cell.getStringCellValue();
                } else {
                    return cell.getCellFormula();
                }
            default:
                return "";
        }
    }
}