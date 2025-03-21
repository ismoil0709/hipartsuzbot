package uz.hiparts.hipartsuz.service.impl;

import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.service.Base64Service;

import java.util.Base64;

@Service
public class Base64ServiceImpl implements Base64Service {

    @Override
    public String base64(String text) {

        if (text.startsWith("/encode")){
            String originalText = text.split("/encode")[1];
            return encode(originalText);
        }
        else if (text.startsWith("/decode")){
            String originalText = text.split("/decode")[1];
            return decode(originalText.trim());
        }

        return "";

    }

    private String encode(String text) {
        byte[] encodedBytes = Base64.getEncoder().encode(text.getBytes());
        return new String(encodedBytes);
    }

    private String decode(String text) {
        byte[] decodedBytes = Base64.getDecoder().decode(text);
        return new String(decodedBytes);
    }


}
