package com.anjin.ccc.controller;

import com.anjin.ccc.dto.QuoteRequest;
import com.anjin.ccc.dto.QuoteResponse;
import com.anjin.ccc.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class QuoteController {
    @Autowired
    private QuoteService quoteService;

    @PostMapping("/quote")
    public ResponseEntity<QuoteResponse> getQuote(@RequestBody QuoteRequest request) {
        QuoteResponse response = quoteService.calculateQuote(request);
        return ResponseEntity.ok(response);
    }


    @RequestMapping("/ok111")
    public String ok() {
        return "ok111";
    }


    @PostMapping("/option")
    public ResponseEntity getOption(@RequestBody List<String> mainProducts) {
        Map<String, List<String>> response = quoteService.getOption(mainProducts);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/desc")
    public ResponseEntity getDesc(@RequestBody List<String> products) {
        Map<String, List<String>> response = quoteService.getDesc(products);
        return ResponseEntity.ok(response);
    }

}
