package com.tqt.shawttee.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public String encode(Long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0){
            int remainder = (int) (id % 62);
            sb.append(CHARS.charAt(remainder));
            id = id / 62;
        }
        return sb.reverse().toString();
    }
}

