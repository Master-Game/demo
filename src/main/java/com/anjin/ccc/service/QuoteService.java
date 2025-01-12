package com.anjin.ccc.service;

import com.anjin.ccc.dto.QuoteRequest;
import com.anjin.ccc.dto.QuoteResponse;
import com.anjin.ccc.tool.ExcelReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.anjin.ccc.tool.ExcelReader.readExcel;

@Service
public class QuoteService {
//    public QuoteResponse calculateQuote(QuoteRequest request) {
//        return new QuoteResponse();
//    }

    private static final String excelFilePath = "C:\\Users\\Fish Leong\\Desktop\\报价系统-数据源-20250102.xlsx";

    @Autowired
    private ExcelReader excelReader;

    public QuoteResponse calculateQuote(QuoteRequest request) {
        List<Map<String, String>> data = readExcel(excelFilePath, 1);
        /**
         * coding
         */
        Map<String, String> cusMap = readCustomerPriceColumnMap();
        String customerType = request.getCustomerType();
        String priceType = cusMap.get(customerType); // 对应price1 or price2
        if (priceType == null) {
            throw new IllegalArgumentException("Invalid customer type: " + customerType);
        }

        BigDecimal totalCost = BigDecimal.ZERO;
        List<QuoteResponse.ProductDetail> responseProducts = new ArrayList<>();

        for (QuoteRequest.ProductDetail productDetail : request.getProducts()) {
            String mainProduct = productDetail.getMainProduct();
            String option = productDetail.getOption();
            int quantity = productDetail.getQuantity();

            // 查找匹配的产品和选项
            Optional<Map<String, String>> matchingProduct = data.stream()
                    .filter(row -> row.get("Main Product").equals(mainProduct) && row.get("Option").equals(option))
                    .findFirst();

            if (matchingProduct.isPresent()) {
                Map<String, String> productRow = matchingProduct.get();
                String priceValue = productRow.get(priceType);

                if (priceValue == null || priceValue.isEmpty()) {
                    throw new IllegalArgumentException("Price value not found for product: " + mainProduct + " with option: " + option);
                }

                BigDecimal price = new BigDecimal(priceValue);
                BigDecimal amount = price.multiply(BigDecimal.valueOf(quantity));
                totalCost = totalCost.add(amount);

                responseProducts.add(new QuoteResponse.ProductDetail(mainProduct, option, quantity));
            } else {
                // 如果没有找到匹配的产品和选项，可以处理异常或记录日志
                System.out.println("No matching product found for: " + mainProduct + " with option: " + option);
            }
        }

        return new QuoteResponse(totalCost, responseProducts);
//        return new QuoteResponse();
    }


//    private static final String EXCEL_FILE_PATH = "C:\\Users\\Fish Leong\\Desktop\\报价系统-数据源-20250102.xlsx";
//
//    public QuoteResponse calculateQuote(QuoteRequest request) {
//        String customerType = request.getCustomerType();
//        List<QuoteRequest.ProductQuantity> products = request.getProducts();
//
//        // 读取客户和价格列标题的关系
//        Map<String, String> customerPriceColumnMap = readCustomerPriceColumnMap();
//        // customerPriceColumnMap:           A客户: Price1   B客户: Price2
//
//        // 获取对应客户的价格列标题
//        String priceColumnTitle = customerPriceColumnMap.get(customerType);
//        if (priceColumnTitle == null) {
//            throw new IllegalArgumentException("客户类型不匹配: " + customerType);
//        }
//
//        // 读取对应价格列中的价格数据
//        Map<String, BigDecimal> priceMap = readPricesFromSheet(1, priceColumnTitle); // 假设价格数据在第二个sheet
//
//        // 计算总价
//        BigDecimal totalCost = BigDecimal.ZERO;
//
//        for (QuoteRequest.ProductQuantity pq : products) {
//            String productName = pq.getProductName();
//            int quantity = pq.getQuantity();
//            BigDecimal price = priceMap.getOrDefault(productName, BigDecimal.ZERO);
//            BigDecimal productTotalCost = price.multiply(BigDecimal.valueOf(quantity));
//            totalCost = totalCost.add(productTotalCost);
//        }
//
//        return new QuoteResponse(totalCost);
//    }
//
    private Map<String, String> readCustomerPriceColumnMap() {
        Map<String, String> customerPriceColumnMap = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(new File(excelFilePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0); // 假设第一行是标题行
            for (Row row : sheet) {
//                if (row.getRowNum() == 0) continue; // 跳过标题行

                Cell customerCell = row.getCell(0);
                Cell priceColumnCell = row.getCell(1);

                if (customerCell != null && priceColumnCell != null) {
                    String customerType = customerCell.getStringCellValue().trim();
                    String priceColumnTitle = priceColumnCell.getStringCellValue().trim();
                    customerPriceColumnMap.put(customerType, priceColumnTitle);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return customerPriceColumnMap;
    }
//
//    private Map<String, BigDecimal> readPricesFromSheet(int sheetIndex, String priceColumnTitle) {
//        Map<String, BigDecimal> priceMap = new HashMap<>();
//
//        try (FileInputStream fis = new FileInputStream(new File(EXCEL_FILE_PATH));
//             Workbook workbook = new XSSFWorkbook(fis)) {
//
//            Sheet sheet = workbook.getSheetAt(sheetIndex);
//            Row headerRow = sheet.getRow(0); // 假设第一行是标题行
//            int priceColumnIndex = -1;
//
//            // 确保标题行中的列标题与 priceColumnTitle 完全匹配
//            for (Cell cell : headerRow) {
//                String cellValue = cell.getStringCellValue().trim();
//                if (cellValue.equalsIgnoreCase(priceColumnTitle)) {
//                    priceColumnIndex = cell.getColumnIndex();
//                    System.out.println("找到价格列索引: " + priceColumnIndex + " (标题: " + priceColumnTitle + ")");
//                    break;
//                }
//            }
//
//            if (priceColumnIndex == -1) {
//                throw new IllegalArgumentException("价格列标题不匹配: " + priceColumnTitle);
//            }
//
//            for (Row row : sheet) {
//                if (row.getRowNum() == 0) continue; // 跳过标题行
//
//                Cell productCell = row.getCell(0);
//                Cell priceCell = row.getCell(priceColumnIndex);
//
//                if (productCell != null && priceCell != null) {
//                    String productName = productCell.getStringCellValue().trim();
//                    BigDecimal price = BigDecimal.ZERO;
//
//                    if (priceCell.getCellType() == CellType.NUMERIC) {
//                        price = BigDecimal.valueOf(priceCell.getNumericCellValue());
//                    } else if (priceCell.getCellType() == CellType.STRING) {
//                        String priceStr = priceCell.getStringCellValue().trim();
//                        priceStr = priceStr.replaceAll("[^\\d.]", "").trim();
//                        try {
//                            price = new BigDecimal(priceStr);
//                        } catch (NumberFormatException e) {
//                            System.err.println("转换失败，产品名称: " + productName + ", 价格: " + priceStr);
//                            continue;
//                        }
//                    }
//
//                    priceMap.put(productName, price);
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return priceMap;
//    }




    public static Map<String, List<String>> getOption(List<String> mainProducts) {
        List<Map<String, String>> data = readExcel(excelFilePath, 1);
        Map<String, List<String>> resMap = new HashMap();
        for (String mainProduct : mainProducts) {
            List<String> options = data.stream()
                    .filter(row -> mainProduct.equals(row.get("Main Product")))
                    .map(row -> row.get("Option"))
                    .filter(option -> option != null && !option.isEmpty())
                    .collect(Collectors.toList());

            resMap.put(mainProduct, options);
        }

        return resMap;
    }

    public static Map<String, List<String>> getDesc(List<String> products) {
        List<Map<String, String>> data = readExcel(excelFilePath, 2);
        Map<String, List<String>> resMap = new HashMap();
        for (String mainProduct : products) {
            List<String> options = data.stream()
                    .filter(row -> mainProduct.equals(row.get("option product")))
                    .map(row -> row.get("Desc"))
                    .filter(option -> option != null && !option.isEmpty())
                    .collect(Collectors.toList());

            resMap.put(mainProduct, options);
        }

        return resMap;
    }


}