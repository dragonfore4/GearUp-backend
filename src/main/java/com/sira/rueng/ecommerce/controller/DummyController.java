package com.sira.rueng.ecommerce.controller;

import com.sira.rueng.ecommerce.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/dummy")
public class DummyController {

    private CloudinaryService cloudinaryService;

    public DummyController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map> uploadFromUrl(@RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            System.out.println(image.getOriginalFilename());
//            Map result = cloudinaryService.uploadImage(image);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
