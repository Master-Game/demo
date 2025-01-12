package com.anjin.ccc.controller;

import com.anjin.ccc.dto.QuoteRequest;
import com.anjin.ccc.dto.QuoteResponse;
import com.anjin.ccc.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}
