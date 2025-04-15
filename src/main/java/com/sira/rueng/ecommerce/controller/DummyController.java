package com.sira.rueng.ecommerce.controller;

import com.sira.rueng.ecommerce.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLOutput;
import java.util.Map;

@RestController
@RequestMapping("/api/dummy")
public class DummyController {


    @GetMapping("/testJenkins")
    public String testJenkins() {
        return "THis is jenkins";
    }

}
