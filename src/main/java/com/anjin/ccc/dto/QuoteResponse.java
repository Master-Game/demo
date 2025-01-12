package com.anjin.ccc.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuoteResponse {
    private BigDecimal totalCost;
    private List<ProductDetail> products;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductDetail {
        private String mainProduct;
        private String option;
        private int quantity;
    }

}
