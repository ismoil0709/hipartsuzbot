package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;

@Service
public interface Base64Service {

    String base64(String text);
}