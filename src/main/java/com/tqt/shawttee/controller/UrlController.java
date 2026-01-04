package com.tqt.shawttee.controller;

import com.tqt.shawttee.dto.ShortenRequest;
import com.tqt.shawttee.entity.Url;
import com.tqt.shawttee.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/urls")
public class UrlController {
    @Autowired
    private UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<Url> shortenUrl(@RequestBody ShortenRequest shortenRequest) {
        return ResponseEntity.ok(urlService.shortenUrl(shortenRequest.getOriginalUrl(),null));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> getOriginalUrl(@PathVariable("shortCode") String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
