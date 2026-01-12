package com.tqt.shawttee.service;

import com.tqt.shawttee.entity.Url;
import com.tqt.shawttee.entity.User;
import com.tqt.shawttee.repository.UrlRepository;
import com.tqt.shawttee.repository.UserRepository;
import com.tqt.shawttee.util.Base62Encoder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private Base62Encoder base62Encoder;

    @Transactional
    public Url shortenUrl(String originalUrl, User owner){
        if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
            originalUrl = "https://" + originalUrl;
        }
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setOwner(owner);
        url.setExpiresAt(LocalDateTime.now().plusDays(30));
        urlRepository.save(url);
        url.setShortCode(base62Encoder.encode(url.getId()));
        urlRepository.save(url);
        return url;
    }

    public String getOriginalUrl(String shortCode) throws RuntimeException {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Url not found"));
        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Url Expired");
        }
        return url.getOriginalUrl();
    }

    public List<Url> getUserUrls(User user) {
        return urlRepository.findByOwner(user);
    }
}
