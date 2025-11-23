package com.partyguham.common.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class ImageUploader {

    private final String uploadDir = "uploads";

    public String upload(MultipartFile file) {
        validateImage(file);

        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(dir, filename);

            file.transferTo(dest);

            return "/uploads/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return; 

        String contentType = file.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/jpeg") ||
                  contentType.equals("image/png") ||
                  contentType.equals("image/jpg"))) {

            throw new IllegalArgumentException("jpg, jpeg, png 형식의 이미지 파일만 업로드 가능합니다.");
        }
    }
}
