package com.anjin.ccc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteRequest {
    private String customerType;
    private List<ProductDetail> products;

//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class ProductQuantity {
//        private String productName;
//        private int quantity;
//    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductDetail {
        private String mainProduct;
        private String option;
        private int quantity;
    }
    // 传参示例： mainProduct ： Main Hoist ,   option ： "" 1
    // 传参示例： mainProduct ： Disc brake SB 30-BL550-8 ,   option ： CMB3 execution  2
}
