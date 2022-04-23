package com.dersaun.apicontas.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    
    String upload(MultipartFile file, String filename) throws IOException;

    Object download(String filename) throws IOException;
}
